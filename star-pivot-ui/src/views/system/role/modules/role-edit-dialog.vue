<template>
  <ElDialog
    v-model="visible"
    :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
    width="30%"
    align-center
    @close="handleClose"
  >
    <ElForm ref="roleForm" :model="form" :rules="rules" label-width="120px">
      <ElFormItem label="角色名称" prop="roleName">
        <ElInput v-model="form.roleName" placeholder="请输入角色名称" />
      </ElFormItem>
      <ElFormItem label="角色编码" prop="roleKey">
        <ElInput v-model="form.roleKey" placeholder="请输入角色编码" />
      </ElFormItem>
      <ElFormItem label="显示顺序" prop="roleSort">
        <ElInput v-model="form.roleSort" placeholder="请输入显示顺序" />
      </ElFormItem>
      <ElFormItem label="备注" prop="description">
        <ElInput v-model="form.remark" type="textarea" :rows="3" placeholder="请输入角色描述" />
      </ElFormItem>
      <ElFormItem label="状态">
        <ElSwitch v-model="statusSwitch" />
      </ElFormItem>
      <ElFormItem label="是否关联菜单树">
        <ElSwitch v-model="menuSwitch" />
      </ElFormItem>
      <ElFormItem label="菜单权限" v-if="menuSwitch">
        <div class="permission-tree-wrapper">
          <!-- 控制选项 -->
          <div class="permission-controls">
            <ElCheckbox v-model="menuExpandAll" @change="toggleMenuExpandAll">展开/折叠</ElCheckbox>
            <ElCheckbox
              v-model="menuSelectAll"
              :indeterminate="menuSelectAllIndeterminate"
              @change="toggleMenuSelectAll"
            >
              全选/全不选
            </ElCheckbox>
            <ElCheckbox v-model="isMenuParentChildLinked" @change="handleMenuCheckStrictlyChange">
              父子联动
            </ElCheckbox>
          </div>
          <!-- 树结构容器 -->
          <div
            class="permission-tree-container"
            :class="isDark ? 'dark-bg' : 'light-bg'"
            ref="menuTreeContainerRef"
          >
            <ElTree
              ref="menuTreeRef"
              :data="menuTreeData"
              show-checkbox
              node-key="menuId"
              :default-expand-all="menuExpandAll"
              :check-strictly="menuCheckStrictly"
              :props="menuTreeProps"
              @check="handleMenuTreeCheck"
              @node-expand="handleMenuNodeExpand"
              @node-collapse="handleMenuNodeCollapse"
            >
              <template #default="{ data }">
                <span>{{ data.label || data.menuName }}</span>
              </template>
            </ElTree>
          </div>
        </div>
      </ElFormItem>
      <ElFormItem label="是否关联部门树">
        <ElSwitch v-model="deptSwitch" />
      </ElFormItem>
      <ElFormItem label="部门权限" v-if="deptSwitch">
        <div class="permission-tree-wrapper">
          <!-- 控制选项 -->
          <div class="permission-controls">
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
                <span>{{ data.label || data.deptName }}</span>
              </template>
            </ElTree>
          </div>
        </div>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" @click="handleSubmit">提交</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { fetchAddRole, fetchUpdateRole } from '@/api/role/role'
  import { fetchGetMenuTree, type SysMenu } from '@/api/menu/menu'
  import { fetchGetDeptTree, type SysDept } from '@/api/dept/dept'
  import { fetchGetRoleMenus, fetchGetRoleDeptIds } from '@/api/role/role'
  import { useSettingStore } from '@/store/modules/setting'

  type RoleListItem = Api.SystemManage.RoleListItem

  interface Props {
    modelValue: boolean
    dialogType: 'add' | 'edit'
    roleData?: RoleListItem
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    dialogType: 'add',
    roleData: undefined
  })

  const emit = defineEmits<Emits>()

  const roleForm = ref<FormInstance>()
  const menuTreeRef = ref()
  const deptTreeRef = ref()
  const menuTreeContainerRef = ref<HTMLElement>()
  const deptTreeContainerRef = ref<HTMLElement>()

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 菜单树数据
  const menuTreeData = ref<SysMenu[]>([])
  const menuTreeProps = {
    children: 'children',
    label: 'menuName'
  }

  // 部门树数据
  const deptTreeData = ref<SysDept[]>([])
  const deptTreeProps = {
    children: 'children',
    label: 'deptName'
  }

  // 菜单树控制状态
  const menuExpandAll = ref(false)
  const menuSelectAll = ref(false)
  const menuSelectAllIndeterminate = ref(false) // 菜单全选按钮半选状态
  const isMenuParentChildLinked = ref(true) // 菜单树父子联动，true表示启用联动，false表示禁用联动
  const menuCheckStrictly = computed(() => !isMenuParentChildLinked.value) // Element Plus的check-strictly为false时表示联动，为true时表示不联动

  // 部门树控制状态
  const deptExpandAll = ref(false)
  const deptSelectAll = ref(false)
  const deptSelectAllIndeterminate = ref(false) // 部门全选按钮半选状态
  const isDeptParentChildLinked = ref(true) // 部门树父子联动，true表示启用联动，false表示禁用联动
  const deptCheckStrictly = computed(() => !isDeptParentChildLinked.value) // Element Plus的check-strictly为false时表示联动，为true时表示不联动
  /**
   * 弹窗显示状态双向绑定
   */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  /**
   * 状态开关双向绑定
   * status为0时选中（true），为1时不选中（false）
   */
  const statusSwitch = computed({
    get: () => {
      // 兼容字符串和数字类型
      const status = Number(form.status)
      return status === 0
    },
    set: (value: boolean) => {
      ;(form as any).status = value ? 0 : 1
    }
  })
  //是否关联菜单树开关
  const menuSwitch = computed({
    get: () => {
      // 兼容字符串和数字类型
      const menuCheckStrictly = Number(form.menuCheckStrictly)
      return menuCheckStrictly === 0
    },
    set: (value: boolean) => {
      ;(form as any).menuCheckStrictly = value ? 0 : 1
    }
  })
  //是否关联部门树开关
  const deptSwitch = computed({
    get: () => {
      // 兼容字符串和数字类型
      const deptCheckStrictly = Number(form.deptCheckStrictly)
      return deptCheckStrictly === 0
    },
    set: (value: boolean) => {
      ;(form as any).deptCheckStrictly = value ? 0 : 1
    }
  })
  /**
   * 表单验证规则
   */
  const rules = reactive<FormRules>({
    roleName: [
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    roleKey: [
      { required: true, message: '请输入角色编码', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    remark: [{ required: true, message: '请输入角色描述', trigger: 'blur' }]
  })

  /**
   * 表单数据
   */
  const form = reactive({
    roleId: 0,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    remark: '',
    createTime: '',
    status: 0,
    menuCheckStrictly: 0, // 菜单树关联显示（0关联 1不关联）
    deptCheckStrictly: 0, // 部门树关联显示（0关联 1不关联）
    menuIds: [] as number[], // 菜单ID列表
    deptIds: [] as number[] // 部门ID列表
  } as unknown as RoleListItem & { menuIds: number[]; deptIds: number[] })

  // 防止重复加载的标记
  const isLoadingMenuTree = ref(false)
  const isLoadingDeptTree = ref(false)

  /**
   * 监听弹窗打开，初始化表单数据
   */
  watch(
    () => props.modelValue,
    async (newVal) => {
      if (newVal) {
        initForm()
        // 重置树数据
        menuTreeData.value = []
        deptTreeData.value = []
        // 重置加载标记
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        // 等待弹窗完全打开后再加载数据
        await nextTick()
        // 防止重复调用
        if (!isLoadingMenuTree.value) {
          isLoadingMenuTree.value = true
          loadMenuTree().finally(() => {
            isLoadingMenuTree.value = false
          })
        }
        if (!isLoadingDeptTree.value) {
          isLoadingDeptTree.value = true
          loadDeptTree().finally(() => {
            isLoadingDeptTree.value = false
          })
        }
      } else {
        // 关闭时清空数据
        menuTreeData.value = []
        deptTreeData.value = []
        isLoadingMenuTree.value = false
        isLoadingDeptTree.value = false
        if (menuTreeContainerRef.value) {
          menuTreeContainerRef.value.style.height = 'auto'
        }
        if (deptTreeContainerRef.value) {
          deptTreeContainerRef.value.style.height = 'auto'
        }
      }
    }
  )

  /**
   * 监听角色数据变化，更新表单
   * 注意：只在弹窗已打开且角色数据变化时更新，避免与 modelValue watch 重复调用
   */
  watch(
    () => props.roleData,
    (newData) => {
      // 只在弹窗已打开且角色数据存在时更新，避免重复初始化
      if (newData && props.modelValue && props.dialogType === 'edit') {
        // 只更新表单数据，不重新加载树（树已在 modelValue watch 中加载）
        const status = newData.status != null ? Number(newData.status) : 0
        Object.assign(form, {
          ...newData,
          status: isNaN(status) ? 0 : status,
          menuCheckStrictly: newData.menuCheckStrictly ?? 0,
          deptCheckStrictly: newData.deptCheckStrictly ?? 0,
          menuIds: [],
          deptIds: []
        })
        // 如果树数据已加载，重新加载角色关联的菜单和部门
        if (menuTreeData.value.length > 0 && form.roleId) {
          loadRoleMenuIds()
        }
        if (deptTreeData.value.length > 0 && form.roleId) {
          loadRoleDeptIds()
        }
      }
    },
    { deep: true }
  )

  /**
   * 加载菜单树数据
   */
  const loadMenuTree = async () => {
    try {
      const menuList = await fetchGetMenuTree()
      if (Array.isArray(menuList) && menuList.length > 0) {
        menuTreeData.value = menuList
        // 等待 DOM 更新
        await nextTick()
        // 如果是编辑模式，加载已选中的菜单
        if (props.dialogType === 'edit' && form.roleId) {
          await loadRoleMenuIds()
          // 等待选中状态更新
          await nextTick()
        }
        // 延迟调整容器高度，确保 DOM 完全渲染
        setTimeout(() => {
          adjustMenuTreeContainerHeight()
        }, 100)
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载菜单树失败:', error)
      }
    }
  }

  /**
   * 加载部门树数据
   */
  const loadDeptTree = async () => {
    try {
      const deptList = await fetchGetDeptTree()
      if (Array.isArray(deptList) && deptList.length > 0) {
        deptTreeData.value = deptList
        // 等待 DOM 更新
        await nextTick()
        // 如果是编辑模式，加载已选中的部门
        if (props.dialogType === 'edit' && form.roleId) {
          await loadRoleDeptIds()
          // 等待选中状态更新
          await nextTick()
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
    }
  }

  /**
   * 加载角色已分配的菜单ID列表
   */
  const loadRoleMenuIds = async () => {
    try {
      const menuIds = await fetchGetRoleMenus(form.roleId)
      // 确保menuIds是数组类型，处理null或undefined情况
      const safeMenuIds = Array.isArray(menuIds) ? menuIds : []
      console.log('加载角色菜单ID:', safeMenuIds)
      
      // 设置树节点选中状态
      // 先关闭父子联动，确保只选中指定的ID
      const originalLink = isMenuParentChildLinked.value
      isMenuParentChildLinked.value = false
      // 等待menuCheckStrictly计算属性更新
      await nextTick()
      // 设置选中状态
      if (menuTreeRef.value) {
        menuTreeRef.value.setCheckedKeys(safeMenuIds)
        // 恢复父子联动
        isMenuParentChildLinked.value = originalLink
        // 等待menuCheckStrictly计算属性更新和Tree组件自动更新父子节点状态
        await nextTick()
        
        // 获取包含半选状态的所有节点ID
        const checkedKeys = menuTreeRef.value.getCheckedKeys() || []
        const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() || []
        // 将半选状态的节点ID也添加到表单数据中
        form.menuIds = [...checkedKeys, ...halfCheckedKeys].filter((key: any) => typeof key === 'number') as number[]
        
        // 更新全选按钮状态
        handleMenuTreeCheck()
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载角色菜单ID失败:', error)
      }
    }
  }

  /**
   * 加载角色已分配的部门ID列表
   */
  const loadRoleDeptIds = async () => {
    try {
      const deptIds = await fetchGetRoleDeptIds(form.roleId)
      // 确保deptIds是数组类型，处理null或undefined情况
      const safeDeptIds = Array.isArray(deptIds) ? deptIds : []
      console.log('加载角色部门ID:', safeDeptIds)
      
      // 设置树节点选中状态
      // 先关闭父子联动，确保只选中指定的ID
      const originalLink = isDeptParentChildLinked.value
      isDeptParentChildLinked.value = false
      // 等待deptCheckStrictly计算属性更新
      await nextTick()
      // 设置选中状态
      if (deptTreeRef.value) {
        deptTreeRef.value.setCheckedKeys(safeDeptIds)
        // 恢复父子联动
        isDeptParentChildLinked.value = originalLink
        // 等待deptCheckStrictly计算属性更新和Tree组件自动更新父子节点状态
        await nextTick()
        
        // 获取包含半选状态的所有节点ID
        const checkedKeys = deptTreeRef.value.getCheckedKeys() || []
        const halfCheckedKeys = deptTreeRef.value.getHalfCheckedKeys() || []
        // 将半选状态的节点ID也添加到表单数据中
        form.deptIds = [...checkedKeys, ...halfCheckedKeys].filter((key: any) => typeof key === 'number') as number[]
        
        // 更新全选按钮状态
        handleDeptTreeCheck()
      }
    } catch (error) {
      // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('加载角色部门ID失败:', error)
      }
    }
  }

  /**
   * 处理菜单树选中变化
   */
  const handleMenuTreeCheck = () => {
    // 获取树的选中状态
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
    form.menuIds = [...checkedKeys, ...halfCheckedKeys].filter(
      (key: any) => typeof key === 'number'
    ) as number[]

    // 更新全选状态
    const allKeys = getAllMenuNodeKeys(menuTreeData.value)
    // 全选状态：所有节点都被完全选中
    menuSelectAll.value = checkedKeys.length === allKeys.length && allKeys.length > 0
    // 半选状态：部分节点被选中或半选中，且不是全选状态
    menuSelectAllIndeterminate.value =
      (checkedKeys.length > 0 || halfCheckedKeys.length > 0) && !menuSelectAll.value
  }

  /**
   * 处理部门树选中变化
   */
  const handleDeptTreeCheck = () => {
    // 获取树的选中状态
    const checkedKeys = deptTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = deptTreeRef.value?.getHalfCheckedKeys() || []
    form.deptIds = [...checkedKeys, ...halfCheckedKeys].filter(
      (key: any) => typeof key === 'number'
    ) as number[]

    // 更新全选状态
    const allKeys = getAllDeptNodeKeys(deptTreeData.value)
    // 全选状态：所有节点都被完全选中
    deptSelectAll.value = checkedKeys.length === allKeys.length && allKeys.length > 0
    // 半选状态：部分节点被选中或半选中，且不是全选状态
    deptSelectAllIndeterminate.value =
      (checkedKeys.length > 0 || halfCheckedKeys.length > 0) && !deptSelectAll.value
  }

  /**
   * 切换菜单树展开/收起
   */
  const toggleMenuExpandAll = () => {
    const tree = menuTreeRef.value
    if (!tree) return
    const nodes = tree.store.nodesMap
    Object.values(nodes).forEach((node: any) => {
      node.expanded = menuExpandAll.value
    })
    // 延迟调整容器高度，等待树节点展开/收起动画完成（Element Plus 动画约 300ms）
    setTimeout(() => {
      adjustMenuTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理菜单树节点展开
   */
  const handleMenuNodeExpand = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustMenuTreeContainerHeight()
    }, 350)
  }

  /**
   * 处理菜单树节点收起
   */
  const handleMenuNodeCollapse = () => {
    // 延迟调整容器高度，等待动画完成
    setTimeout(() => {
      adjustMenuTreeContainerHeight()
    }, 350)
  }

  /**
   * 调整菜单树容器高度
   */
  const adjustMenuTreeContainerHeight = () => {
    if (!menuTreeContainerRef.value) return
    const treeEl = menuTreeContainerRef.value.querySelector('.el-tree')
    if (treeEl) {
      // 先移除固定高度，让容器自适应
      menuTreeContainerRef.value.style.height = 'auto'
      // 获取实际内容高度
      const height = treeEl.scrollHeight
      // 设置新的高度，但不超过最大值
      menuTreeContainerRef.value.style.height = `${Math.min(height + 24, 600)}px`
    }
  }

  /**
   * 切换菜单树全选/全不选
   */
  const toggleMenuSelectAll = () => {
    const tree = menuTreeRef.value
    if (!tree) return

    if (menuSelectAll.value) {
      const allKeys = getAllMenuNodeKeys(menuTreeData.value)
      tree.setCheckedKeys(allKeys)
      menuSelectAllIndeterminate.value = false
    } else {
      tree.setCheckedKeys([])
      menuSelectAllIndeterminate.value = false
    }
  }

  /**
   * 处理菜单树父子联动变化
   */
  const handleMenuCheckStrictlyChange = () => {
    const tree = menuTreeRef.value
    if (tree) {
      // 重新设置选中状态以应用新的联动模式
      const checkedKeys = tree.getCheckedKeys()
      const halfCheckedKeys = tree.getHalfCheckedKeys()
      tree.setCheckedKeys([])
      nextTick(() => {
        // 合并完全选中和半选中的节点ID，确保父级菜单ID被传递
        const allKeys = [...checkedKeys, ...halfCheckedKeys]
        tree.setCheckedKeys(allKeys)
      })
    }
  }

  /**
   * 切换部门树展开/收起
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
   * 切换部门树全选/全不选
   */
  const toggleDeptSelectAll = () => {
    const tree = deptTreeRef.value
    if (!tree) return

    if (deptSelectAll.value) {
      const allKeys = getAllDeptNodeKeys(deptTreeData.value)
      tree.setCheckedKeys(allKeys)
      deptSelectAllIndeterminate.value = false
    } else {
      tree.setCheckedKeys([])
      deptSelectAllIndeterminate.value = false
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
      const halfCheckedKeys = tree.getHalfCheckedKeys()
      tree.setCheckedKeys([])
      nextTick(() => {
        // 合并完全选中和半选中的节点ID，确保父级菜单ID被传递
        const allKeys = [...checkedKeys, ...halfCheckedKeys]
        tree.setCheckedKeys(allKeys)
      })
    }
  }

  /**
   * 递归获取所有菜单节点的 key
   */
  const getAllMenuNodeKeys = (nodes: SysMenu[]): number[] => {
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
   * 递归获取所有部门节点的 key
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
   * 初始化表单数据
   * 根据弹窗类型填充表单或重置表单
   */
  const initForm = () => {
    if (props.dialogType === 'edit' && props.roleData) {
      // 确保 status 字段被正确赋值，兼容字符串和数字类型
      const status = props.roleData.status != null ? Number(props.roleData.status) : 0
      Object.assign(form, {
        ...props.roleData,
        status: isNaN(status) ? 0 : status,
        menuCheckStrictly: props.roleData.menuCheckStrictly ?? 0,
        deptCheckStrictly: props.roleData.deptCheckStrictly ?? 0,
        menuIds: [],
        deptIds: []
      })
    } else {
      Object.assign(form, {
        roleId: 0,
        roleName: '',
        roleKey: '',
        roleSort: 0,
        remark: '',
        createTime: '',
        status: 0,
        menuCheckStrictly: 0,
        deptCheckStrictly: 0,
        menuIds: [],
        deptIds: []
      })
    }
  }

  /**
   * 关闭弹窗并重置表单
   */
  const handleClose = () => {
    visible.value = false
    roleForm.value?.resetFields()
    // 清空树选中状态
    menuTreeRef.value?.setCheckedKeys([])
    deptTreeRef.value?.setCheckedKeys([])
    // 重置控制状态
    menuExpandAll.value = false
    menuSelectAll.value = false
    deptExpandAll.value = false
    deptSelectAll.value = false
  }

  /**
   * 提交表单
   * 验证通过后调用接口保存数据
   */
  const handleSubmit = async () => {
    if (!roleForm.value) return

    try {
      await roleForm.value.validate()
      // 获取选中的菜单ID和部门ID
      const menuIds = menuTreeRef.value?.getCheckedKeys() || []
      const deptIds = deptTreeRef.value?.getCheckedKeys() || []

      const submitData = {
        ...form,
        menuIds: menuIds.filter((key: any) => typeof key === 'number') as number[],
        deptIds: deptIds.filter((key: any) => typeof key === 'number') as number[]
      }

      // 根据弹窗类型调用对应的API
      if (props.dialogType === 'add') {
        await fetchAddRole(submitData)
      } else {
        await fetchUpdateRole(submitData)
      }
      emit('success')
      handleClose()
    } catch (error) {
      // 表单验证失败：Element Plus 会通过 UI 显示错误，这里不需要处理
      // API 调用失败：错误已在 HTTP 拦截器中统一处理并显示错误消息
      if (import.meta.env.DEV) {
        console.error('提交失败:', error)
      }
    }
  }
</script>

<style scoped lang="scss">
  .permission-tree-wrapper {
    .permission-controls {
      display: flex;
      gap: 20px;
      margin-bottom: 12px;
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
    }
  }
</style>
