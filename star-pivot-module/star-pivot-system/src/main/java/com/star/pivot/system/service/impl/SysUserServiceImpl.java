package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.AssertUtils;
import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.AssignUserReqBo;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.assembler.UserVOAssembler;
import com.star.pivot.system.service.ImportExportService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.UserPermissionCacheService;
import com.star.pivot.system.utils.DataScopeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

/**
 * 用户信息表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 17:28:24
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private UserPermissionCacheService userPermissionCacheService;
    @Autowired
    private DataScopeService dataScopeService;
    @Autowired
    private UserVOAssembler userVOAssembler;

    /** 自身代理，用于 importData/importUsers 等场景经代理调用 @Transactional 方法，避免自调用导致事务不生效 */
    @Lazy
    @Autowired
    private SysUserService self;

    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserVO> pageList(UserReqBo userReqBo) {
        Page<SysUser> page = new Page<>(userReqBo.getPageNum(), userReqBo.getPageSize());
        Map<String, Object> param = buildDataScopeParam();
        param.put("userReqBo", userReqBo);
        IPage<SysUser> pageList = sysUserMapper.selectPageList(page, param);
        List<UserVO> voList = userVOAssembler.convertToVOList(pageList.getRecords());
        return toPageResponse(pageList, pageList.getCurrent(), pageList.getSize(), voList);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysRole> getRolesByUserId(Long userId) {
        return sysUserMapper.getRolesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUser getUserWithRoles(Long userId) {
        // 使用 LEFT JOIN 一次性查询用户及其角色信息，避免 N+1 查询问题
        return sysUserMapper.selectUserWithRoles(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysMenu> getMenuByUserId(Long userId) {
        return sysUserMapper.getMenuByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean addUser(UserDTO userDTO) {
        AssertUtils.isNull(getUserByUsername(userDTO.getUserName()), ErrorCode.USER_USERNAME_EXISTS);
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);
        sysUser.setUserType("00");
        sysUser.setStatus(StringUtils.hasText(userDTO.getStatus()) ? userDTO.getStatus() : AppConstants.Status.NORMAL);
        sysUser.setDelFlag(AppConstants.DelFlag.NORMAL);

        // 密码加密  如前端传密码，则进行加密处理，没有传，则使用默认密码123456
        if (StringUtils.hasText(userDTO.getPassword())) {
            sysUser.setPassword(SecurityUtils.encryptPassword(userDTO.getPassword()));
        } else {
            sysUser.setPassword(SecurityUtils.encryptPassword("123456"));
        }
        //创建人 当前登录人
        String currentUser = SecurityContextUtils.getUsername();
        sysUser.setCreateBy(currentUser);
        sysUser.setCreateTime(LocalDateTime.now());

        boolean success = this.save(sysUser);
        //用户添加成功后，添加角色关联信息、岗位关联信息
        if (success && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            // 分配角色
            insertUserRoles(sysUser.getUserId(), userDTO.getRoleIds());
        }
        if (success && userDTO.getPostIds() != null && !userDTO.getPostIds().isEmpty()) {
            // 分配岗位
            insertUserPosts(sysUser.getUserId(), userDTO.getPostIds());
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO selectByUserId(Long userId) {
        SysUser user = this.getById(userId);
        return user == null ? null : userVOAssembler.convertToVO(user);
    }
    /*
    * 修改用户信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        SysUser user = this.getById(userDTO.getUserId());
        AssertUtils.notNull(user, ErrorCode.USER_NOT_FOUND);
        if (AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        SysUser existUser = getUserByUsername(userDTO.getUserName());
        if (existUser != null && !existUser.getUserId().equals(userDTO.getUserId())) {
            throw new BizException(ErrorCode.USER_USERNAME_USED);
        }

        // 更新用户信息
        BeanUtils.copyProperties(userDTO, user, "password", "userId");
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(user);

        if (success) {
            // 更新角色关联
            if (userDTO.getRoleIds() != null) {
                // 删除旧的角色关联
                LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.eq(UserRole::getUserId, userDTO.getUserId());
                userRoleMapper.delete(roleWrapper);

                // 添加新的角色关联
                if (!userDTO.getRoleIds().isEmpty()) {
                    insertUserRoles(userDTO.getUserId(), userDTO.getRoleIds());
                }
                
                // 清除用户权限缓存（角色变更会影响权限）
                userPermissionCacheService.clearUserPermissionCache(userDTO.getUserName());
            }

            // 更新岗位关联
            if (userDTO.getPostIds() != null) {
                // 删除旧的岗位关联
                LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
                postWrapper.eq(UserPost::getUserId, userDTO.getUserId());
                userPostMapper.delete(postWrapper);

                // 添加新的岗位关联
                if (!userDTO.getPostIds().isEmpty()) {
                    insertUserPosts(userDTO.getUserId(), userDTO.getPostIds());
                }
            }
        }

        return success;
    }

    @Override
    public boolean changeUserStatus(Long userId, String status) {
        SysUser user = this.getById(userId);
        AssertUtils.notNull(user, ErrorCode.USER_NOT_FOUND);
        if (AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    public boolean resetUserPassword(Long userId, String password) {
        SysUser user = this.getById(userId);
        AssertUtils.notNull(user, ErrorCode.USER_NOT_FOUND);
        if (AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setPwdUpdateDate(LocalDateTime.now());
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean deleteUserByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }
        
        // 批量查询用户信息
        List<SysUser> userList = this.listByIds(userIds);
        if (userList.isEmpty()) {
            return false;
        }
        
        String currentUser = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        
        // 更新用户状态
        userList.forEach(user -> {
            if (!AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
                user.setDelFlag(AppConstants.DelFlag.DELETE);
                user.setUpdateBy(currentUser);
                user.setUpdateTime(now);
            }
        });
        
        // 批量更新
        this.updateBatchById(userList);
        return true;
    }

    /**
     * 根据角色ID查询用户列表
     */
    @Override
    public PageResponse<SysUser> getUserListByRoleId(AssignUserReqBo assignUserReqBo) {
        return queryPageWithDataScope(assignUserReqBo, sysUserMapper::getUserListByRoleId);
    }

    @Override
    public PageResponse<SysUser> unallocatedList(AssignUserReqBo assignUserReqBo) {
        return queryPageWithDataScope(assignUserReqBo, sysUserMapper::unallocatedList);
    }

    /** 根据当前用户数据权限构建查询参数（dataScope、deptIds、userDeptId、userId） */
    private Map<String, Object> buildDataScopeParam() {
        DataScope dataScope = dataScopeService.getCurrentUserDataScope();
        Map<String, Object> param = new HashMap<>();
        param.put("dataScope", dataScope);
        param.put("deptIds", dataScope.getDeptIds());
        param.put("userDeptId", dataScope.getUserDeptId());
        param.put("userId", dataScope.getUserId());
        return param;
    }

    /** 数据权限分页：构建 param + 分页查询 + 封装 PageResponse，供 getUserListByRoleId、unallocatedList 复用 */
    private PageResponse<SysUser> queryPageWithDataScope(AssignUserReqBo bo,
                                                         BiFunction<Page<SysUser>, Map<String, Object>, IPage<SysUser>> query) {
        Map<String, Object> param = buildDataScopeParam();
        param.put("assignUserReqBo", bo);
        Page<SysUser> page = new Page<>(bo.getPageNum(), bo.getPageSize());
        IPage<SysUser> result = query.apply(page, param);
        return toPageResponse(result, bo.getPageNum().longValue(), bo.getPageSize().longValue(), result.getRecords());
    }

    private <T> PageResponse<T> toPageResponse(IPage<?> ipage, long pageNum, long pageSize, List<T> rows) {
        PageResponse<T> resp = new PageResponse<>();
        resp.setTotal(ipage.getTotal());
        resp.setRows(rows);
        resp.setPageNum(ipage.getCurrent());
        resp.setPageSize(ipage.getSize());
        resp.setPageCount(ipage.getPages());
        return resp;
    }

    /**
     * 批量导入用户（Excel 导入）
     * <p>
     * 说明：
     * - 前端会将 Excel 解析为 List&lt;Map&lt;String, Object&gt;&gt; 传入
     * - key 为表头名称，约定如下（可根据实际模板调整）：
     *   用户账号、用户昵称、邮箱、手机号、性别、状态、部门ID、备注
     * - 性别：男 / 女 / 其它 -> 0 / 1 / 2
     * - 状态：正常 / 停用 -> 0 / 1
     *
     * @param rowList Excel 解析后的行数据列表
     * @return 成功导入的用户数量
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public int importUsers(List<Map<String, Object>> rowList) {
        AssertUtils.notEmpty(rowList, ErrorCode.USER_IMPORT_EMPTY);
        int successCount = 0;
        int rowIndex = 1; // 用于错误提示（从 1 开始，方便与 Excel 行号对应）

        for (Map<String, Object> row : rowList) {
            try {
                // 从 Excel 行数据构建 UserDTO
                UserDTO userDTO = buildUserDTOFromRow(row, rowIndex);

                // 复用已有新增用户逻辑（经 self 代理调用，确保 @Transactional 生效）
                boolean success = self.addUser(userDTO);
                if (success) {
                    successCount++;
                }
            } catch (BizException e) {
                // 业务异常直接抛出，让前端看到明确提示
                throw e;
            } catch (Exception e) {
                // 其他异常统一包装为业务异常，带上行号
                throw new BizException("第 " + rowIndex + " 行导入失败：" + e.getMessage());
            } finally {
                rowIndex++;
            }
        }

        return successCount;
    }

    /**
     * 将 Excel 行数据转换为 UserDTO
     *
     * @param row      当前行数据
     * @param rowIndex 行号（用于错误提示）
     * @return 组装后的 UserDTO
     */
    private UserDTO buildUserDTOFromRow(Map<String, Object> row, int rowIndex) {
        if (row == null || row.isEmpty()) {
            throw new BizException(ErrorCode.USER_IMPORT_ROW_EMPTY, "第 " + rowIndex + " 行数据为空");
        }

        UserDTO userDTO = new UserDTO();

        String userName = getStringCell(row, "用户账号");
        if (!StringUtils.hasText(userName)) {
            throw new BizException(ErrorCode.USER_IMPORT_USERNAME_EMPTY, "第 " + rowIndex + " 行用户账号不能为空");
        }
        userDTO.setUserName(userName.trim());

        String nickName = getStringCell(row, "用户昵称");
        if (!StringUtils.hasText(nickName)) {
            throw new BizException(ErrorCode.USER_IMPORT_NICKNAME_EMPTY, "第 " + rowIndex + " 行用户昵称不能为空");
        }
        userDTO.setNickName(nickName.trim());

        // 邮箱（可选）
        String email = getStringCell(row, "邮箱");
        if (StringUtils.hasText(email)) {
            userDTO.setEmail(email.trim());
        }

        // 手机号（可选）
        String phoneNumber = getStringCell(row, "手机号");
        if (StringUtils.hasText(phoneNumber)) {
            userDTO.setPhonenumber(phoneNumber.trim());
        }

        // 性别（0男 1女 2未知）
        String sexText = getStringCell(row, "性别");
        String sexCode = "2";
        if (StringUtils.hasText(sexText)) {
            sexText = sexText.trim();
            if ("男".equals(sexText)) {
                sexCode = "0";
            } else if ("女".equals(sexText)) {
                sexCode = "1";
            }
        }
        userDTO.setSex(sexCode);

        // 状态（0正常 1停用），默认为正常
        String statusText = getStringCell(row, "状态");
        String statusCode = AppConstants.Status.NORMAL;
        if (StringUtils.hasText(statusText)) {
            statusText = statusText.trim();
            if ("停用".equals(statusText) || "禁用".equals(statusText)) {
                statusCode = AppConstants.Status.DISABLE;
            }
        }
        userDTO.setStatus(statusCode);

        String deptIdText = getStringCell(row, "部门ID");
        if (StringUtils.hasText(deptIdText)) {
            try {
                userDTO.setDeptId(Long.parseLong(deptIdText.trim()));
            } catch (NumberFormatException e) {
                throw new BizException(ErrorCode.USER_IMPORT_DEPT_FORMAT_ERROR, "第 " + rowIndex + " 行部门ID格式不正确，应为数字");
            }
        }

        // 备注（可选）
        String remark = getStringCell(row, "备注");
        if (StringUtils.hasText(remark)) {
            userDTO.setRemark(remark.trim());
        }

        // 密码不从 Excel 读取，使用系统默认密码逻辑（addUser 中处理）
        return userDTO;
    }

    /**
     * 从行数据中安全获取字符串单元格
     *
     * @param row 当前行 Map
     * @param key 表头名称
     * @return 字符串值（若为 null 则返回空字符串）
     */
    private String getStringCell(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * 插入用户角色关联（批量）
     */
    private void insertUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        // 构建用户角色关联集合，使用批量插入提升性能
        List<UserRole> userRoles = new ArrayList<>(roleIds.size());
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        userRoleMapper.insertBatchUserRoles(userRoles);
    }

    /**
     * 插入用户岗位关联（批量）
     */
    private void insertUserPosts(Long userId, List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        // 构建用户岗位关联集合，使用批量插入提升性能
        List<UserPost> userPosts = new ArrayList<>(postIds.size());
        for (Long postId : postIds) {
            UserPost userPost = new UserPost();
            userPost.setUserId(userId);
            userPost.setPostId(postId);
            userPosts.add(userPost);
        }
        userPostMapper.insertBatchUserPosts(userPosts);
    }

    // ==================== ImportExportService 接口实现 ====================

    @Override
    public String getBusinessType() {
        return "user";
    }

    @Override
    public ImportExportService.ImportExportResult importData(List<Map<String, Object>> rowList, Map<String, Object> options) {
        AssertUtils.notEmpty(rowList, ErrorCode.USER_IMPORT_EMPTY);

        ImportExportService.ImportExportResult result = new ImportExportService.ImportExportResult();
        int rowIndex = 1; // 用于错误提示（从 1 开始，方便与 Excel 行号对应）

        boolean overwrite = options != null && Boolean.TRUE.equals(options.get("overwrite"));
        for (Map<String, Object> row : rowList) {
            try {
                UserDTO userDTO = buildUserDTOFromRow(row, rowIndex);
                saveOrUpdateFromImport(userDTO, overwrite, rowIndex, result);
            } catch (BizException e) {
                // 业务异常记录错误信息
                result.setFailCount(result.getFailCount() + 1);
                result.addError("第 " + rowIndex + " 行：" + e.getMessage());
            } catch (Exception e) {
                // 其他异常统一包装
                result.setFailCount(result.getFailCount() + 1);
                result.addError("第 " + rowIndex + " 行导入失败：" + e.getMessage());
            } finally {
                rowIndex++;
            }
        }

        return result;
    }

    /** 导入单行：overwrite 时存在则更新否则新增，否则仅新增；更新 result 计数 */
    private void saveOrUpdateFromImport(UserDTO userDTO, boolean overwrite, int rowIndex,
                                        ImportExportService.ImportExportResult result) {
        boolean success;
        if (overwrite) {
            SysUser existing = self.getUserByUsername(userDTO.getUserName());
            if (existing != null) {
                userDTO.setUserId(existing.getUserId());
                success = self.updateUser(userDTO);
            } else {
                success = self.addUser(userDTO);
            }
        } else {
            success = self.addUser(userDTO);
        }
        if (success) {
            result.setSuccessCount(result.getSuccessCount() + 1);
        } else {
            result.setFailCount(result.getFailCount() + 1);
            result.addError("第 " + rowIndex + " 行" + (overwrite ? "更新" : "新增") + "失败");
        }
    }

    /** 从导出/查询参数 Map 填充 UserReqBo，避免重复 if 判空与设值 */
    private UserReqBo buildUserReqBoFromQueryParams(Map<String, Object> queryParams) {
        UserReqBo bo = new UserReqBo();
        if (queryParams == null) {
            return bo;
        }
        setStringIfPresent(queryParams, "userName", bo::setUserName);
        setStringIfPresent(queryParams, "nickName", bo::setNickName);
        setStringIfPresent(queryParams, "sex", bo::setSex);
        setStringIfPresent(queryParams, "status", bo::setStatus);
        setStringIfPresent(queryParams, "phonenumber", bo::setPhonenumber);
        setStringIfPresent(queryParams, "email", bo::setEmail);
        Object v = queryParams.get("deptId");
        if (v != null) {
            if (v instanceof Number) {
                bo.setDeptId(((Number) v).longValue());
            } else if (v instanceof String && StringUtils.hasText((String) v)) {
                try {
                    bo.setDeptId(Long.parseLong(((String) v).trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return bo;
    }

    private static void setStringIfPresent(Map<String, Object> map, String key, java.util.function.Consumer<String> setter) {
        Object v = map.get(key);
        if (v != null) {
            setter.accept(v.toString());
        }
    }

    @Override
    public List<Map<String, Object>> exportData(Map<String, Object> queryParams) {
        UserReqBo userReqBo = buildUserReqBoFromQueryParams(queryParams);
        // 分批导出，每批 5000 条，防止 OOM
        final int BATCH_SIZE = 5000;
        userReqBo.setPageNum(1);
        userReqBo.setPageSize(BATCH_SIZE);
        PageResponse<UserVO> pageResponse = this.pageList(userReqBo);
        List<UserVO> userList = pageResponse.getRows();
        if (userList == null) {
            userList = new ArrayList<>();
        }

        List<Map<String, Object>> exportList = new ArrayList<>();
        for (UserVO user : userList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("用户账号", user.getUserName() != null ? user.getUserName() : "");
            row.put("用户昵称", user.getNickName() != null ? user.getNickName() : "");
            row.put("邮箱", user.getEmail() != null ? user.getEmail() : "");
            row.put("手机号", user.getPhonenumber() != null ? user.getPhonenumber() : "");
            String sexText = "未知";
            if (user.getSex() != null) {
                if ("0".equals(user.getSex())) {
                    sexText = "男";
                } else if ("1".equals(user.getSex())) {
                    sexText = "女";
                }
            }
            row.put("性别", sexText);
            row.put("状态", AppConstants.Status.NORMAL.equals(user.getStatus()) ? "正常" : "停用");
            row.put("部门ID", user.getDeptId() != null ? user.getDeptId() : "");
            row.put("部门名称", user.getDeptName() != null ? user.getDeptName() : "");
            row.put("备注", user.getRemark() != null ? user.getRemark() : "");
            exportList.add(row);
        }

        return exportList;
    }

    @Override
    public ImportExportService.ImportTemplate getImportTemplate() {
        // 定义表头
        List<String> headers = new ArrayList<>();
        headers.add("用户账号");
        headers.add("用户昵称");
        headers.add("邮箱");
        headers.add("手机号");
        headers.add("性别");
        headers.add("状态");
        headers.add("部门ID");
        headers.add("备注");

        // 定义示例数据
        List<Map<String, Object>> sampleData = new ArrayList<>();
        Map<String, Object> sampleRow = new LinkedHashMap<>();
        sampleRow.put("用户账号", "test001");
        sampleRow.put("用户昵称", "测试用户");
        sampleRow.put("邮箱", "test@example.com");
        sampleRow.put("手机号", "13800138000");
        sampleRow.put("性别", "男");
        sampleRow.put("状态", "正常");
        sampleRow.put("部门ID", "1");
        sampleRow.put("备注", "示例数据");
        sampleData.add(sampleRow);

        return new ImportExportService.ImportTemplate(headers, sampleData);
    }
}

