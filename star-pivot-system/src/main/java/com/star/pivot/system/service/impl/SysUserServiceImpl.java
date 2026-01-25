package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.domain.Constants;
import com.star.pivot.common.domain.DataScope;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.common.utils.SecurityUtils;
import com.star.pivot.security.utils.SecurityContextUtils;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.AssignUserReqBo;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.AccountLockService;
import com.star.pivot.system.service.ImportExportService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.UserPermissionCacheService;
import com.star.pivot.system.utils.DataScopeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

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
    private SysDeptMapper deptMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private UserPermissionCacheService userPermissionCacheService;
    @Autowired
    private AccountLockService accountLockService;
    @Autowired
    private DataScopeService dataScopeService;
    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    @Override
    public PageResponse<UserVO> pageList(UserReqBo userReqBo) {
        Page<SysUser> page = new Page<>(userReqBo.getPageNum(), userReqBo.getPageSize());
        // 1. 获取当前用户数据权限
        DataScope dataScope = dataScopeService.getCurrentUserDataScope();
        // 2. 构造查询参数（MyBatis参数传递）
        Map<String, Object> param = new HashMap<>();
        param.put("dataScope", dataScope);
        param.put("deptIds", dataScope.getDeptIds());
        param.put("userDeptId", dataScope.getUserDeptId()); // 使用DataScope中的userDeptId，避免重复查询
        param.put("userId", dataScope.getUserId());
        param.put("userReqBo",userReqBo);
        //3.分页查询数据
        IPage<SysUser> pageList = sysUserMapper.selectPageList(page, param);
        
        List<SysUser> userList = pageList.getRecords();
        List<UserVO> voList = convertToVOList(userList);
        PageResponse<UserVO> pageResponse = new PageResponse<>();
        // 转换为分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return sysUserMapper.getRolesByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        return sysUserMapper.getMenuByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (getUserByUsername(userDTO.getUserName()) != null) {
            throw new BusinessException("用户名已存在");
        }
        //创建用户
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);
        sysUser.setUserType("00");
        sysUser.setStatus(StringUtils.hasText(userDTO.getStatus()) ? userDTO.getStatus() : Constants.Status.NORMAL);
        sysUser.setDelFlag(Constants.DelFlag.NORMAL);

        // 密码加密  如前端传密码，则进行加密处理，没有传，则使用默认密码123456
        if (StringUtils.hasText(userDTO.getPassword())) {
            sysUser.setPassword(SecurityUtils.encryptPassword(userDTO.getPassword()));
        } else {
            // 默认密码
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
    public UserVO selectByUserId(Long userId) {
        UserVO vo;
        //查询用户信息
        SysUser user = this.getById(userId);
        vo = convertToVO(user);
        return vo;
    }
    /*
    * 修改用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        SysUser user = this.getById(userDTO.getUserId());
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否已被其他用户使用
        SysUser existUser = getUserByUsername(userDTO.getUserName());
        if (existUser != null && !existUser.getUserId().equals(userDTO.getUserId())) {
            throw new BusinessException("用户名已被使用");
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
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
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
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setPwdUpdateDate(LocalDateTime.now());
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            if (!"2".equals(user.getDelFlag())) {
                user.setDelFlag("2");
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
        PageResponse<SysUser> pageResponse = new PageResponse<>();
        // 分页查询
        Page<SysUser> page = new Page<>(assignUserReqBo.getPageNum(), assignUserReqBo.getPageSize());
        
        // 获取当前用户数据权限
        DataScope dataScope = dataScopeService.getCurrentUserDataScope();
        Map<String, Object> param = new HashMap<>();
        param.put("dataScope", dataScope);
        param.put("deptIds", dataScope.getDeptIds());
        param.put("userDeptId", dataScope.getUserDeptId());
        param.put("userId", dataScope.getUserId());
        param.put("assignUserReqBo", assignUserReqBo);
        
        IPage<SysUser> pageList = sysUserMapper.getUserListByRoleId(page, param);
        // 转换为分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
        pageResponse.setPageNum(assignUserReqBo.getPageNum().longValue());
        pageResponse.setPageSize(assignUserReqBo.getPageSize().longValue());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

    @Override
    public PageResponse<SysUser> unallocatedList(AssignUserReqBo assignUserReqBo) {
        PageResponse<SysUser> pageResponse = new PageResponse<>();
        // 分页查询
        Page<SysUser> page = new Page<>(assignUserReqBo.getPageNum(), assignUserReqBo.getPageSize());
        
        // 获取当前用户数据权限
        DataScope dataScope = dataScopeService.getCurrentUserDataScope();
        Map<String, Object> param = new HashMap<>();
        param.put("dataScope", dataScope);
        param.put("deptIds", dataScope.getDeptIds());
        param.put("userDeptId", dataScope.getUserDeptId());
        param.put("userId", dataScope.getUserId());
        param.put("assignUserReqBo", assignUserReqBo);
        
        IPage<SysUser> pageList = sysUserMapper.unallocatedList(page, param);
        // 转换为分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
        pageResponse.setPageNum(assignUserReqBo.getPageNum().longValue());
        pageResponse.setPageSize(assignUserReqBo.getPageSize().longValue());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
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
    @Transactional(rollbackFor = Exception.class)
    public int importUsers(List<Map<String, Object>> rowList) {
        if (rowList == null || rowList.isEmpty()) {
            throw new BusinessException("导入数据不能为空");
        }
        int successCount = 0;
        int rowIndex = 1; // 用于错误提示（从 1 开始，方便与 Excel 行号对应）

        for (Map<String, Object> row : rowList) {
            try {
                // 从 Excel 行数据构建 UserDTO
                UserDTO userDTO = buildUserDTOFromRow(row, rowIndex);

                // 复用已有新增用户逻辑（含用户名唯一性校验、密码加密等）
                boolean success = this.addUser(userDTO);
                if (success) {
                    successCount++;
                }
            } catch (BusinessException e) {
                // 业务异常直接抛出，让前端看到明确提示
                throw e;
            } catch (Exception e) {
                // 其他异常统一包装为业务异常，带上行号
                throw new BusinessException("第 " + rowIndex + " 行导入失败：" + e.getMessage());
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
            throw new BusinessException("第 " + rowIndex + " 行数据为空");
        }

        UserDTO userDTO = new UserDTO();

        // 用户账号（必填）
        String userName = getStringCell(row, "用户账号");
        if (!StringUtils.hasText(userName)) {
            throw new BusinessException("第 " + rowIndex + " 行用户账号不能为空");
        }
        userDTO.setUserName(userName.trim());

        // 用户昵称（必填）
        String nickName = getStringCell(row, "用户昵称");
        if (!StringUtils.hasText(nickName)) {
            throw new BusinessException("第 " + rowIndex + " 行用户昵称不能为空");
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
        String statusCode = Constants.Status.NORMAL;
        if (StringUtils.hasText(statusText)) {
            statusText = statusText.trim();
            if ("停用".equals(statusText) || "禁用".equals(statusText)) {
                statusCode = Constants.Status.DISABLE;
            }
        }
        userDTO.setStatus(statusCode);

        // 部门ID（可选，数字）
        String deptIdText = getStringCell(row, "部门ID");
        if (StringUtils.hasText(deptIdText)) {
            try {
                userDTO.setDeptId(Long.parseLong(deptIdText.trim()));
            } catch (NumberFormatException e) {
                throw new BusinessException("第 " + rowIndex + " 行部门ID格式不正确，应为数字");
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
    /**
     * 批量转换为VO，解决N+1查询问题
     */
    private List<UserVO> convertToVOList(List<SysUser> userList) {
        if (userList == null || userList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 收集所有用户ID
        List<Long> userIds = userList.stream()
                .map(SysUser::getUserId)
                .collect(Collectors.toList());
        
        // 2. 收集所有部门ID
        List<Long> deptIds = userList.stream()
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        // 3. 批量查询部门信息
        Map<Long, String> deptNameMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().in(SysDept::getDeptId, deptIds));
            for (SysDept dept : deptList) {
                deptNameMap.put(dept.getDeptId(), dept.getDeptName());
            }
        }
        
        // 4. 批量查询角色信息
        Map<Long, List<SysRole>> userRolesMap = new HashMap<>();
        LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(UserRole::getUserId, userIds);
        List<UserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        
        // 收集角色ID
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询角色详情
        Map<Long, SysRole> roleMap = new HashMap<>();
        if (!roleIds.isEmpty()) {
            List<SysRole> roleList = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds));
            for (SysRole role : roleList) {
                roleMap.put(role.getRoleId(), role);
            }
        }
        
        // 构建用户角色映射
        for (UserRole userRole : userRoles) {
            userRolesMap.computeIfAbsent(userRole.getUserId(), k -> new ArrayList<>())
                    .add(roleMap.get(userRole.getRoleId()));
        }
        
        // 5. 批量查询岗位信息
        Map<Long, List<Long>> userPostIdsMap = new HashMap<>();
        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(UserPost::getUserId, userIds);
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        
        // 收集岗位ID
        List<Long> postIds = userPosts.stream()
                .map(UserPost::getPostId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询岗位详情
        Map<Long, String> postNameMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds));
            for (SysPost post : postList) {
                postNameMap.put(post.getPostId(), post.getPostName());
            }
        }
        
        // 构建用户岗位映射
        for (UserPost userPost : userPosts) {
            userPostIdsMap.computeIfAbsent(userPost.getUserId(), k -> new ArrayList<>())
                    .add(userPost.getPostId());
        }
        
        // 6. 转换为VO列表
        List<UserVO> voList = new ArrayList<>();
        for (SysUser user : userList) {
            UserVO vo = convertToVO(user, deptNameMap, userRolesMap, userPostIdsMap, postNameMap);
            voList.add(vo);
        }
        
        // 7. 批量查询账户锁定状态
        if (accountLockService != null) {
            for (UserVO vo : voList) {
                if (vo.getUserName() != null) {
                    boolean isLocked = accountLockService.isAccountLocked(vo.getUserName());
                    vo.setIsLocked(isLocked);
                } else {
                    vo.setIsLocked(false);
                }
            }
        }
        
        return voList;
    }
    
    /**
     * 转换为VO（使用预加载的Map信息，避免N+1查询）
     */
    private UserVO convertToVO(SysUser user, Map<Long, String> deptNameMap, Map<Long, List<SysRole>> userRolesMap, 
                            Map<Long, List<Long>> userPostIdsMap, Map<Long, String> postNameMap) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置部门名称
        if (user.getDeptId() != null) {
            vo.setDeptName(deptNameMap.get(user.getDeptId()));
        }

        // 设置角色信息
        List<SysRole> roles = userRolesMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        // 设置岗位信息
        List<Long> postIds = userPostIdsMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!postIds.isEmpty()) {
            vo.setPostIds(postIds);
            
            List<String> postNames = postIds.stream()
                    .map(postNameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            vo.setPostNames(postNames);
        }

        return vo;
    }
    
    /**
     * 转换为VO（单用户查询使用）
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 查询部门名称
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        // 查询角色信息
        List<SysRole> roles = sysRoleMapper.selectRoleListByUserId(user.getUserId());
        if (roles != null && !roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        // 查询岗位信息
        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(UserPost::getUserId, user.getUserId());
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        if (userPosts != null && !userPosts.isEmpty()) {
            List<Long> postIds = userPosts.stream().map(UserPost::getPostId).collect(Collectors.toList());
            vo.setPostIds(postIds);

            List<String> postNames = new ArrayList<>();
            for (Long postId : postIds) {
                SysPost post = postMapper.selectById(postId);
                if (post != null) {
                    postNames.add(post.getPostName());
                }
            }
            vo.setPostNames(postNames);
        }

        // 查询账户锁定状态
        if (accountLockService != null && user.getUserName() != null) {
            boolean isLocked = accountLockService.isAccountLocked(user.getUserName());
            vo.setIsLocked(isLocked);
        } else {
            vo.setIsLocked(false);
        }

        return vo;
    }

    // ==================== ImportExportService 接口实现 ====================

    @Override
    public String getBusinessType() {
        return "user";
    }

    @Override
    public ImportExportService.ImportExportResult importData(List<Map<String, Object>> rowList, Map<String, Object> options) {
        if (rowList == null || rowList.isEmpty()) {
            throw new BusinessException("导入数据不能为空");
        }

        ImportExportService.ImportExportResult result = new ImportExportService.ImportExportResult();
        int rowIndex = 1; // 用于错误提示（从 1 开始，方便与 Excel 行号对应）

        for (Map<String, Object> row : rowList) {
            try {
                // 从 Excel 行数据构建 UserDTO
                UserDTO userDTO = buildUserDTOFromRow(row, rowIndex);

                // 检查是否覆盖已存在数据
                boolean overwrite = options != null && Boolean.TRUE.equals(options.get("overwrite"));
                if (overwrite) {
                    // 如果用户名已存在，则更新用户
                    SysUser existingUser = this.getUserByUsername(userDTO.getUserName());
                    if (existingUser != null) {
                        userDTO.setUserId(existingUser.getUserId());
                        boolean success = this.updateUser(userDTO);
                        if (success) {
                            result.setSuccessCount(result.getSuccessCount() + 1);
                        } else {
                            result.setFailCount(result.getFailCount() + 1);
                            result.addError("第 " + rowIndex + " 行更新失败");
                        }
                    } else {
                        // 不存在则新增
                        boolean success = this.addUser(userDTO);
                        if (success) {
                            result.setSuccessCount(result.getSuccessCount() + 1);
                        } else {
                            result.setFailCount(result.getFailCount() + 1);
                            result.addError("第 " + rowIndex + " 行新增失败");
                        }
                    }
                } else {
                    // 不覆盖，直接新增
                    boolean success = this.addUser(userDTO);
                    if (success) {
                        result.setSuccessCount(result.getSuccessCount() + 1);
                    } else {
                        result.setFailCount(result.getFailCount() + 1);
                        result.addError("第 " + rowIndex + " 行新增失败");
                    }
                }
            } catch (BusinessException e) {
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

    @Override
    public List<Map<String, Object>> exportData(Map<String, Object> queryParams) {
        UserReqBo userReqBo = new UserReqBo();
        if (queryParams != null) {
            Object v;
            if ((v = queryParams.get("userName")) != null) {
                userReqBo.setUserName(v.toString());
            }
            if ((v = queryParams.get("nickName")) != null) {
                userReqBo.setNickName(v.toString());
            }
            if ((v = queryParams.get("sex")) != null) {
                userReqBo.setSex(v.toString());
            }
            if ((v = queryParams.get("status")) != null) {
                userReqBo.setStatus(v.toString());
            }
            if ((v = queryParams.get("phonenumber")) != null) {
                userReqBo.setPhonenumber(v.toString());
            }
            if ((v = queryParams.get("email")) != null) {
                userReqBo.setEmail(v.toString());
            }
            if ((v = queryParams.get("deptId")) != null) {
                Long deptId = null;
                if (v instanceof Number) {
                    deptId = ((Number) v).longValue();
                } else if (v instanceof String && StringUtils.hasText((String) v)) {
                    try {
                        deptId = Long.parseLong(((String) v).trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (deptId != null) {
                    userReqBo.setDeptId(deptId);
                }
            }
        }

        userReqBo.setPageNum(1);
        userReqBo.setPageSize(100_000);
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
            row.put("状态", user.getStatus() != null && "0".equals(user.getStatus()) ? "正常" : "停用");
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

