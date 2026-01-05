<!-- 菜单管理页面 -->
<template>
  <div class="menu-page art-full-height">
    <!-- 搜索栏 -->
    <ArtSearchBar
      v-model="formFilters"
      :items="formItems"
      :showExpand="false"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader
        :showZebra="false"
        :loading="loading"
        v-model:columns="columnChecks"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElButton @click="handleAddMenu" v-ripple> 添加菜单 </ElButton>
          <ElButton @click="toggleExpand" v-ripple>
            {{ isExpanded ? '收起' : '展开' }}
          </ElButton>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        rowKey="path"
        :loading="loading"
        :columns="columns"
        :data="filteredTableData"
        :stripe="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      />

      <!-- 菜单弹窗 -->
      <MenuDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :editData="editData"
        :rawMenuData="rawMenuData"
        :lockType="lockMenuType"
        @submit="handleSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { formatMenuTitle } from '@/utils/router'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import type { AppRouteRecord } from '@/types/router'
  import MenuDialog from './modules/menu-dialog.vue'
  import {
    fetchAddMenu,
    fetchDeleteMenu,
    fetchGetMenuTree,
    fetchUpdateMenu,
    type SysMenu
  } from '@/api/menu/menu'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { MenuProcessor } from '@/router/core/MenuProcessor'
  import type { MenuFormData } from './types'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'

  defineOptions({ name: 'Menus' })

  // 状态管理
  const loading = ref(false)
  const isExpanded = ref(false)
  const tableRef = ref()

  // 弹窗相关
  const dialogVisible = ref(false)
  const dialogType = ref<'menu' | 'button'>('menu')
  const editData = ref<Partial<MenuFormData & AppRouteRecord> | null>(null)
  const lockMenuType = ref(false)

  // 搜索相关
  const initialSearchState = {
    menuName: '',
    route: '',
    perms: '',
    status: ''
  }

  const formFilters = reactive({ ...initialSearchState })
  const appliedFilters = reactive({ ...initialSearchState })

  const formItems = computed(() => [
    {
      label: '菜单名称',
      key: 'menuName',
      type: 'input',
      props: { clearable: true, placeholder: '请输入菜单名称' }
    },
    {
      label: '路由地址',
      key: 'route',
      type: 'input',
      props: { clearable: true, placeholder: '请输入路由地址' }
    },
    {
      label: '权限标识',
      key: 'perms',
      type: 'input',
      props: { clearable: true, placeholder: '请输入权限标识' }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        clearable: true,
        placeholder: '请选择状态'
      },
      options: [
        { label: '正常', value: '0' },
        { label: '停用', value: '1' }
      ]
    }
  ])

  onMounted(() => {
    getMenuList()
  })

  // 常量配置
  const MENU_TYPE_CONFIG = {
    M: { text: '目录', color: 'info' as const },
    C: { text: '菜单', color: 'primary' as const },
    F: { text: '按钮', color: 'danger' as const }
  } as const

  const STATUS_CONFIG = {
    '0': { text: '正常', type: 'success' as const },
    '1': { text: '停用', type: 'danger' as const }
  } as const

  // 存储原始菜单数据（用于查找parentId和原始path）
  const rawMenuData = ref<SysMenu[]>([])
  // 存储菜单ID到原始path的映射
  const menuPathMap = ref<Map<number, string>>(new Map())

  /**
   * 构建菜单路径映射
   * @param menuList 菜单列表
   */
  const buildMenuPathMap = (menuList: SysMenu[]): void => {
    menuPathMap.value.clear()
    const traverse = (menus: SysMenu[]): void => {
      menus.forEach((menu) => {
        if (menu.menuId !== undefined && menu.path) {
          menuPathMap.value.set(menu.menuId, menu.path)
        }
        if (menu.children && menu.children.length > 0) {
          traverse(menu.children)
        }
      })
    }
    traverse(menuList)
  }

  /**
   * 获取菜单列表数据
   */
  const getMenuList = async (): Promise<void> => {
    loading.value = true

    try {
      // 菜单管理页面应该获取所有菜单树，而不是用户有权限的菜单
      const sysMenuList = await fetchGetMenuTree()
      rawMenuData.value = sysMenuList

      // 构建菜单路径映射
      buildMenuPathMap(sysMenuList)

      // 使用 MenuProcessor 将 SysMenu[] 转换为 AppRouteRecord[]
      // 由于MenuProcessor的getMenuList使用的是userMenuTree接口，我们需要手动转换
      const menuProcessor = new MenuProcessor()

      // 创建一个临时的MenuProcessor实例来使用其私有方法
      // 由于TypeScript的限制，我们使用类型断言来访问私有方法
      const processor = menuProcessor as any
      const convertedList = processor.convertSysMenuToRouteRecord(sysMenuList, 0)
      const filteredList = processor.filterEmptyMenus(convertedList)
      processor.validateMenuPaths(filteredList)
      const normalizedList = processor.normalizeMenuPaths(filteredList)

      // 手动添加 perms 字段（从原始数据中获取）
      const addPermsToMenu = (menus: AppRouteRecord[], rawMenus: SysMenu[]): void => {
        menus.forEach((menu) => {
          const rawMenu = findMenuAndParentFromRawData(menu.id, rawMenus).menu
          if (rawMenu?.perms) {
            menu.perms = rawMenu.perms
          }
          if (menu.children?.length && rawMenu?.children) {
            addPermsToMenu(menu.children, rawMenu.children)
          }
        })
      }
      addPermsToMenu(normalizedList, sysMenuList)
      tableData.value = normalizedList
    } catch (error) {
      console.error('获取菜单列表失败:', error)
      ElMessage.error('获取菜单列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 从原始菜单数据中查找菜单项及其父ID
   * @param menuId 菜单ID
   * @param menuList 菜单列表
   * @param parentId 父菜单ID（递归使用）
   * @returns 菜单项和父ID的对象
   */
  const findMenuAndParentFromRawData = (
    menuId: number | undefined,
    menuList: SysMenu[],
    parentId?: number
  ): { menu: SysMenu | undefined; parentId: number | undefined } => {
    if (!menuId) return { menu: undefined, parentId: undefined }

    for (const menu of menuList) {
      if (menu.menuId === menuId) {
        return { menu, parentId }
      }
      if (menu.children?.length) {
        const found = findMenuAndParentFromRawData(menuId, menu.children, menu.menuId)
        if (found.menu) return found
      }
    }
    return { menu: undefined, parentId: undefined }
  }

  /**
   * 获取菜单类型信息
   * @param row 菜单行数据
   * @returns 菜单类型信息（文本和颜色）
   */
  const getMenuTypeInfo = (
    row: AppRouteRecord
  ): { text: string; color: 'primary' | 'success' | 'warning' | 'info' | 'danger' } => {
    // 优先使用后端返回的 menuType 字段
    if (row.menuType && row.menuType in MENU_TYPE_CONFIG) {
      const config = MENU_TYPE_CONFIG[row.menuType as keyof typeof MENU_TYPE_CONFIG]
      return { text: config.text, color: config.color }
    }
    // 兼容旧逻辑：如果没有 menuType，则根据其他字段推断
    if (row.meta?.isAuthButton) return { text: '按钮', color: 'danger' }
    if (row.children?.length) return { text: '目录', color: 'info' }
    if (row.meta?.link && row.meta?.isIframe) return { text: '内嵌', color: 'success' }
    if (row.path) return { text: '菜单', color: 'primary' }
    if (row.meta?.link) return { text: '外链', color: 'warning' }
    return { text: '未知', color: 'info' }
  }

  // 表格列配置
  const { columnChecks, columns } = useTableColumns(() => [
    {
      prop: 'meta.title',
      label: '菜单名称',
      minWidth: 120,
      formatter: (row: AppRouteRecord) => formatMenuTitle(row.meta?.title)
    },
    {
      prop: 'type',
      label: '菜单类型',
      formatter: (row: AppRouteRecord) => {
        const typeInfo = getMenuTypeInfo(row)
        return h(ElTag, { type: typeInfo.color }, () => typeInfo.text)
      }
    },
    {
      prop: 'path',
      label: '路由地址',
      formatter: (row: AppRouteRecord) => {
        if (row.meta?.isAuthButton) return ''
        // 优先显示外链
        if (row.meta?.link) return row.meta.link
        // 显示后端返回的原始 path，而不是规范化后的 path
        if (row.id !== undefined) {
          const originalPath = menuPathMap.value.get(row.id)
          if (originalPath) return originalPath
        }
        return row.path || ''
      }
    },
    {
      prop: 'perms',
      label: '权限标识',
      minWidth: 150,
      formatter: (row: AppRouteRecord) => {
        if (row.meta?.isAuthButton) {
          return h(ElTag, { type: 'danger', size: 'small' }, () => row.meta?.authMark || '')
        }
        // 优先显示后端返回的 perms 字段
        if (row.perms) {
          return h(ElTag, { type: 'info', size: 'small' }, () => row.perms)
        }
        // 兼容旧逻辑：如果没有 perms，则显示权限列表
        if (row.meta?.authList?.length) {
          return h(
            'div',
            { style: 'display: flex; flex-wrap: wrap; gap: 4px;' },
            row.meta.authList.map((auth) =>
              h(ElTag, { type: 'info', size: 'small' }, () => auth.authMark)
            )
          )
        }
        return h('span', { style: 'color: #999' }, '无')
      }
    },
    {
      prop: 'createTime',
      label: '创建时间',
      width: 180,
      formatter: (row: AppRouteRecord) => {
        return row.createTime || h('span', { style: 'color: #999' }, '暂无')
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      formatter: (row: AppRouteRecord) => {
        // 按钮类型不显示状态
        if (row.meta?.isAuthButton) {
          return ''
        }
        const status = (row.status || '0') as keyof typeof STATUS_CONFIG
        const statusInfo = STATUS_CONFIG[status] || STATUS_CONFIG['0']
        return h(ElTag, { type: statusInfo.type }, () => statusInfo.text)
      }
    },
    {
      prop: 'operation',
      label: '操作',
      width: 180,
      align: 'right',
      formatter: (row: AppRouteRecord) => {
        const buttonStyle = { style: 'text-align: right' }

        if (row.meta?.isAuthButton) {
          return h('div', buttonStyle, [
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => handleEditAuth(row)
            }),
            h(ArtButtonTable, {
              type: 'delete',
              onClick: () => handleDeleteAuth(row)
            })
          ])
        }

        return h('div', buttonStyle, [
          h(ArtButtonTable, {
            type: 'add',
            onClick: () => handleAddAuth(row),
            title: '新增权限'
          }),
          h(ArtButtonTable, {
            type: 'edit',
            onClick: () => handleEditMenu(row)
          }),
          h(ArtButtonTable, {
            type: 'delete',
            onClick: () => handleDeleteMenu(row)
          })
        ])
      }
    }
  ])

  // 数据相关
  const tableData = ref<AppRouteRecord[]>([])

  /**
   * 重置搜索条件
   */
  const handleReset = (): void => {
    Object.assign(formFilters, { ...initialSearchState })
    Object.assign(appliedFilters, { ...initialSearchState })
    getMenuList()
  }

  /**
   * 执行搜索
   */
  const handleSearch = (): void => {
    Object.assign(appliedFilters, { ...formFilters })
    getMenuList()
  }

  /**
   * 刷新菜单列表
   */
  const handleRefresh = (): void => {
    getMenuList()
  }

  /**
   * 深度克隆对象
   * @param obj 要克隆的对象
   * @returns 克隆后的对象
   */
  const deepClone = <T,>(obj: T): T => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj) as T
    if (Array.isArray(obj)) return obj.map((item) => deepClone(item)) as T

    const cloned = {} as T
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        cloned[key] = deepClone(obj[key])
      }
    }
    return cloned
  }

  /**
   * 将权限列表转换为子节点
   * @param items 菜单项数组
   * @returns 转换后的菜单项数组
   */
  const convertAuthListToChildren = (items: AppRouteRecord[]): AppRouteRecord[] => {
    return items.map((item) => {
      const clonedItem = deepClone(item)

      if (clonedItem.children?.length) {
        clonedItem.children = convertAuthListToChildren(clonedItem.children)
      }

      // 如果菜单本身有 perms 字段，则不将 authList 转换为子节点
      // 菜单本身的权限应该显示在权限标识列中，而不是作为子节点
      if (item.meta?.authList?.length && !item.perms) {
        const authChildren: AppRouteRecord[] = item.meta.authList.map(
          (auth: { title: string; authMark: string }) => ({
            path: `${item.path}_auth_${auth.authMark}`,
            name: `${String(item.name)}_auth_${auth.authMark}`,
            meta: {
              title: auth.title,
              authMark: auth.authMark,
              isAuthButton: true,
              parentPath: item.path
            }
          })
        )

        clonedItem.children = clonedItem.children?.length
          ? [...clonedItem.children, ...authChildren]
          : authChildren
      }

      return clonedItem
    })
  }

  /**
   * 搜索菜单
   * @param items 菜单项数组
   * @returns 搜索结果数组
   */
  const searchMenu = (items: AppRouteRecord[]): AppRouteRecord[] => {
    const results: AppRouteRecord[] = []

    // 提前计算搜索条件，避免在循环中重复计算
    const searchName = appliedFilters.menuName?.toLowerCase().trim() || ''
    const searchRoute = appliedFilters.route?.toLowerCase().trim() || ''
    const searchPerms = appliedFilters.perms?.toLowerCase().trim() || ''
    const searchStatus = appliedFilters.status?.trim() || ''

    for (const item of items) {
      const menuTitle = formatMenuTitle(item.meta?.title || '').toLowerCase()
      const menuPath = (item.path || '').toLowerCase()

      // 权限标识匹配：优先检查 perms 字段，然后检查权限列表或权限标识
      let permsMatch = true
      if (searchPerms) {
        const itemPerms = (item.perms || '').toLowerCase()
        const authMarks = item.meta?.authList?.map((auth) => auth.authMark.toLowerCase()) || []
        const authMark = item.meta?.authMark?.toLowerCase() || ''
        permsMatch =
          itemPerms.includes(searchPerms) ||
          authMarks.some((mark) => mark.includes(searchPerms)) ||
          authMark.includes(searchPerms) ||
          (item.meta?.authList?.some((auth) => auth.title.toLowerCase().includes(searchPerms)) ??
            false)
      }

      // 状态匹配
      const statusMatch = !searchStatus || item.status === searchStatus

      // 名称和路由匹配
      const nameMatch = !searchName || menuTitle.includes(searchName)
      const routeMatch = !searchRoute || menuPath.includes(searchRoute)

      // 如果所有搜索条件都匹配
      const allMatch = nameMatch && routeMatch && permsMatch && statusMatch

      if (item.children?.length) {
        const matchedChildren = searchMenu(item.children)
        if (matchedChildren.length > 0) {
          const clonedItem = deepClone(item)
          clonedItem.children = matchedChildren
          results.push(clonedItem)
          continue
        }
        // 如果父节点本身匹配，即使子节点不匹配也要显示
        if (allMatch) {
          const clonedItem = deepClone(item)
          clonedItem.children = searchMenu(item.children)
          results.push(clonedItem)
          continue
        }
      }

      if (allMatch) {
        results.push(deepClone(item))
      }
    }

    return results
  }

  // 过滤后的表格数据
  const filteredTableData = computed(() => {
    const searchedData = searchMenu(tableData.value)
    return convertAuthListToChildren(searchedData)
  })

  /**
   * 添加菜单
   */
  const handleAddMenu = (): void => {
    dialogType.value = 'menu'
    editData.value = null
    lockMenuType.value = false // 允许用户自主选择菜单类型
    dialogVisible.value = true
  }

  /**
   * 添加权限按钮
   * @param parentRow 父级菜单行数据
   */
  const handleAddAuth = (parentRow?: AppRouteRecord): void => {
    dialogType.value = 'button'
    editData.value = parentRow ? ({ ...parentRow, parentId: parentRow.id } as any) : null
    lockMenuType.value = false
    dialogVisible.value = true
  }

  /**
   * 编辑菜单
   * @param row 菜单行数据
   */
  const handleEditMenu = (row: AppRouteRecord): void => {
    dialogType.value = 'menu'
    // 从原始菜单数据中查找parentId和原始path、component
    const { menu: rawMenu, parentId } = findMenuAndParentFromRawData(row.id, rawMenuData.value)

    // 使用原始数据中的 path 和 component，而不是规范化后的
    editData.value = {
      ...row,
      parentId,
      path: rawMenu?.path || row.path || '',
      component: rawMenu?.component || row.component || ''
    } as any
    lockMenuType.value = true
    dialogVisible.value = true
  }

  /**
   * 编辑权限按钮
   * @param row 权限行数据
   */
  const handleEditAuth = (row: AppRouteRecord): void => {
    dialogType.value = 'button'
    editData.value = {
      id: row.id,
      title: row.meta?.title,
      authMark: row.meta?.authMark,
      perms: row.meta?.authMark,
      parentPath: row.meta?.parentPath
    } as any
    lockMenuType.value = false
    dialogVisible.value = true
  }

  /**
   * 提交表单数据
   * @param formData 表单数据
   */
  const handleSubmit = async (formData: MenuFormData): Promise<void> => {
    try {
      const isEdit = !!formData.menuId
      if (isEdit) {
        await fetchUpdateMenu(formData)
        ElMessage.success('修改菜单成功')
      } else {
        await fetchAddMenu(formData)
        ElMessage.success('新增菜单成功')
      }
      dialogVisible.value = false
      await getMenuList()
    } catch (error) {
      console.error('保存菜单失败:', error)
      ElMessage.error(formData.menuId ? '修改菜单失败' : '新增菜单失败')
    }
  }

  /**
   * 删除菜单或权限
   * @param row 菜单或权限行数据
   * @param isAuthButton 是否为权限按钮
   */
  const handleDelete = async (row: AppRouteRecord, isAuthButton = false): Promise<void> => {
    if (!row.id) {
      ElMessage.warning(`${isAuthButton ? '权限' : '菜单'}ID不存在，无法删除`)
      return
    }

    try {
      ElMessageBox.confirm(
        `确定要删除该${isAuthButton ? '权限' : '菜单'}吗？删除后无法恢复`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      await fetchDeleteMenu(row.id)
      ElMessage.success('删除成功')
      await getMenuList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error(`删除${isAuthButton ? '权限' : '菜单'}失败:`, error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 删除菜单
   * @param row 菜单行数据
   */
  const handleDeleteMenu = (row: AppRouteRecord): Promise<void> => {
    return handleDelete(row, false)
  }

  /**
   * 删除权限按钮
   * @param row 权限行数据
   */
  const handleDeleteAuth = (row: AppRouteRecord): Promise<void> => {
    return handleDelete(row, true)
  }

  /**
   * 切换展开/收起所有菜单
   */
  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
    nextTick(() => {
      if (tableRef.value?.elTableRef && filteredTableData.value) {
        const processRows = (rows: AppRouteRecord[]) => {
          rows.forEach((row) => {
            if (row.children?.length) {
              tableRef.value.elTableRef.toggleRowExpansion(row, isExpanded.value)
              processRows(row.children)
            }
          })
        }
        processRows(filteredTableData.value)
      }
    })
  }
</script>
