<template>
  <ElDialog
    v-model="visible"
    title="权限分配"
    width="800px"
    align-center
    class="el-dialog-border"
    @close="handleClose"
  >
    <div class="permission-dialog">
      <ElTabs v-model="activeTab" type="border-card">
        <!-- 菜单权限标签页 -->
        <ElTabPane label="菜单权限" name="menu">
          <!-- 控制选项 -->
          <div class="permission-controls">
            <span>分配菜单</span>
            <ElCheckbox v-model="isExpandAll" @change="toggleExpandAll">展开/折叠</ElCheckbox>
            <ElCheckbox v-model="isSelectAll" @change="toggleSelectAll">全选/全不选</ElCheckbox>
            <ElCheckbox v-model="checkStrictly" @change="handleCheckStrictlyChange">
              父子联动
            </ElCheckbox>
          </div>
          <!-- 树结构容器 -->
          <div
            class="permission-tree-container"
            :class="isDark ? 'dark-bg' : 'light-bg'"
            ref="treeContainerRef"
          >
            <div v-loading="loading" class="tree-wrapper">
              <ElTree
                ref="treeRef"
                :data="menuTreeData"
                show-checkbox
                node-key="menuId"
                :default-expand-all="isExpandAll"
                :check-strictly="!checkStrictly"
                :props="treeProps"
                @check="handleTreeCheck"
                @node-expand="handleNodeExpand"
                @node-collapse="handleNodeCollapse"
              >
                <template #default="{ data }">
                  <span>{{ data.menuName }}</span>
                </template>
              </ElTree>
            </div>
          </div>
        </ElTabPane>
        <!-- 部门权限标签页 -->
        <ElTabPane label="部门权限" name="dept">
          <!-- 控制选项 -->
          <div class="permission-controls">
            <span>分配部门</span>
            <ElCheckbox v-model="deptExpandAll" @change="toggleDeptExpandAll">展开/折叠</ElCheckbox>
            <ElCheckbox v-model="deptSelectAll" @change="toggleDeptSelectAll">
              全选/全不选
            </ElCheckbox>
            <ElCheckbox v-model="deptCheckStrictly" @change="handleDeptCheckStrictlyChange">
              父子联动
            </ElCheckbox>
          </div>
          <!-- 树结构容器 -->
          <div
            class="permission-tree-container"
            :class="isDark ? 'dark-bg' : 'light-bg'"
            ref="deptTreeContainerRef"
          >
            <div v-loading="deptLoading" class="tree-wrapper">
              <ElTree
                ref="deptTreeRef"
                :data="deptTreeData"
                show-checkbox
                node-key="deptId"
                :default-expand-all="deptExpandAll"
                :check-strictly="!deptCheckStrictly"
                :props="deptTreeProps"
                @check="handleDeptTreeCheck"
                @node-expand="handleDeptNodeExpand"
                @node-collapse="handleDeptNodeCollapse"
              >
                <template #default="{ data }">
                  <span>{{ data.deptName }}</span>
                </template>
              </ElTree>
            </div>
          </div>
        </ElTabPane>
      </ElTabs>
    </div>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" @click="savePermission">保存</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import { fetchGetMenuTree, fetchGetRoleMenus, type SysMenu } from '@/api/menu/menu'
  import { fetchGetDeptTree, fetchGetRoleDeptIds, type SysDept } from '@/api/dept/dept'
  import { fetchUpdateRole } from '@/api/role/role'
  import { useSettingStore } from '@/store/modules/setting'

  type RoleListItem = Api.SystemManage.RoleListItem

  interface Props {
    modelValue: boolean
    roleData?: RoleListItem
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    roleData: undefined
  })

  const emit = defineEmits<Emits>()

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 标签页
  const activeTab = ref('menu')

  // 菜单树相关
  const treeRef = ref()
  const treeContainerRef = ref<HTMLElement>()
  const isExpandAll = ref(false)
  const isSelectAll = ref(false)
  const checkStrictly = ref(true) // 父子联动，true表示联动，false表示不联动
  const menuTreeData = ref<SysMenu[]>([])
  const loading = ref(false)

  // 部门树相关
  const deptTreeRef = ref()
  const deptTreeContainerRef = ref<HTMLElement>()
  const deptExpandAll = ref(false)
  const deptSelectAll = ref(false)
  const deptCheckStrictly = ref(true) // 父子联动，true表示联动，false表示不联动
  const deptTreeData = ref<SysDept[]>([])
  const deptLoading = ref(false)

  /**
   * 弹窗显示状态双向绑定
   */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  /**
   * 菜单树形组件配置
   */
  const treeProps = {
    children: 'children',
    label: 'menuName'
  }

  /**
   * 部门树形组件配置
   */
  const deptTreeProps = {
    children: 'children',
    label: 'deptName'
  }

  // 防止重复加载的标记
  const isLoadingMenuTree = ref(false)
  const isLoadingDeptTree = ref(false)

  /**
   * 加载菜单树数据
   */
  const loadMenuTree = async () => {
    // 防止重复调用
    if (isLoadingMenuTree.value) {
      return
    }
    isLoadingMenuTree.value = true
    loading.value = true
    try {
      const menuList = await fetchGetMenuTree()
      if (Array.isArray(menuList) && menuList.length > 0) {
        menuTreeData.value = menuList
        // 等待 DOM 更新
        await nextTick()
        // 如果是编辑模式，加载已选中的菜单
        if (props.roleData?.roleId) {
          // 等待树组件完全初始化后再加载角色菜单权限
          await nextTick()
          // 延迟加载角色菜单，确保树组件已完全渲染
          setTimeout(() => {
            loadRoleMenuIds()
          }, 300)
        }
        // 延迟调整容器高度，确保 DOM 完全渲染
        setTimeout(() => {
          adjustTreeContainerHeight()
        }, 100)
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载菜单树失败:', error)
      }
    } finally {
      loading.value = false
      isLoadingMenuTree.value = false
      // 加载完成后再次调整高度
      setTimeout(() => {
        adjustTreeContainerHeight()
      }, 200)
    }
  }

  /**
   * 加载角色已分配的菜单ID列表
   */
  const loadRoleMenuIds = async () => {
    if (!props.roleData?.roleId) return
    try {
      const menus = await fetchGetRoleMenus(props.roleData.roleId)
      if (Array.isArray(menus) && menus.length > 0) {
        // 从菜单对象列表中提取菜单ID
        const menuIds = extractMenuIds(menus)
        if (menuIds.length > 0) {
          // 等待树组件完全渲染后再设置选中状态
          await nextTick()
          // 再次等待，确保树节点已完全初始化
          setTimeout(() => {
            if (treeRef.value) {
              treeRef.value.setCheckedKeys(menuIds)
              // 设置选中状态后，更新全选按钮状态
              handleTreeCheck()
              // 调整容器高度
              adjustTreeContainerHeight()
            }
          }, 100)
        }
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载角色菜单ID失败:', error)
      }
    }
  }

  /**
   * 递归提取菜单ID列表
   * @param menus 菜单列表
   * @returns 菜单ID数组
   */
  const extractMenuIds = (menus: SysMenu[]): number[] => {
    const ids: number[] = []
    const traverse = (menuList: SysMenu[]) => {
      menuList.forEach((menu) => {
        if (menu.menuId) {
          ids.push(menu.menuId)
        }
        if (menu.children && menu.children.length > 0) {
          traverse(menu.children)
        }
      })
    }
    traverse(menus)
    return ids
  }

  /**
   * 加载部门树数据
   */
  const loadDeptTree = async () => {
    // 防止重复调用
    if (isLoadingDeptTree.value) {
      return
    }
    isLoadingDeptTree.value = true
    deptLoading.value = true
    try {
      const deptList = await fetchGetDeptTree()
      if (Array.isArray(deptList) && deptList.length > 0) {
        deptTreeData.value = deptList
        // 等待 DOM 更新
        await nextTick()
        // 如果是编辑模式，加载已选中的部门
        if (props.roleData?.roleId) {
          // 等待树组件完全初始化后再加载角色部门权限
          await nextTick()
          // 延迟加载角色部门，确保树组件已完全渲染
          setTimeout(() => {
            loadRoleDeptIds()
          }, 300)
        }
        // 延迟调整容器高度，确保 DOM 完全渲染
        setTimeout(() => {
          adjustDeptTreeContainerHeight()
        }, 100)
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载部门树失败:', error)
      }
    } finally {
      deptLoading.value = false
      isLoadingDeptTree.value = false
      // 加载完成后再次调整高度
      setTimeout(() => {
        adjustDeptTreeContainerHeight()
      }, 200)
    }
  }

  /**
   * 加载角色已分配的部门ID列表
   */
  const loadRoleDeptIds = async () => {
    if (!props.roleData?.roleId) return
    try {
      const deptIds = await fetchGetRoleDeptIds(props.roleData.roleId)
      if (Array.isArray(deptIds) && deptIds.length > 0) {
        // 等待树组件完全渲染后再设置选中状态
        await nextTick()
        // 再次等待，确保树节点已完全初始化
        setTimeout(() => {
          if (deptTreeRef.value) {
            deptTreeRef.value.setCheckedKeys(deptIds)
            // 设置选中状态后，更新全选按钮状态
            handleDeptTreeCheck()
            // 调整容器高度
            adjustDeptTreeContainerHeight()
          }
        }, 100)
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载角色部门ID失败:', error)
      }
    }
  }

  /**
   * 监听弹窗打开，初始化权限数据
   */
  watch(
    () => props.modelValue,
    async (newVal) => {
      if (newVal) {
        // 重置状态
        menuTreeData.value = []
        deptTreeData.value = []
        isExpandAll.value = false
        isSelectAll.value = false
        deptExpandAll.value = false
        deptSelectAll.value = false
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        activeTab.value = 'menu'
        // 等待弹窗完全打开后再加载数据
        await nextTick()
        loadMenuTree()
        loadDeptTree()
      } else {
        // 关闭时清空数据和重置加载标记
        menuTreeData.value = []
        deptTreeData.value = []
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        if (treeContainerRef.value) {
          treeContainerRef.value.style.height = 'auto'
        }
        if (deptTreeContainerRef.value) {
          deptTreeContainerRef.value.style.height = 'auto'
        }
      }
    }
  )

  /**
   * 关闭弹窗并清空选中状态
   */
  const handleClose = () => {
    visible.value = false
    treeRef.value?.setCheckedKeys([])
    deptTreeRef.value?.setCheckedKeys([])
  }

  /**
   * 保存权限配置
   */
  const savePermission = async () => {
    if (!props.roleData?.roleId) {
      ElMessage.warning('请先选择角色')
      return
    }

    try {
      // 获取选中的菜单ID和部门ID
      const menuCheckedKeys = treeRef.value?.getCheckedKeys() || []
      const menuHalfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
      const deptCheckedKeys = deptTreeRef.value?.getCheckedKeys() || []
      const deptHalfCheckedKeys = deptTreeRef.value?.getHalfCheckedKeys() || []
      // 合并完全选中和半选中的节点ID，确保父级菜单ID被传递
      const allMenuKeys = [...menuCheckedKeys, ...menuHalfCheckedKeys]
      const allDeptKeys = [...deptCheckedKeys, ...deptHalfCheckedKeys]
      // 去重并过滤出数字类型的ID
      const menuIds = Array.from(new Set(allMenuKeys)).filter(
        (key: any) => typeof key === 'number'
      ) as number[]
      const deptIds = Array.from(new Set(allDeptKeys)).filter(
        (key: any) => typeof key === 'number'
      ) as number[]

      // 调用更新角色接口，同时保存菜单和部门权限
      const updateData = {
        ...props.roleData,
        menuIds,
        deptIds
      }
      await fetchUpdateRole(updateData)

      ElMessage.success('权限保存成功')
      emit('success')
      handleClose()
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('保存权限失败:', error)
      }
    }
  }

  /**
   * 切换全部展开/收起状态
   */
  const toggleExpandAll = () => {
    const tree = treeRef.value
    if (!tree) return

    const nodes = tree.store.nodesMap
    // 这里保留 any，因为 Element Plus 的内部节点类型较复杂
    Object.values(nodes).forEach((node: any) => {
      node.expanded = isExpandAll.value
    })
    // 延迟调整容器高度，等待树节点展开/收起动画完成（Element Plus 动画约 300ms）
    setTimeout(() => {
      adjustTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理树节点展开
   */
  const handleNodeExpand = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理树节点收起
   */
  const handleNodeCollapse = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustTreeContainerHeight()
    }, 350)
  }

  /**
   * 调整树容器高度
   */
  const adjustTreeContainerHeight = () => {
    if (!treeContainerRef.value) return
    const treeEl = treeContainerRef.value.querySelector('.el-tree')
    if (treeEl) {
      // 先移除固定高度，让容器自适应
      treeContainerRef.value.style.height = 'auto'
      // 获取实际内容高度
      const height = treeEl.scrollHeight
      // 设置新的高度，但不超过最大值
      treeContainerRef.value.style.height = `${Math.min(height + 24, 600)}px`
    }
  }

  /**
   * 切换全选/取消全选状态
   */
  const toggleSelectAll = () => {
    const tree = treeRef.value
    if (!tree) return

    if (isSelectAll.value) {
      const allKeys = getAllNodeKeys(menuTreeData.value)
      tree.setCheckedKeys(allKeys)
    } else {
      tree.setCheckedKeys([])
    }
  }

  /**
   * 处理父子联动变化
   */
  const handleCheckStrictlyChange = () => {
    // check-strictly 为 false 时表示父子联动，为 true 时表示不联动
    // checkStrictly 为 true 时表示联动，所以需要取反
    const tree = treeRef.value
    if (tree) {
      // 重新设置选中状态以应用新的联动模式
      const checkedKeys = tree.getCheckedKeys()
      tree.setCheckedKeys([])
      nextTick(() => {
        tree.setCheckedKeys(checkedKeys)
      })
    }
  }

  /**
   * 递归获取所有节点的 key
   * @param nodes 节点列表
   * @returns 所有节点的 key 数组
   */
  const getAllNodeKeys = (nodes: SysMenu[]): number[] => {
    const keys: number[] = []
    const traverse = (nodeList: SysMenu[]): void => {
      nodeList.forEach((node) => {
        if (node.menuId) keys.push(node.menuId)
        if (node.children?.length) traverse(node.children)
      })
    }
    traverse(nodes)
    return keys
  }

  /**
   * 处理树节点选中状态变化
   * 同步更新全选按钮状态
   */
  const handleTreeCheck = () => {
    const tree = treeRef.value
    if (!tree) return

    const checkedKeys = tree.getCheckedKeys()
    const allKeys = getAllNodeKeys(menuTreeData.value)

    isSelectAll.value = checkedKeys.length === allKeys.length && allKeys.length > 0
  }

  /**
   * 切换部门树全部展开/收起状态
   */
  const toggleDeptExpandAll = () => {
    const tree = deptTreeRef.value
    if (!tree) return

    const nodes = tree.store.nodesMap
    Object.values(nodes).forEach((node: any) => {
      node.expanded = deptExpandAll.value
    })
    // 延迟调整容器高度，等待树节点展开/收起动画完成（Element Plus 动画约 300ms）
    setTimeout(() => {
      adjustDeptTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理部门树节点展开
   */
  const handleDeptNodeExpand = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustDeptTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理部门树节点收起
   */
  const handleDeptNodeCollapse = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustDeptTreeContainerHeight()
    }, 350)
  }

  /**
   * 调整部门树容器高度
   */
  const adjustDeptTreeContainerHeight = () => {
    if (!deptTreeContainerRef.value) return
    const treeEl = deptTreeContainerRef.value.querySelector('.el-tree')
    if (treeEl) {
      // 先移除固定高度，让容器自适应
      deptTreeContainerRef.value.style.height = 'auto'
      // 获取实际内容高度
      const height = treeEl.scrollHeight
      // 设置新的高度，但不超过最大值
      deptTreeContainerRef.value.style.height = `${Math.min(height + 24, 600)}px`
    }
  }

  /**
   * 切换部门树全选/取消全选状态
   */
  const toggleDeptSelectAll = () => {
    const tree = deptTreeRef.value
    if (!tree) return

    if (deptSelectAll.value) {
      const allKeys = getAllDeptNodeKeys(deptTreeData.value)
      tree.setCheckedKeys(allKeys)
    } else {
      tree.setCheckedKeys([])
    }
  }

  /**
   * 处理部门树父子联动变化
   */
  const handleDeptCheckStrictlyChange = () => {
    const tree = deptTreeRef.value
    if (tree) {
      // 重新设置选中状态以应用新的联动模式
      const checkedKeys = tree.getCheckedKeys()
      tree.setCheckedKeys([])
      nextTick(() => {
        tree.setCheckedKeys(checkedKeys)
      })
    }
  }

  /**
   * 递归获取所有部门节点的 key
   * @param nodes 节点列表
   * @returns 所有节点的 key 数组
   */
  const getAllDeptNodeKeys = (nodes: SysDept[]): number[] => {
    const keys: number[] = []
    const traverse = (nodeList: SysDept[]): void => {
      nodeList.forEach((node) => {
        if (node.deptId) keys.push(node.deptId)
        if (node.children?.length) traverse(node.children)
      })
    }
    traverse(nodes)
    return keys
  }

  /**
   * 处理部门树节点选中状态变化
   * 同步更新全选按钮状态
   */
  const handleDeptTreeCheck = () => {
    const tree = deptTreeRef.value
    if (!tree) return

    const checkedKeys = tree.getCheckedKeys()
    const allKeys = getAllDeptNodeKeys(deptTreeData.value)

    deptSelectAll.value = checkedKeys.length === allKeys.length && allKeys.length > 0
  }
</script>

<style scoped lang="scss">
  .permission-dialog {
    .permission-controls {
      display: flex;
      gap: 20px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--el-border-color-lighter);
      color: var(--el-text-color-primary);
      transition: color 0.3s ease;
    }

    .permission-tree-container {
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 4px;
      padding: 12px;
      min-height: 100px;
      max-height: 600px;
      overflow: auto;
      transition:
        height 0.3s ease,
        background-color 0.3s ease;
      height: auto;

      &.light-bg {
        background: #fff;
      }

      &.dark-bg {
        background: var(--el-bg-color-page);
      }

      .tree-wrapper {
        min-height: 100px;
      }
    }
  }
</style>
