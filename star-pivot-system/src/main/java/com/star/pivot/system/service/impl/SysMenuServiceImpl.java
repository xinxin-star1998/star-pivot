package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.constants.Constants;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.RoleMenu;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.RoleMenuMapper;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.UserPermissionCacheService;
import com.star.pivot.system.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 21:57:29
 */
@Slf4j
@Service("sysMenuService")
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final Long ROOT_PARENT_ID = 0L;
    private static final Long TOP_MENU_PARENT_ID = -1L;

    private final SysUserService sysUserService;
    private final RoleMenuMapper roleMenuMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final UserPermissionCacheService userPermissionCacheService;

    @Override
    @Cacheable(cacheNames = "menuTree", key = "'all'")
    public List<SysMenu> menuTree() {
        // 查询所有权限
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> allMenu = this.list(queryWrapper);

        // 构建权限树
        return buildMenuTree(allMenu, ROOT_PARENT_ID);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        log.debug("获取用户菜单树，userId: {}", userId);
        // 查询用户角色
        List<SysRole> roles = sysUserService.getRolesByUserId(userId);
        log.debug("用户角色列表，userId: {}, roles: {}", userId, roles);
        
        List<SysMenu> allMenu;
        // 获取用户角色对应的菜单，roleKey == admin 时，查询全部菜单
        boolean isAdmin = roles.stream().anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
        log.debug("是否为admin用户，userId: {}, isAdmin: {}", userId, isAdmin);
        
        if (isAdmin) {
            // admin用户查询所有可见且正常的菜单（用于构建树结构）
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, Constants.Status.NORMAL)
                    .orderByAsc(SysMenu::getOrderNum);
            allMenu = this.list(queryWrapper);
            log.debug("admin用户查询到所有菜单，userId: {}, menuCount: {}", userId, allMenu != null ? allMenu.size() : 0);
        } else {
            // 获取用户角色对应的菜单
            allMenu = sysUserService.getMenuByUserId(userId);
            if (allMenu == null) {
                allMenu = Collections.emptyList();
            }
            log.debug("普通用户查询到菜单，userId: {}, menuCount: {}", userId, allMenu.size());
        }
        // 将按钮权限(SysMenu.menuType = F)合并到父级菜单的perms字段中，
        // 方便前端基于路由meta.authList做按钮级权限控制
        mergeButtonPermsToParent(allMenu);
        // 构建菜单树，并过滤掉有按钮子节点的菜单
        return buildUserMenuTree(allMenu, ROOT_PARENT_ID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    public boolean insertMenu(MenuDTO menuDTO) {
        log.info("新增菜单: menuName={}, parentId={}, menuType={}", 
                menuDTO.getMenuName(), menuDTO.getParentId(), menuDTO.getMenuType());
        
        // 检查菜单名称是否唯一
        if (!checkMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), null)) {
            throw new BusinessException("菜单名称已存在");
        }

        // 创建菜单
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(menuDTO, menu);
        menu.setParentId(menuDTO.getParentId() != null ? menuDTO.getParentId() : ROOT_PARENT_ID);
        menu.setOrderNum(menuDTO.getOrderNum() != null ? menuDTO.getOrderNum() : 0);
        menu.setIsFrame(menuDTO.getIsFrame() != null ? menuDTO.getIsFrame() : 1);
        menu.setIsCache(menuDTO.getIsCache() != null ? menuDTO.getIsCache() : 0);
        menu.setVisible(StringUtils.hasText(menuDTO.getVisible()) ? menuDTO.getVisible() : Constants.Visible.SHOW);
        menu.setStatus(StringUtils.hasText(menuDTO.getStatus()) ? menuDTO.getStatus() : Constants.Status.NORMAL);

        String currentUser = SecurityContextUtils.getUsername();
        menu.setCreateBy(currentUser);
        menu.setCreateTime(LocalDateTime.now());

        boolean result = this.save(menu);
        if (result) {
            log.info("新增菜单成功: menuId={}, menuName={}", menu.getMenuId(), menu.getMenuName());
            // 清除所有用户权限缓存（菜单变更可能影响所有用户权限）
            userPermissionCacheService.clearAllUserPermissionCache();
        } else {
            log.error("新增菜单失败: menuName={}", menuDTO.getMenuName());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    public boolean updateMenu(MenuDTO menuDTO) {
        log.info("修改菜单: menuId={}, menuName={}", menuDTO.getMenuId(), menuDTO.getMenuName());
        
        SysMenu menu = this.getById(menuDTO.getMenuId());
        if (menu == null) {
            log.warn("菜单不存在: menuId={}", menuDTO.getMenuId());
            throw new BusinessException("菜单不存在");
        }

        // 不能将父菜单设置为自己的子菜单
        if (menuDTO.getParentId() != null && menuDTO.getParentId().equals(menuDTO.getMenuId())) {
            log.warn("不能将父菜单设置为自己的子菜单: menuId={}", menuDTO.getMenuId());
            throw new BusinessException("不能将父菜单设置为自己的子菜单");
        }

        // 检查菜单名称是否唯一
        if (!checkMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), menuDTO.getMenuId())) {
            log.warn("菜单名称已存在: menuName={}, parentId={}", menuDTO.getMenuName(), menuDTO.getParentId());
            throw new BusinessException("菜单名称已存在");
        }

        // 更新菜单信息
        BeanUtils.copyProperties(menuDTO, menu, "menuId");
        String currentUser = SecurityContextUtils.getUsername();
        menu.setUpdateBy(currentUser);
        menu.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(menu);
        if (result) {
            log.info("修改菜单成功: menuId={}, menuName={}", menu.getMenuId(), menu.getMenuName());
            // 清除所有用户权限缓存（菜单变更可能影响所有用户权限）
            userPermissionCacheService.clearAllUserPermissionCache();
        } else {
            log.error("修改菜单失败: menuId={}", menuDTO.getMenuId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    public boolean deleteMenu(Long menuId) {
        log.info("删除菜单: menuId={}", menuId);
        
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        long count = this.count(wrapper);
        if (count > 0) {
            log.warn("存在子菜单，不允许删除: menuId={}, 子菜单数量={}", menuId, count);
            throw new BusinessException("存在子菜单，不允许删除");
        }

        // 检查是否有角色使用该菜单
        LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(RoleMenu::getMenuId, menuId);
        long roleMenuCount = roleMenuMapper.selectCount(roleMenuWrapper);
        if (roleMenuCount > 0) {
            log.warn("菜单已被角色使用，不允许删除: menuId={}, 使用角色数量={}", menuId, roleMenuCount);
            throw new BusinessException("菜单已被角色使用，不允许删除");
        }

        boolean result = this.removeById(menuId);
        if (result) {
            log.info("删除菜单成功: menuId={}", menuId);
            // 清除所有用户权限缓存（菜单变更可能影响所有用户权限）
            userPermissionCacheService.clearAllUserPermissionCache();
        } else {
            log.error("删除菜单失败: menuId={}", menuId);
        }
        return result;
    }

    @Override
    public List<SysMenu> getParent() {
        // 查询目录和菜单类型（排除按钮类型）
        List<String> menuTypes = Arrays.asList(
                Constants.MenuType.CATALOG,
                Constants.MenuType.MENU
        );
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().in(SysMenu::getMenuType, menuTypes).orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menuList = this.baseMapper.selectList(query);
        
        // 组装顶级菜单
        SysMenu topMenu = new SysMenu();
        topMenu.setMenuName("顶级菜单");
        topMenu.setLabel("顶级菜单");
        topMenu.setParentId(TOP_MENU_PARENT_ID);
        topMenu.setMenuId(ROOT_PARENT_ID);
        topMenu.setValue(ROOT_PARENT_ID);
        menuList.add(topMenu);
        
        // 组装菜单树（使用统一的树构建方法，并设置label和value）
        return buildMenuTreeWithLabelValue(menuList, TOP_MENU_PARENT_ID);
    }

    /**
     * 构建菜单树（带label和value字段，用于下拉选择等场景）
     * @param menuList 菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenu> buildMenuTreeWithLabelValue(List<SysMenu> menuList, long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        
        // 筛选出指定父级ID的菜单
        Optional.ofNullable(menuList).orElse(Collections.emptyList())
                .stream()
                .filter(item -> item != null && item.getParentId() != null && item.getParentId().equals(parentId))
                .forEach(item -> {
                    SysMenu menu = new SysMenu();
                    BeanUtils.copyProperties(item, menu);
                    menu.setLabel(item.getMenuName());
                    menu.setValue(item.getMenuId());
                    // 递归构建子树
                    List<SysMenu> children = buildMenuTreeWithLabelValue(menuList, item.getMenuId());
                    menu.setChildren(children);
                    tree.add(menu);
                });
        
        return tree;
    }

    public boolean checkMenuNameUnique(String menuName, Long parentId, Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menuName)
                .eq(SysMenu::getParentId, parentId != null ? parentId : ROOT_PARENT_ID);
        if (menuId != null) {
            wrapper.ne(SysMenu::getMenuId, menuId);
        }
        return this.count(wrapper) == 0;
    }
    /**
     * 构建菜单树（通用方法）
     * @param allMenu 所有菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> allMenu, long parentId) {
        if (allMenu == null || allMenu.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SysMenu> tree = new ArrayList<>();

        // 筛选出指定父级ID的菜单
        List<SysMenu> children = allMenu.stream()
                .filter(menu -> menu != null && menu.getParentId() != null && menu.getParentId().equals(parentId))
                .toList();

        // 递归构建子树
        for (SysMenu menu : children) {
            // 设置label和value字段，用于前端显示
            menu.setLabel(menu.getMenuName());
            menu.setValue(menu.getMenuId());
            
            List<SysMenu> childTree = buildMenuTree(allMenu, menu.getMenuId());
            menu.setChildren(childTree);
            tree.add(menu);
        }

        return tree;
    }

    /**
     * 构建用户菜单树（过滤掉按钮类型和有按钮子节点的菜单）
     * @param allMenu 所有菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenu> buildUserMenuTree(List<SysMenu> allMenu, long parentId) {
        if (allMenu == null || allMenu.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysMenu> tree = new ArrayList<>();
        // 筛选出指定父级ID的菜单，排除按钮类型
        List<SysMenu> children = allMenu.stream()
                .filter(menu -> menu != null 
                        && menu.getParentId() != null 
                        && menu.getParentId().equals(parentId)
                        && !Constants.MenuType.BUTTON.equals(menu.getMenuType())) // 过滤掉按钮类型
                .toList();

        // 递归构建子树
        for (SysMenu menu : children) {
            // 设置label和value字段，用于前端显示
            menu.setLabel(menu.getMenuName());
            menu.setValue(menu.getMenuId());
            
            List<SysMenu> childTree = buildUserMenuTree(allMenu, menu.getMenuId());
            
            // 如果子菜单树为空，则不设置children
            if (!childTree.isEmpty()) {
                menu.setChildren(childTree);
            }
            tree.add(menu);
        }

        return tree;
    }

    /**
     * 将按钮权限合并到父级菜单
     *
     * <p>约定：
     * <ul>
     *   <li>按钮菜单：menuType = F，perms 为具体权限标识（如 system:user:add）</li>
     *   <li>父级菜单（页面级）：menuType = M / C</li>
     *   <li>同一父级下多个按钮的 perms 会合并为逗号分隔字符串，并去重</li>
     *   <li>如果父级本身已有 perms，会一并合并并去重</li>
     * </ul>
     *
     * <p>这样前端在根据 /sys/menu/userMenuTree 构建路由时，
     * 就可以直接从每个页面级菜单的 perms 中得到该用户实际拥有的按钮权限列表，
     * 再通过 meta.authList + v-auth/useAuth 实现按钮级展示控制。
     *
     * @param allMenu 当前用户拥有的完整菜单列表（包含按钮）
     */
    private void mergeButtonPermsToParent(List<SysMenu> allMenu) {
        if (allMenu == null || allMenu.isEmpty()) {
            return;
        }

        // 1. 按父菜单ID聚合按钮权限
        // key: parentId, value: 该父菜单下所有按钮的权限标识集合
        var buttonPermsByParent = new java.util.HashMap<Long, java.util.Set<String>>();

        for (SysMenu menu : allMenu) {
            if (menu == null) {
                continue;
            }
            // 只处理按钮类型
            if (!Constants.MenuType.BUTTON.equals(menu.getMenuType())) {
                continue;
            }
            Long parentId = menu.getParentId();
            if (parentId == null) {
                continue;
            }
            String perms = menu.getPerms();
            if (!StringUtils.hasText(perms)) {
                continue;
            }
            // 支持逗号分隔的多权限
            String[] splitPerms = perms.split(",");
            java.util.Set<String> permSet = buttonPermsByParent
                    .computeIfAbsent(parentId, k -> new java.util.LinkedHashSet<>());
            for (String perm : splitPerms) {
                if (StringUtils.hasText(perm)) {
                    permSet.add(perm.trim());
                }
            }
        }

        if (buttonPermsByParent.isEmpty()) {
            return;
        }

        // 2. 将聚合后的按钮权限写回对应父菜单的 perms 字段
        for (SysMenu menu : allMenu) {
            if (menu == null) {
                continue;
            }
            // 只合并到非按钮菜单（目录/菜单）
            if (Constants.MenuType.BUTTON.equals(menu.getMenuType())) {
                continue;
            }
            Long menuId = menu.getMenuId();
            if (menuId == null) {
                continue;
            }
            java.util.Set<String> childPerms = buttonPermsByParent.get(menuId);
            if (childPerms == null || childPerms.isEmpty()) {
                continue;
            }

            // 合并父级原有的 perms（如果有）
            java.util.Set<String> merged = new java.util.LinkedHashSet<>(childPerms);
            String currentPerms = menu.getPerms();
            if (StringUtils.hasText(currentPerms)) {
                String[] existing = currentPerms.split(",");
                for (String perm : existing) {
                    if (StringUtils.hasText(perm)) {
                        merged.add(perm.trim());
                    }
                }
            }

            // 重新设置为去重后的逗号分隔字符串
            menu.setPerms(String.join(",", merged));
        }
    }
}

