<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="860px"
    align-center
    class="menu-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="width > 640 ? 12 : 24"
      :gutter="20"
      label-width="100px"
      :show-reset="false"
      :show-submit="false"
    >
      <template #menuType>
        <ElRadioGroup v-model="form.menuType" :disabled="disableMenuType">
          <ElRadioButton value="M" :disabled="menuTypeDisabled.M">目录</ElRadioButton>
          <ElRadioButton value="C" :disabled="menuTypeDisabled.C">菜单</ElRadioButton>
          <ElRadioButton value="F" :disabled="menuTypeDisabled.F">按钮</ElRadioButton>
        </ElRadioGroup>
      </template>
      <template #icon>
        <ArtIconPicker ref="iconPickerRef" v-model="form.icon">
          <ElInput
            v-model="form.icon"
            placeholder="如：ri:user-line"
            clearable
            style="width: 100%"
            @focus="handleIconInputFocus"
          >
            <template #prefix>
              <Icon v-if="form.icon" :icon="form.icon" style="font-size: 18px" />
            </template>
          </ElInput>
        </ArtIconPicker>
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">取 消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确 定</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElIcon, ElTooltip, ElMessage, ElInput } from 'element-plus'
  import { QuestionFilled } from '@element-plus/icons-vue'
  import { Icon } from '@iconify/vue'
  import { formatMenuTitle } from '@/utils/router'
  import { safeError } from '@/utils'
  import type { AppRouteRecord } from '@/types/router'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import ArtIconPicker from '@/components/core/base/art-icon-picker/index.vue'
  import { useWindowSize } from '@vueuse/core'
  import type { MenuFormData } from '../types'
  import { fetchGetParentMenu, type SysMenu } from '@/api/menu/menu'

  const { width } = useWindowSize()

  /**
   * 创建带 tooltip 的表单标签
   * @param label 标签文本
   * @param tooltip 提示文本
   * @returns 渲染函数
   */
  const createLabelTooltip = (label: string, tooltip: string) => {
    return () =>
      h('span', { class: 'flex items-center' }, [
        h('span', label),
        h(
          ElTooltip,
          {
            content: tooltip,
            placement: 'top'
          },
          () => h(ElIcon, { class: 'ml-0.5 cursor-help' }, () => h(QuestionFilled))
        )
      ])
  }

  interface Props {
    visible: boolean
    editData?: AppRouteRecord | any
    rawMenuData?: SysMenu[] // 原始菜单数据，用于回显
    type?: 'menu' | 'button'
    lockType?: boolean
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit', data: MenuFormData): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    type: 'menu',
    lockType: false
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)
  const iconPickerRef = ref()

  // 处理图标输入框聚焦
  const handleIconInputFocus = () => {
    iconPickerRef.value?.open()
  }
  // 树形选择器的数据结构
  interface TreeNode {
    label: string
    value: number
    menuType?: string // 新增menuType字段，用于判断上级菜单类型
    children?: TreeNode[]
  }
  const parentMenuOptions = ref<TreeNode[]>([])
  const originalMenus = ref<SysMenu[]>([])

  const form = reactive<MenuFormData>({
    menuType: 'M',
    menuName: '',
    parentId: undefined,
    orderNum: 1,
    path: '',
    component: '',
    query: '',
    routeName: '',
    isFrame: 1,
    isCache: 1,
    visible: '0',
    status: '0',
    perms: '',
    icon: '',
    remark: ''
  })

  /**
   * 根据menuId查找菜单类型
   */
  const findMenuTypeById = (menuId: number, menuList: SysMenu[]): string | undefined => {
    for (const menu of menuList) {
      if (menu.menuId === menuId) {
        return menu.menuType
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuTypeById(menuId, menu.children)
        if (found) return found
      }
    }
    return undefined
  }

  // 路径验证函数
  const validatePath = (rule: any, value: string, callback: any) => {
    if (!value && form.menuType === 'C') {
      callback(new Error('菜单类型必须填写路由地址'))
      return
    }
    if (value) {
      // 检查是否包含非法字符
      if (/[<>"']/.test(value)) {
        callback(new Error('路由地址不能包含特殊字符 < > " \''))
        return
      }
      // 一级菜单必须以 / 开头
      if (!form.parentId && !value.startsWith('/')) {
        callback(new Error('一级菜单的路由地址必须以 / 开头'))
        return
      }
      // 二级及以下菜单不能以 / 开头（除非是外链）
      if (form.parentId && value.startsWith('/') && !value.startsWith('http')) {
        callback(new Error('二级及以下菜单的路由地址不能以 / 开头'))
        return
      }
    }
    callback()
  }

  // 组件路径验证函数
  const validateComponent = (rule: any, value: string, callback: any) => {
    if (form.menuType === 'C' && !value) {
      callback(new Error('菜单类型必须填写组件路径'))
      return
    }
    if (value) {
      // 组件路径必须以 / 开头
      if (!value.startsWith('/')) {
        callback(new Error('组件路径必须以 / 开头'))
        return
      }
      // 检查是否包含非法字符
      if (/[<>"']/.test(value)) {
        callback(new Error('组件路径不能包含特殊字符 < > " \''))
        return
      }
    }
    callback()
  }

  // 权限标识验证函数
  const validatePerms = (rule: any, value: string, callback: any) => {
    if (form.menuType === 'F' && !value) {
      callback(new Error('按钮类型必须填写权限标识'))
      return
    }
    if (value) {
      // 权限标识格式验证：允许字母、数字、冒号、下划线
      if (!/^[a-zA-Z0-9:_-]+$/.test(value)) {
        callback(new Error('权限标识只能包含字母、数字、冒号、下划线和横线'))
        return
      }
    }
    callback()
  }

  const rules = reactive<FormRules>({
    menuName: [
      { required: true, message: '请输入菜单名称', trigger: 'blur' },
      { max: 50, message: '菜单名称长度不能超过50个字符', trigger: 'blur' }
    ],
    menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
    path: [{ validator: validatePath, trigger: 'blur' }],
    component: [{ validator: validateComponent, trigger: 'blur' }],
    perms: [{ validator: validatePerms, trigger: 'blur' }]
  })

  /**
   * 根据菜单类型动态生成表单项
   */
  const formItems = computed<FormItem[]>(() => {
    // 确保选项数据存在
    const menuOptions = parentMenuOptions.value || []

    // 菜单类型和上级菜单放在同一行
    const baseItems: FormItem[] = [
      { label: '菜单类型', key: 'menuType', span: 12 },
      {
        label: '上级菜单',
        key: 'parentId',
        type: 'treeselect',
        span: 12,
        props: {
          placeholder: '请选择上级菜单，不选则为顶级菜单',
          clearable: true,
          data: menuOptions,
          'render-after-expand': false,
          'check-strictly': true,
          'default-expand-all': false
        }
      }
    ]

    // 按钮类型（F）只显示基本字段
    if (form.menuType === 'F') {
      return [
        ...baseItems,
        {
          label: '按钮名称',
          key: 'menuName',
          type: 'input',
          props: { placeholder: '请输入按钮名称' }
        },
        {
          label: '权限标识',
          key: 'perms',
          type: 'input',
          props: { placeholder: '如：system:user:add' }
        },
        {
          label: '显示顺序',
          key: 'orderNum',
          type: 'number',
          props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
        },
        {
          label: '备注',
          key: 'remark',
          type: 'input',
          span: 24,
          props: { type: 'textarea', rows: 3, placeholder: '请输入备注' }
        }
      ]
    }

    // 目录（M）和菜单（C）类型
    const switchSpan = width.value < 640 ? 12 : 6

    return [
      ...baseItems,
      {
        label: '菜单名称',
        key: 'menuName',
        type: 'input',
        props: { placeholder: '请输入菜单名称' }
      },
      {
        label: createLabelTooltip(
          '路由地址',
          '一级菜单：以 / 开头的绝对路径（如 /dashboard）\n二级及以下：相对路径（如 console、user）'
        ),
        key: 'path',
        type: 'input',
        props: { placeholder: '如：/dashboard 或 console' }
      },
      {
        label: createLabelTooltip(
          '路由名称',
          '路由的唯一标识，用于路由跳转，建议使用大驼峰命名（如：UserManage）'
        ),
        key: 'routeName',
        type: 'input',
        props: { placeholder: '如：UserManage' }
      },
      {
        label: createLabelTooltip(
          '组件路径',
          '一级父级菜单：填写 /index/index\n具体页面：填写组件路径（如 /system/user）\n目录菜单：留空'
        ),
        key: 'component',
        type: 'input',
        props: {
          placeholder:
            '如：一级父级菜单：填写 /index/index\n具体页面：填写组件路径（如 /system/user）\n目录菜单：留空'
        }
      },
      {
        label: '路由参数',
        key: 'query',
        type: 'input',
        props: { placeholder: '如：id=1&type=edit' }
      },
      { label: '菜单图标', key: 'icon', type: 'input', props: { placeholder: '如：ri:user-line' } },
      {
        label: '权限标识',
        key: 'perms',
        type: 'input',
        props: { placeholder: '如：system:user:list' }
      },
      {
        label: '显示顺序',
        key: 'orderNum',
        type: 'number',
        props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
      },
      {
        label: '是否外链',
        key: 'isFrame',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: 0,
          inactiveValue: 1
        }
      },
      {
        label: '是否缓存',
        key: 'isCache',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: 0,
          inactiveValue: 1
        }
      },
      {
        label: '是否显示',
        key: 'visible',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: '0',
          inactiveValue: '1'
        }
      },
      {
        label: '是否启用',
        key: 'status',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: '0',
          inactiveValue: '1'
        }
      },
      {
        label: '备注',
        key: 'remark',
        type: 'input',
        span: 24,
        props: { type: 'textarea', rows: 3, placeholder: '请输入备注0/500' }
      }
    ]
  })

  const dialogTitle = computed(() => {
    const menuTypeMap = {
      M: '目录',
      C: '菜单',
      F: '按钮'
    }
    const type = menuTypeMap[form.menuType] || '菜单'
    return isEdit.value ? `编辑${type}` : `新建${type}`
  })

  /**
   * 获取当前选择的上级菜单类型
   */
  const getParentMenuType = computed(() => {
    if (form.parentId === 0 || form.parentId === undefined) {
      return undefined // 顶级菜单，无上级
    }
    return findMenuTypeById(form.parentId, originalMenus.value)
  })

  /**
   * 是否禁用菜单类型切换
   */
  const disableMenuType = computed(() => {
    if (isEdit.value) return true
    if (!isEdit.value && form.menuType === 'M' && props.lockType) return true
    return false
  })

  /**
   * 菜单类型选项的禁用状态
   */
  const menuTypeDisabled = computed(() => {
    const parentType = getParentMenuType.value
    if (parentType === 'C') {
      // 如果上级菜单是'C'（菜单），则禁用'M'（目录）选项
      return {
        M: true, // 禁用目录
        C: false, // 允许菜单
        F: false // 允许按钮
      }
    }
    return {
      M: false,
      C: false,
      F: false
    }
  })

  /**
   * 加载上级菜单选项（树形结构）
   * 接口已返回 label 和 value 字段，转换为树形结构
   */
  const loadParentMenuOptions = async (): Promise<void> => {
    try {
      const menus = await fetchGetParentMenu()
      // 保存原始菜单数据，用于后续查找菜单类型
      originalMenus.value = menus

      // 将菜单数据转换为树形结构（跳过 menuId 为 0 的顶级虚拟节点）
      const convertToTree = (menuList: SysMenu[]): TreeNode[] => {
        const treeNodes: TreeNode[] = []

        if (!Array.isArray(menuList)) return treeNodes

        menuList.forEach((menu) => {
          // 直接使用接口返回的 label 和 value
          if ((menu as any).label && (menu as any).value !== undefined) {
            const node: TreeNode = {
              label: (menu as any).label,
              value: (menu as any).value,
              menuType: menu.menuType // 保存菜单类型
            }
            // 递归处理子菜单
            if (menu.children && Array.isArray(menu.children) && menu.children.length > 0) {
              const children = convertToTree(menu.children)
              if (children.length > 0) {
                node.children = children
              }
            }

            treeNodes.push(node)
          }
        })

        return treeNodes
      }

      const treeData = convertToTree(menus)

      // 确保至少有一个选项
      if (treeData.length === 0) {
        treeData.push({ label: '无上级菜单', value: 0 })
      }

      parentMenuOptions.value = treeData
    } catch (error) {
      safeError('加载上级菜单失败:', error)
      parentMenuOptions.value = [{ label: '无上级菜单', value: 0 }]
    }
  }

  /**
   * 重置表单数据
   */
  const resetForm = (): void => {
    Object.assign(form, {
      menuType: props.type === 'button' ? 'F' : 'M',
      menuName: '',
      parentId: undefined,
      orderNum: 1,
      path: '',
      component: '',
      query: '',
      routeName: '',
      isFrame: 1,
      isCache: 1,
      visible: '0',
      status: '0',
      perms: '',
      icon: '',
      remark: ''
    })
    // ArtForm 组件暴露的是 reset() 方法，而不是 resetFields()
    // 如果需要清除验证状态，可以通过 ref 访问内部的 formInstance
    nextTick(() => {
      if (formRef.value?.ref) {
        formRef.value.ref.resetFields()
      }
    })
    isEdit.value = false
  }

  /**
   * 从原始菜单数据中查找菜单项
   * @param menuId 菜单ID
   * @param menuList 菜单列表
   * @returns 菜单项
   */
  const findRawMenu = (menuId: number | undefined, menuList: SysMenu[]): SysMenu | undefined => {
    if (!menuId || !menuList) return undefined

    for (const menu of menuList) {
      if (menu.menuId === menuId) {
        return menu
      }
      if (menu.children && menu.children.length > 0) {
        const found = findRawMenu(menuId, menu.children)
        if (found) return found
      }
    }
    return undefined
  }

  /**
   * 加载表单数据（编辑模式）
   * 优先使用接口返回的原始数据
   */
  const loadFormData = (): void => {
    if (!props.editData) return

    const row = props.editData

    // 只有当 row.id 存在时，才认为是编辑模式
    // 如果只有 parentId 而没有 id，说明是新增模式
    if (row.id) {
      isEdit.value = true
    } else {
      isEdit.value = false
    }

    // 从原始数据中查找菜单项
    const rawMenu = row.id && props.rawMenuData ? findRawMenu(row.id, props.rawMenuData) : null

    // 如果是按钮类型
    if (props.type === 'button' || row.meta?.isAuthButton) {
      form.menuType = 'F'
      form.menuId = row.id || undefined
      // 优先使用原始数据
      form.menuName = rawMenu?.menuName || row.meta?.title || row.title || row.menuName || ''
      form.perms = rawMenu?.perms || row.meta?.authMark || row.authMark || row.perms || ''
      form.parentId = rawMenu?.parentId || row.parentId || undefined
      form.orderNum = rawMenu?.orderNum || row.meta?.orderNum || row.orderNum || 1
      form.remark = rawMenu?.remark || row.remark || ''
      return
    }

    // 目录或菜单类型 - 优先使用原始数据
    form.menuId = row.id || undefined
    form.menuName = rawMenu?.menuName || formatMenuTitle(row.meta?.title || row.menuName || '')
    form.parentId = rawMenu?.parentId || row.parentId || undefined
    form.orderNum = rawMenu?.orderNum || row.meta?.orderNum || row.orderNum || 1
    form.path = rawMenu?.path || row.path || ''
    form.component = rawMenu?.component || row.component || ''
    form.query = rawMenu?.query || row.query || ''
    form.routeName = rawMenu?.routeName || row.name || row.routeName || ''
    form.isFrame =
      rawMenu?.isFrame !== undefined
        ? rawMenu.isFrame
        : row.meta?.isIframe === true
          ? 0
          : (row.isFrame ?? 1)
    form.isCache =
      rawMenu?.isCache !== undefined
        ? rawMenu.isCache
        : row.meta?.keepAlive === true
          ? 0
          : (row.isCache ?? 1)
    form.visible = rawMenu?.visible || (row.meta?.isHide === true ? '1' : (row.visible ?? '0'))
    form.status = rawMenu?.status || row.status || '0'
    form.perms = rawMenu?.perms || row.meta?.authList?.[0]?.authMark || row.perms || ''
    form.icon = rawMenu?.icon || row.meta?.icon || row.icon || ''
    form.remark = rawMenu?.remark || row.remark || ''

    // 根据原始数据中的 menuType 判断，如果没有则根据是否有component判断
    if (rawMenu?.menuType) {
      form.menuType = rawMenu.menuType as 'M' | 'C' | 'F'
    } else if (!form.component || form.component === '') {
      form.menuType = 'M'
    } else {
      form.menuType = 'C'
    }
  }

  /**
   * 提交表单
   */
  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      // 构建提交数据
      const submitData: MenuFormData = {
        menuType: form.menuType,
        menuName: form.menuName,
        parentId: form.parentId === 0 ? undefined : form.parentId,
        orderNum: form.orderNum,
        path: form.path || undefined,
        // 目录类型或组件路径为空时，明确传递空字符串以清空后端数据
        // 如果传递 undefined，后端可能不会更新该字段，导致原来的值保留
        component:
          form.menuType === 'M' || !form.component || form.component.trim() === ''
            ? ''
            : form.component,
        query: form.query || undefined,
        routeName: form.routeName || undefined,
        isFrame: form.isFrame,
        isCache: form.isCache,
        visible: form.visible,
        status: form.status,
        // 如果 perms 为空，明确传递空字符串以清空后端数据
        // 如果传递 undefined，后端可能不会更新该字段，导致原来的值保留
        perms: form.perms && form.perms.trim() !== '' ? form.perms : '',
        icon: form.icon || undefined,
        remark: form.remark || undefined
      }

      // 如果是编辑模式，添加menuId
      if (isEdit.value && form.menuId) {
        submitData.menuId = form.menuId
      }

      emit('submit', submitData)
      handleCancel()
    } catch {
      ElMessage.error('表单校验失败，请检查输入')
    }
  }

  /**
   * 取消操作
   */
  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  /**
   * 对话框关闭后的回调
   */
  const handleClosed = (): void => {
    resetForm()
  }

  /**
   * 监听对话框显示状态
   */
  watch(
    () => props.visible,
    async (newVal) => {
      if (newVal) {
        // 先加载上级菜单选项
        await loadParentMenuOptions()

        // 设置菜单类型
        if (props.type === 'button') {
          form.menuType = 'F'
        } else if (!props.editData) {
          // 新增菜单时，如果未锁定类型，默认设置为目录（M），用户可自行切换
          form.menuType = 'M'
        }

        // 等待 DOM 更新后再加载表单数据
        await nextTick()
        if (props.editData) {
          loadFormData()
        } else {
          resetForm()
        }
      }
    }
  )

  /**
   * 监听上级菜单变化，自动调整菜单类型
   */
  watch([() => form.parentId, () => getParentMenuType.value], ([newParentId, newParentType]) => {
    if (newParentType === 'C' && form.menuType === 'M') {
      // 如果上级菜单是'C'（菜单），且当前选择的是'M'（目录），则自动切换到'C'（菜单）
      form.menuType = 'C'
    }
  })

  /**
   * 监听菜单类型变化，更新验证规则
   */
  watch(
    () => form.menuType,
    () => {
      // 验证规则已经在 rules 中定义，这里只需要确保触发验证
      nextTick(() => {
        if (formRef.value) {
          formRef.value.clearValidate()
        }
      })
    },
    { immediate: true }
  )
  onMounted(async () => {
    await loadParentMenuOptions()
  })
</script>

<style scoped lang="scss"></style>
