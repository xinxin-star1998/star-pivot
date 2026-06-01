package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.excel.ExcelImportResult;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.system.assembler.UserVOAssembler;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.AssignUserReqBo;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.domain.excel.SysUserExcel;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.interfaces.SysUserService;
import com.star.pivot.system.service.interfaces.UserPermissionCacheService;
import com.star.pivot.system.utils.DataScopeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private TransactionTemplate transactionTemplate;

    

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

        boolean success = this.updateById(user);
        if (success) {
            userPermissionCacheService.clearUserPermissionCache(user.getUserName());
        }
        return success;
    }

    @Override
    public boolean updateUserPassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        AssertUtils.notNull(user, ErrorCode.USER_NOT_FOUND);
        if (AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword())) {
            throw new BizException(ErrorCode.USER_PASSWORD_ERROR, "旧密码不正确");
        }
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "新密码不能与旧密码相同");
        }

        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        user.setPwdUpdateDate(LocalDateTime.now());
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(user);
        if (success) {
            userPermissionCacheService.clearUserPermissionCache(user.getUserName());
        }
        return success;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean deleteUserByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }
        
        String currentUser = SecurityContextUtils.getUsername();
        
        // 使用 LambdaUpdateWrapper 进行批量更新，避免先查询再更新的额外开销
        return this.update(new LambdaUpdateWrapper<SysUser>()
                .in(SysUser::getUserId, userIds)
                .eq(SysUser::getDelFlag, AppConstants.DelFlag.NORMAL)
                .set(SysUser::getDelFlag, AppConstants.DelFlag.DELETE)
                .set(SysUser::getUpdateBy, currentUser)
                .set(SysUser::getUpdateTime, LocalDateTime.now()));
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
        resp.setPageNum(pageNum);
        resp.setPageSize(pageSize);
        resp.setPageCount(ipage.getPages());
        return resp;
    }

    @Override
    public List<SysUserExcel> listForExport(UserReqBo userReqBo) {
        final int batchSize = 5000;
        userReqBo.setPageNum(1);
        userReqBo.setPageSize(batchSize);
        PageResponse<UserVO> pageResponse = this.pageList(userReqBo);
        List<UserVO> userList = pageResponse.getRows();
        if (userList == null) {
            return new ArrayList<>();
        }
        List<SysUserExcel> exportList = new ArrayList<>(userList.size());
        for (UserVO user : userList) {
            SysUserExcel row = new SysUserExcel();
            row.setUserName(StringUtils.hasText(user.getUserName()) ? user.getUserName() : "");
            row.setNickName(StringUtils.hasText(user.getNickName()) ? user.getNickName() : "");
            row.setEmail(StringUtils.hasText(user.getEmail()) ? user.getEmail() : "");
            row.setPhonenumber(StringUtils.hasText(user.getPhonenumber()) ? user.getPhonenumber() : "");
            row.setSex(convertSexCodeToText(user.getSex()));
            row.setStatus(AppConstants.Status.NORMAL.equals(user.getStatus()) ? "正常" : "停用");
            row.setDeptId(user.getDeptId());
            row.setDeptName(StringUtils.hasText(user.getDeptName()) ? user.getDeptName() : "");
            row.setRemark(StringUtils.hasText(user.getRemark()) ? user.getRemark() : "");
            exportList.add(row);
        }
        return exportList;
    }

    /**
     * 将性别编码转换为文本
     *
     * @param sexCode 性别编码：0-男，1-女，其他-未知
     * @return 性别文本
     */
    private String convertSexCodeToText(String sexCode) {
        if (!StringUtils.hasText(sexCode)) {
            return "未知";
        }
        return switch (sexCode) {
            case "0" -> "男";
            case "1" -> "女";
            default -> "未知";
        };
    }

    @Override
    public ExcelImportResult importFromExcel(List<SysUserExcel> rows, boolean updateSupport) {
        AssertUtils.notEmpty(rows, ErrorCode.USER_IMPORT_EMPTY);
        ExcelImportResult result = new ExcelImportResult();
        int rowIndex = 1;
        for (SysUserExcel row : rows) {
            try {
                UserDTO userDTO = buildUserDTOFromExcel(row, rowIndex);
                saveOrUpdateFromImport(userDTO, updateSupport, rowIndex, result);
            } catch (BizException e) {
                result.setFailCount(result.getFailCount() + 1);
                result.addError("第 " + rowIndex + " 行：" + e.getMessage());
            } catch (Exception e) {
                result.setFailCount(result.getFailCount() + 1);
                result.addError("第 " + rowIndex + " 行导入失败：" + e.getMessage());
            } finally {
                rowIndex++;
            }
        }
        return result;
    }

    private UserDTO buildUserDTOFromExcel(SysUserExcel row, int rowIndex) {
        if (row == null) {
            throw new BizException(ErrorCode.USER_IMPORT_ROW_EMPTY, "第 " + rowIndex + " 行数据为空");
        }
        UserDTO userDTO = new UserDTO();
        if (!StringUtils.hasText(row.getUserName())) {
            throw new BizException(ErrorCode.USER_IMPORT_USERNAME_EMPTY, "第 " + rowIndex + " 行用户账号不能为空");
        }
        userDTO.setUserName(row.getUserName().trim());
        if (!StringUtils.hasText(row.getNickName())) {
            throw new BizException(ErrorCode.USER_IMPORT_NICKNAME_EMPTY, "第 " + rowIndex + " 行用户昵称不能为空");
        }
        userDTO.setNickName(row.getNickName().trim());
        if (StringUtils.hasText(row.getEmail())) {
            userDTO.setEmail(row.getEmail().trim());
        }
        if (StringUtils.hasText(row.getPhonenumber())) {
            userDTO.setPhonenumber(row.getPhonenumber().trim());
        }
        String sexCode = "2";
        if (StringUtils.hasText(row.getSex())) {
            String sexText = row.getSex().trim();
            if ("男".equals(sexText)) {
                sexCode = "0";
            } else if ("女".equals(sexText)) {
                sexCode = "1";
            }
        }
        userDTO.setSex(sexCode);
        String statusCode = AppConstants.Status.NORMAL;
        if (StringUtils.hasText(row.getStatus())) {
            String statusText = row.getStatus().trim();
            if ("停用".equals(statusText) || "禁用".equals(statusText)) {
                statusCode = AppConstants.Status.DISABLE;
            }
        }
        userDTO.setStatus(statusCode);
        if (row.getDeptId() != null) {
            userDTO.setDeptId(row.getDeptId());
        }
        if (StringUtils.hasText(row.getRemark())) {
            userDTO.setRemark(row.getRemark().trim());
        }
        return userDTO;
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

    /** 导入单行：updateSupport 时存在则更新否则新增，否则仅新增 */
    private void saveOrUpdateFromImport(
            UserDTO userDTO, boolean updateSupport, int rowIndex, ExcelImportResult result) {
        boolean success;
        if (updateSupport) {
            SysUser existing = getUserByUsername(userDTO.getUserName());
            if (existing != null) {
                userDTO.setUserId(existing.getUserId());
                success = transactionTemplate.execute(status -> updateUser(userDTO));
            } else {
                success = transactionTemplate.execute(status -> addUser(userDTO));
            }
        } else {
            success = transactionTemplate.execute(status -> addUser(userDTO));
        }
        if (Boolean.TRUE.equals(success)) {
            result.setSuccessCount(result.getSuccessCount() + 1);
        } else {
            result.setFailCount(result.getFailCount() + 1);
            result.addError("第 " + rowIndex + " 行" + (updateSupport ? "更新" : "新增") + "失败");
        }
    }

    @Override
    public boolean isCurrentUserSuperAdmin() {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        // 超级管理员用户ID
        if (AppConstants.ADMIN_USER_ID.equals(currentUserId)) {
            return true;
        }
        // 拥有 admin 角色的用户
        List<SysRole> roles = getRolesByUserId(currentUserId);
        return roles != null && roles.stream()
                .anyMatch(role -> AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
    }

    @Override
    public boolean canUpdateUser(Long targetUserId) {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        // 超级管理员可以修改所有用户
        if (isCurrentUserSuperAdmin()) {
            return true;
        }
        // 普通用户只能修改自己
        return currentUserId.equals(targetUserId);
    }

    @Override
    public String canDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return "删除ID不能为空";
        }
        Long currentUserId = SecurityContextUtils.getUserId();
        for (Long userId : userIds) {
            if (userId.equals(currentUserId)) {
                return "不能删除当前登录用户";
            }
        }
        return null;
    }

    @Override
    public String canResetPassword(Long targetUserId) {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId != null && currentUserId.equals(targetUserId)) {
            return "不能重置当前登录用户密码";
        }
        return null;
    }
}

