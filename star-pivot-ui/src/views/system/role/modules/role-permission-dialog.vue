<template>
  <ElDialog
    v-model="visible"
    title="菜单权限"
    width="520px"
    align-center
    class="el-dialog-border"
    @close="handleClose"
  >
    <div class="permission-dialog">
      <!-- 控制选项 -->
      <div class="permission-controls">
        <ElCheckbox v-model="isExpandAll" @change="toggleExpandAll">展开/折叠</ElCheckbox>
        <ElCheckbox v-model="isSelectAll" @change="toggleSelectAll">全选/全不选</ElCheckbox>
        <ElCheckbox v-model="checkStrictly" @change="handleCheckStrictlyChange">
          父子联动
        </ElCheckbox>
      </div>
      <!-- 树结构容器 -->
      <div class="permission-tree-container" ref="treeContainerRef">
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

  const treeRef = ref()
  const treeContainerRef = ref<HTMLElement>()
  const isExpandAll = ref(false)
  const isSelectAll = ref(false)
  const checkStrictly = ref(true) // 父子联动，true表示联动，false表示不联动
  const menuTreeData = ref<SysMenu[]>([])
  const loading = ref(false)

  /**
   * 弹窗显示状态双向绑定
   */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  /**
   * 树形组件配置
   */
  const treeProps = {
    children: 'children',
    label: 'menuName'
  }

  // 防止重复加载的标记
  const isLoadingMenuTree = ref(false)

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
   * 监听弹窗打开，初始化权限数据
   */
  watch(
    () => props.modelValue,
    async (newVal) => {
      if (newVal) {
        // 重置状态
        menuTreeData.value = []
        isExpandAll.value = false
        isSelectAll.value = false
        isLoadingMenuTree.value = false
        // 等待弹窗完全打开后再加载数据
        await nextTick()
        loadMenuTree()
      } else {
        // 关闭时清空数据和重置加载标记
        menuTreeData.value = []
        isLoadingMenuTree.value = false
        if (treeContainerRef.value) {
          treeContainerRef.value.style.height = 'auto'
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
      // TODO: 调用保存权限接口
      // const checkedKeys = treeRef.value?.getCheckedKeys() || []
      // const menuIds = checkedKeys.filter((key: any) => typeof key === 'number') as number[]
      // await fetchSaveRoleMenuIds(props.roleData.roleId, menuIds)

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
</script>

<style scoped lang="scss">
  .permission-dialog {
    .permission-controls {
      display: flex;
      gap: 20px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }

    .permission-tree-container {
      background: #fff;
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 4px;
      padding: 12px;
      min-height: 100px;
      max-height: 600px;
      overflow: auto;
      transition: height 0.3s ease;
      height: auto;

      .tree-wrapper {
        min-height: 100px;
      }
    }
  }
</style>
