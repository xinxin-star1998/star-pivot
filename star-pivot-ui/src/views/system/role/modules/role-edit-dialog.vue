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
  import { useCheckableTree } from '@/composables/useCheckableTree'

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
  const menuTree = useCheckableTree<SysMenu>({ keyField: 'menuId', childrenField: 'children' })
  const deptTree = useCheckableTree<SysDept>({ keyField: 'deptId', childrenField: 'children' })

  // 兼容原模板变量命名（保持模板不动）
  const menuTreeRef = menuTree.treeRef
  const deptTreeRef = deptTree.treeRef
  const menuTreeContainerRef = menuTree.containerRef
  const deptTreeContainerRef = deptTree.containerRef

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 菜单树数据
  const menuTreeData = menuTree.data
  const menuTreeProps = {
    children: 'children',
    label: 'menuName'
  }

  // 部门树数据
  const deptTreeData = deptTree.data
  const deptTreeProps = {
    children: 'children',
    label: 'deptName'
  }

  // 菜单树控制状态
  const menuExpandAll = menuTree.expandAll
  const menuSelectAll = menuTree.selectAll
  const menuSelectAllIndeterminate = menuTree.indeterminate
  const isMenuParentChildLinked = menuTree.parentChildLinked
  const menuCheckStrictly = menuTree.checkStrictly

  // 部门树控制状态
  const deptExpandAll = deptTree.expandAll
  const deptSelectAll = deptTree.selectAll
  const deptSelectAllIndeterminate = deptTree.indeterminate
  const isDeptParentChildLinked = deptTree.parentChildLinked
  const deptCheckStrictly = deptTree.checkStrictly
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
          menuTree.adjustContainerHeight()
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
          deptTree.adjustContainerHeight()
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

      await menuTree.applyCheckedKeys(safeMenuIds)
      // 同步表单 + 全选/半选
      handleMenuTreeCheck()
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

      await deptTree.applyCheckedKeys(safeDeptIds)
      // 同步表单 + 全选/半选
      handleDeptTreeCheck()
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
    menuTree.syncSelectState()
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
    deptTree.syncSelectState()
  }

  /**
   * 切换菜单树展开/收起
   */
  const toggleMenuExpandAll = () => {
    menuTree.toggleExpandAll()
  }

  /**
   * 处理菜单树节点展开
   */
  const handleMenuNodeExpand = () => {
    menuTree.handleNodeExpand()
  }

  /**
   * 处理菜单树节点收起
   */
  const handleMenuNodeCollapse = () => {
    menuTree.handleNodeCollapse()
  }

  /**
   * 切换菜单树全选/全不选
   */
  const toggleMenuSelectAll = () => {
    menuTree.toggleSelectAll()
    handleMenuTreeCheck()
  }

  /**
   * 处理菜单树父子联动变化
   */
  const handleMenuCheckStrictlyChange = () => {
    menuTree.handleCheckStrictlyChange()
    handleMenuTreeCheck()
  }

  /**
   * 切换部门树展开/收起
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
   * 切换部门树全选/全不选
   */
  const toggleDeptSelectAll = () => {
    deptTree.toggleSelectAll()
    handleDeptTreeCheck()
  }

  /**
   * 处理部门树父子联动变化
   */
  const handleDeptCheckStrictlyChange = () => {
    deptTree.handleCheckStrictlyChange()
    handleDeptTreeCheck()
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
