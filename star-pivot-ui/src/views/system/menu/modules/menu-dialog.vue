<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    :width="width > 768 ? '860px' : '95%'"
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
          <ElRadioButton value="M" :disabled="menuTypeDisabled.M">{{
            t('system.menu.typeDir')
          }}</ElRadioButton>
          <ElRadioButton value="C" :disabled="menuTypeDisabled.C">{{
            t('system.menu.typeMenu')
          }}</ElRadioButton>
          <ElRadioButton value="F" :disabled="menuTypeDisabled.F">{{
            t('system.menu.typeButton')
          }}</ElRadioButton>
        </ElRadioGroup>
      </template>
      <template #icon>
        <ArtIconPicker ref="iconPickerRef" v-model="form.icon" :manual="true">
          <ElInput
            class="menu-icon-input"
            v-model="form.icon"
            :placeholder="t('common.pleaseInput')"
            clearable
            style="width: 100%"
          >
            <template #prepend>
              <div class="menu-icon-prepend">
                <Icon :icon="form.icon || 'ri:apps-line'" style="font-size: 18px" />
              </div>
            </template>
            <template #append>
              <ElButton class="menu-icon-append-btn" @click.stop="handleChooseIconClick">
                {{ t('system.menu.menuIcon') }}
              </ElButton>
            </template>
          </ElInput>
        </ArtIconPicker>
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleSubmit">{{ t('common.confirm') }}</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElIcon, ElInput, ElMessage, ElTooltip } from 'element-plus'
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
  import { fetchGetMenuById, fetchGetParentMenu, type SysMenu } from '@/api/menu/menu'
  import { useI18n } from 'vue-i18n'

  const { width } = useWindowSize()
  const { t } = useI18n()

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
  const iconPickerRef = ref<{ open: () => void; close: () => void } | null>(null)
  const isEdit = ref(false)
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

  /** 路由地址是否为 http(s) 外链（与 MenuProcessor 判定一致） */
  const isHttpExternalPath = (p: string) => p.startsWith('http://') || p.startsWith('https://')

  /** 是否视为顶级菜单（无上级或上级为 0） */
  const isTopLevelMenu = () => !form.parentId || form.parentId === 0
  /** isFrame是否选中 */
  const isFrameChecked = () => form.isFrame === 0

  // 路径验证函数（是否外链由 isFrame 决定：选中则按 http(s) 外链规则，否则按站内路径规则）
  const validatePath = (rule: any, value: string, callback: any) => {
    if (!value && form.menuType === 'C') {
      callback(new Error(t('system.menu.pathRequired')))
      return
    }
    if (value) {
      // 检查是否包含非法字符
      if (/[<>"']/.test(value)) {
        callback(new Error(t('system.menu.pathRequired')))
        return
      }
      if (isFrameChecked()) {
        if (!isHttpExternalPath(value)) {
          callback(new Error(t('system.menu.pathExternalInvalid')))
          return
        }
        callback()
        return
      }
      if (isTopLevelMenu() && !value.startsWith('/') && !isHttpExternalPath(value)) {
        callback(new Error(t('system.menu.pathRequired')))
        return
      }
      if (form.parentId && value.startsWith('/') && !value.startsWith('http')) {
        callback(new Error(t('system.menu.pathRequired')))
        return
      }
    }
    callback()
  }

  // 组件路径验证函数
  const validateComponent = (rule: any, value: string, callback: any) => {
    const pathTrim = (form.path || '').trim()
    const externalByPath = isHttpExternalPath(pathTrim)
    const externalMenu = isFrameChecked() || externalByPath
    if (form.menuType === 'C' && !value && !externalMenu) {
      callback(new Error(t('system.menu.componentPlaceholder')))
      return
    }
    if (value) {
      if (!value.startsWith('/')) {
        callback(new Error(t('system.menu.componentInvalid')))
        return
      }
      if (/[<>"']/.test(value)) {
        callback(new Error(t('system.menu.componentInvalid')))
        return
      }
    }
    callback()
  }

  // 权限标识验证函数
  const validatePerms = (rule: any, value: string, callback: any) => {
    if (form.menuType === 'F' && !value) {
      callback(new Error(t('system.menu.permsPlaceholder')))
      return
    }
    if (value) {
      if (!/^[a-zA-Z0-9:_-]+$/.test(value)) {
        callback(new Error(t('system.menu.permsInvalid')))
        return
      }
    }
    callback()
  }

  const rules = computed<FormRules>(() => ({
    menuName: [
      { required: true, message: t('system.menu.nameRequired'), trigger: 'blur' },
      { max: 50, message: t('system.menu.nameMaxLength'), trigger: 'blur' }
    ],
    menuType: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
    path: [{ validator: validatePath, trigger: 'blur' }],
    component: [{ validator: validateComponent, trigger: 'blur' }],
    perms: [{ validator: validatePerms, trigger: 'blur' }]
  }))

  /**
   * 根据菜单类型动态生成表单项
   */
  const formItems = computed<FormItem[]>(() => {
    // 确保选项数据存在
    const menuOptions = parentMenuOptions.value || []

    // 菜单类型和上级菜单放在同一行
    const baseItems: FormItem[] = [
      { label: t('system.menu.menuType'), key: 'menuType', span: 12 },
      {
        label: t('system.menu.parentMenu'),
        key: 'parentId',
        type: 'treeselect',
        span: 12,
        props: {
          placeholder: t('system.menu.parentMenuPlaceholder'),
          clearable: true,
          data: menuOptions,
          'render-after-expand': false,
          'check-strictly': true,
          'default-expand-all': false
        }
      }
    ]

    if (form.menuType === 'F') {
      return [
        ...baseItems,
        {
          label: t('system.menu.buttonName'),
          key: 'menuName',
          type: 'input',
          props: { placeholder: t('system.menu.buttonNamePlaceholder') }
        },
        {
          label: t('system.menu.perms'),
          key: 'perms',
          type: 'input',
          props: { placeholder: t('system.menu.buttonPermsPlaceholder') }
        },
        {
          label: t('system.menu.orderNum'),
          key: 'orderNum',
          type: 'number',
          props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
        },
        {
          label: t('common.remark'),
          key: 'remark',
          type: 'input',
          span: 24,
          props: { type: 'textarea', rows: 3, placeholder: t('common.pleaseInput') }
        }
      ]
    }

    // 目录（M）和菜单（C）类型
    const switchSpan = width.value < 640 ? 12 : 6
    // 读取 isFrame 以建立依赖：外链选中时路由地址提示外链填写规则
    const externalFrame = isFrameChecked()

    const pathTooltipInternal = t('system.menu.pathTipInternal')
    const pathTooltipExternal = t('system.menu.pathTipExternal')

    return [
      ...baseItems,
      {
        label: t('system.menu.menuName'),
        key: 'menuName',
        type: 'input',
        props: { placeholder: t('system.menu.namePlaceholder') }
      },
      {
        label: createLabelTooltip(
          t('system.menu.routePath'),
          externalFrame ? pathTooltipExternal : pathTooltipInternal
        ),
        key: 'path',
        type: 'input',
        props: {
          placeholder: externalFrame
            ? t('system.menu.pathPlaceholderExternal')
            : t('system.menu.pathPlaceholder')
        }
      },
      {
        label: createLabelTooltip(t('system.menu.routeName'), t('system.menu.routeNameTip')),
        key: 'routeName',
        type: 'input',
        props: { placeholder: t('system.menu.routeNamePlaceholder') }
      },
      {
        label: createLabelTooltip(t('system.menu.component'), t('system.menu.componentTip')),
        key: 'component',
        type: 'input',
        props: {
          placeholder: t('system.menu.componentPlaceholder')
        }
      },
      {
        label: t('system.menu.routeQuery'),
        key: 'query',
        type: 'input',
        props: { placeholder: t('system.menu.routeQueryPlaceholder') }
      },
      {
        label: t('system.menu.menuIcon'),
        key: 'icon',
        type: 'input',
        props: { placeholder: t('system.menu.iconPlaceholder') }
      },
      {
        label: t('system.menu.perms'),
        key: 'perms',
        type: 'input',
        props: { placeholder: t('system.menu.permsPlaceholder') }
      },
      {
        label: t('system.menu.orderNum'),
        key: 'orderNum',
        type: 'number',
        props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
      },
      {
        label: t('system.menu.isFrame'),
        key: 'isFrame',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: 0,
          inactiveValue: 1
        }
      },
      {
        label: t('system.menu.isCache'),
        key: 'isCache',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: 0,
          inactiveValue: 1
        }
      },
      {
        label: t('system.menu.visible'),
        key: 'visible',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: '0',
          inactiveValue: '1'
        }
      },
      {
        label: t('system.menu.status'),
        key: 'status',
        type: 'switch',
        span: switchSpan,
        props: {
          activeValue: '0',
          inactiveValue: '1'
        }
      },
      {
        label: t('common.remark'),
        key: 'remark',
        type: 'input',
        span: 24,
        props: { type: 'textarea', rows: 3, placeholder: t('common.pleaseInput') }
      }
    ]
  })

  const dialogTitle = computed(() => {
    const menuTypeMap: Record<string, string> = {
      M: t('system.menu.typeDir'),
      C: t('system.menu.typeMenu'),
      F: t('system.menu.typeButton')
    }
    const type = menuTypeMap[form.menuType] || t('system.menu.typeMenu')
    return isEdit.value ? `${t('common.edit')}${type}` : `${t('common.add')}${type}`
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
        treeData.push({ label: t('common.empty'), value: 0 })
      }

      parentMenuOptions.value = treeData
    } catch (error) {
      safeError('加载上级菜单失败:', error)
      parentMenuOptions.value = [{ label: t('common.empty'), value: 0 }]
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
  const loadFormData = async (): Promise<void> => {
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

    // 编辑时优先用详情接口拿默认语言菜单名
    if (row.id) {
      try {
        const detail = await fetchGetMenuById(row.id)
        if (detail?.menuName) {
          form.menuName = detail.menuName
        }
      } catch (error) {
        safeError('加载菜单详情失败:', error)
      }
    }

    // 如果是按钮类型
    if (props.type === 'button' || row.meta?.isAuthButton) {
      form.menuType = 'F'
      form.menuId = row.id || undefined
      // 优先使用原始数据
      if (!form.menuName) {
        form.menuName = rawMenu?.menuName || row.meta?.title || row.title || row.menuName || ''
      }
      form.perms = rawMenu?.perms || row.meta?.authMark || row.authMark || row.perms || ''
      form.parentId = rawMenu?.parentId ?? row.parentId ?? undefined
      form.orderNum = rawMenu?.orderNum || row.meta?.orderNum || row.orderNum || 1
      form.remark = rawMenu?.remark || row.remark || ''
      return
    }

    // 目录或菜单类型 - 优先使用原始数据
    form.menuId = row.id || undefined
    if (!form.menuName) {
      form.menuName = rawMenu?.menuName || formatMenuTitle(row.meta?.title || row.menuName || '')
    }
    form.parentId = rawMenu?.parentId ?? row.parentId ?? undefined
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

      // 构建提交数据（多语言标题统一在「国际化管理 → UI 文案」维护）
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
      ElMessage.error(t('common.pleaseInput'))
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
    async (newVal: boolean) => {
      if (newVal) {
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
          await loadFormData()
        } else {
          resetForm()
        }
      }
    }
  )

  /**
   * 监听上级菜单变化，自动调整菜单类型
   */
  watch(
    [() => form.parentId, () => getParentMenuType.value],
    ([newParentId, newParentType]: [number | undefined, string | undefined]) => {
      if (newParentType === 'C' && form.menuType === 'M') {
        // 如果上级菜单是'C'（菜单），且当前选择的是'M'（目录），则自动切换到'C'（菜单）
        if (newParentId !== undefined) {
          form.menuType = 'C'
        }
      }
    }
  )

  /**
   * 监听菜单类型变化，清除校验状态（规则内已读 form.menuType / form.isFrame）
   */
  watch(
    () => form.menuType,
    () => {
      nextTick(() => {
        formRef.value?.ref?.clearValidate()
      })
    },
    { immediate: true }
  )

  /** 是否外链切换时，路由/组件规则不同，立即按新规则重验 */
  watch(
    () => form.isFrame,
    () => {
      nextTick(() => {
        const elForm = formRef.value?.ref
        if (!elForm) return
        elForm.clearValidate(['path', 'component'])
        elForm.validateField(['path', 'component']).catch(() => {})
      })
    }
  )
  onMounted(async () => {
    await loadParentMenuOptions()
  })

  const handleChooseIconClick = () => {
    iconPickerRef.value?.open()
  }
</script>

<style scoped lang="scss">
  .menu-icon-prepend {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 32px;
    color: var(--el-text-color-regular);
    cursor: pointer;
  }

  /* 让输入组更接近截图的“分段”观感 */
  :deep(.menu-icon-input.el-input-group) {
    .el-input-group__prepend {
      padding: 0;
      background: var(--el-fill-color-light);
    }

    .el-input-group__append {
      padding: 0;
      background: var(--el-fill-color-light);
    }
  }

  :deep(.menu-icon-append-btn) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 112px;
    height: 32px;
    padding: 0 14px;
    font-size: 13px;
    line-height: 32px;
    color: var(--el-color-primary);
    white-space: nowrap;
    background: var(--default-box-color);
    border: 0;
    border-left: 1px solid var(--el-border-color);
    border-radius: 0 4px 4px 0;

    &:hover {
      background: var(--el-fill-color-light);
    }
  }
</style>
