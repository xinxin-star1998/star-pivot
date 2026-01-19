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
            <ElCheckbox
              v-model="isSelectAll"
              :indeterminate="isSelectAllIndeterminate"
              @change="toggleSelectAll"
              >全选/全不选</ElCheckbox
            >
            <ElCheckbox v-model="isParentChildLinked" @change="handleCheckStrictlyChange">
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
                :check-strictly="checkStrictly"
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
            <ElCheckbox
              v-model="deptSelectAll"
              :indeterminate="deptSelectAllIndeterminate"
              @change="toggleDeptSelectAll"
            >
              全选/全不选
            </ElCheckbox>
            <ElCheckbox v-model="isDeptParentChildLinked" @change="handleDeptCheckStrictlyChange">
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
                :check-strictly="deptCheckStrictly"
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
  import { fetchGetMenuTree, type SysMenu } from '@/api/menu/menu'
  import { fetchGetDeptTree, type SysDept } from '@/api/dept/dept'
  import { fetchAssignPermission, fetchGetRoleMenus, fetchGetRoleDeptIds } from '@/api/role/role'
  import { useSettingStore } from '@/store/modules/setting'
  import { useCheckableTree } from '@/composables/useCheckableTree'

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

  // 菜单树（通用封装）
  const menuTree = useCheckableTree<SysMenu>({ keyField: 'menuId', childrenField: 'children' })
  const loading = ref(false)

  // 部门树（通用封装）
  const deptTree = useCheckableTree<SysDept>({ keyField: 'deptId', childrenField: 'children' })
  const deptLoading = ref(false)

  // 兼容原模板变量命名（保持模板不动，脚本大幅收敛）
  const treeRef = menuTree.treeRef
  const treeContainerRef = menuTree.containerRef
  const isExpandAll = menuTree.expandAll
  const isSelectAll = menuTree.selectAll
  const isSelectAllIndeterminate = menuTree.indeterminate
  const isParentChildLinked = menuTree.parentChildLinked
  const checkStrictly = menuTree.checkStrictly
  const menuTreeData = menuTree.data

  const deptTreeRef = deptTree.treeRef
  const deptTreeContainerRef = deptTree.containerRef
  const deptExpandAll = deptTree.expandAll
  const deptSelectAll = deptTree.selectAll
  const deptSelectAllIndeterminate = deptTree.indeterminate
  const isDeptParentChildLinked = deptTree.parentChildLinked
  const deptCheckStrictly = deptTree.checkStrictly
  const deptTreeData = deptTree.data

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
  // Tree 组件 props（模板仍使用）
  const treeProps = { children: 'children', label: 'menuName' }
  const deptTreeProps = { children: 'children', label: 'deptName' }

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
        menuTree.data.value = menuList
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
          menuTree.adjustContainerHeight()
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
        menuTree.adjustContainerHeight()
      }, 200)
    }
  }

  /**
   * 加载角色已分配的菜单ID列表
   */
  const loadRoleMenuIds = async () => {
    if (!props.roleData?.roleId) return
    try {
      const menuIds = await fetchGetRoleMenus(props.roleData.roleId)
      console.log('加载角色菜单ID:', menuIds)
      // 确保menuIds是数组类型，处理null或undefined情况
      const safeMenuIds = Array.isArray(menuIds) ? menuIds : []
      console.log('菜单ID列表:', safeMenuIds)
      await menuTree.applyCheckedKeys(safeMenuIds)
      menuTree.adjustContainerHeight()
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载角色菜单ID失败:', error)
      }
    }
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
        deptTree.data.value = deptList
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
          deptTree.adjustContainerHeight()
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
        deptTree.adjustContainerHeight()
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
      console.log('加载角色部门ID:', deptIds)
      // 确保deptIds是数组类型，处理null或undefined情况
      const safeDeptIds = Array.isArray(deptIds) ? deptIds : []
      console.log('部门ID列表:', safeDeptIds)
      await deptTree.applyCheckedKeys(safeDeptIds)
      deptTree.adjustContainerHeight()
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
        menuTree.data.value = []
        deptTree.data.value = []
        menuTree.expandAll.value = false
        menuTree.selectAll.value = false
        menuTree.indeterminate.value = false
        deptTree.expandAll.value = false
        deptTree.selectAll.value = false
        deptTree.indeterminate.value = false
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        activeTab.value = 'menu'
        // 重置树组件选中状态
        menuTree.treeRef.value?.setCheckedKeys([])
        deptTree.treeRef.value?.setCheckedKeys([])
        // 等待弹窗完全打开后再加载数据
        await nextTick()
        loadMenuTree()
        loadDeptTree()
      } else {
        // 关闭时清空数据和重置加载标记
        menuTree.data.value = []
        deptTree.data.value = []
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        if (menuTree.containerRef.value) {
          menuTree.containerRef.value.style.height = 'auto'
        }
        if (deptTree.containerRef.value) {
          deptTree.containerRef.value.style.height = 'auto'
        }
      }
    }
  )

  /**
   * 关闭弹窗并清空选中状态
   */
  const handleClose = () => {
    visible.value = false
    menuTree.treeRef.value?.setCheckedKeys([])
    deptTree.treeRef.value?.setCheckedKeys([])
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
      const menuCheckedKeys = menuTree.treeRef.value?.getCheckedKeys() || []
      const menuHalfCheckedKeys = menuTree.treeRef.value?.getHalfCheckedKeys() || []
      const deptCheckedKeys = deptTree.treeRef.value?.getCheckedKeys() || []
      const deptHalfCheckedKeys = deptTree.treeRef.value?.getHalfCheckedKeys() || []
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
      await fetchAssignPermission(updateData)

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
    menuTree.toggleExpandAll()
  }

  /**
   * 处理树节点展开
   */
  const handleNodeExpand = () => {
    menuTree.handleNodeExpand()
  }

  /**
   * 处理树节点收起
   */
  const handleNodeCollapse = () => {
    menuTree.handleNodeCollapse()
  }

  /**
   * 切换全选/取消全选状态
   */
  const toggleSelectAll = () => {
    menuTree.toggleSelectAll()
  }

  /**
   * 处理父子联动变化
   */
  const handleCheckStrictlyChange = () => {
    menuTree.handleCheckStrictlyChange()
  }

  /**
   * 处理树节点选中状态变化
   * 同步更新全选按钮状态
   */
  const handleTreeCheck = () => {
    menuTree.syncSelectState()
  }

  /**
   * 切换部门树全部展开/收起状态
   */
  const toggleDeptExpandAll = () => {
    deptTree.toggleExpandAll()
  }

  /**
   * 处理部门树节点展开
   */
  const handleDeptNodeExpand = () => {
    deptTree.handleNodeExpand()
  }

  /**
   * 处理部门树节点收起
   */
  const handleDeptNodeCollapse = () => {
    deptTree.handleNodeCollapse()
  }

  /**
   * 切换部门树全选/取消全选状态
   */
  const toggleDeptSelectAll = () => {
    deptTree.toggleSelectAll()
  }

  /**
   * 处理部门树父子联动变化
   */
  const handleDeptCheckStrictlyChange = () => {
    deptTree.handleCheckStrictlyChange()
  }

  /**
   * 处理部门树节点选中状态变化
   * 同步更新全选按钮状态
   */
  const handleDeptTreeCheck = () => {
    deptTree.syncSelectState()
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
