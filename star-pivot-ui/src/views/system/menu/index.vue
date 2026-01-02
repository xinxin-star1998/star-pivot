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
<!--          <ElButton v-auth="'add'" @click="handleAddMenu" v-ripple> 添加菜单 </ElButton>-->
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
  import { fetchGetMenuList, fetchGetMenuTree, fetchAddMenu, fetchUpdateMenu, fetchDeleteMenu, type SysMenu } from '@/api/menu/menu'
  import { ElTag, ElMessageBox, ElMessage } from 'element-plus'
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
  const editData = ref<AppRouteRecord | any>(null)
  const lockMenuType = ref(false)
  const currentDeleteRow = ref<AppRouteRecord | null>(null)

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
      tableData.value = normalizedList
    } catch (error) {
      console.error('获取菜单列表失败:', error)
      ElMessage.error('获取菜单列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 从原始菜单数据中查找parentId
   * @param menuId 菜单ID
   * @param menuList 菜单列表
   * @returns 父菜单ID
   */
  const findParentIdFromRawData = (menuId: number | undefined, menuList: SysMenu[]): number | undefined => {
    if (!menuId) return undefined
    
    for (const menu of menuList) {
      if (menu.children) {
        const found = menu.children.find((child) => child.menuId === menuId)
        if (found) return menu.menuId
        const parentId = findParentIdFromRawData(menuId, menu.children)
        if (parentId !== undefined) return parentId
      }
    }
    return undefined
  }

  /**
   * 从原始菜单数据中查找菜单项
   * @param menuId 菜单ID
   * @param menuList 菜单列表
   * @returns 菜单项
   */
  const findMenuFromRawData = (menuId: number | undefined, menuList: SysMenu[]): SysMenu | undefined => {
    if (!menuId) return undefined
    
    for (const menu of menuList) {
      if (menu.menuId === menuId) {
        return menu
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuFromRawData(menuId, menu.children)
        if (found) return found
      }
    }
    return undefined
  }

  /**
   * 获取菜单类型标签颜色
   * @param row 菜单行数据
   * @returns 标签颜色类型
   */
  const getMenuTypeTag = (
    row: AppRouteRecord
  ): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    if (row.meta?.isAuthButton) return 'danger'
    if (row.children?.length) return 'info'
    if (row.meta?.link && row.meta?.isIframe) return 'success'
    if (row.path) return 'primary'
    if (row.meta?.link) return 'warning'
    return 'info'
  }

  /**
   * 获取菜单类型文本
   * @param row 菜单行数据
   * @returns 菜单类型文本
   */
  const getMenuTypeText = (row: AppRouteRecord): string => {
    if (row.meta?.isAuthButton) return '按钮'
    if (row.children?.length) return '目录'
    if (row.meta?.link && row.meta?.isIframe) return '内嵌'
    if (row.path) return '菜单'
    if (row.meta?.link) return '外链'
    return '未知'
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
        return h(ElTag, { type: getMenuTypeTag(row) }, () => getMenuTypeText(row))
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
      prop: 'meta.authList',
      label: '权限标识',
      minWidth: 150,
      formatter: (row: AppRouteRecord) => {
        if (row.meta?.isAuthButton) {
          return h(ElTag, { type: 'danger', size: 'small' }, () => row.meta?.authMark || '')
        }
        if (!row.meta?.authList?.length) {
          return h('span', { style: 'color: #999' }, '无')
        }
        // 显示权限标识列表，使用标签展示
        return h('div', { style: 'display: flex; flex-wrap: wrap; gap: 4px;' }, 
          row.meta.authList.map((auth) => 
            h(ElTag, { type: 'info', size: 'small' }, () => auth.authMark)
          )
        )
      }
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 180,
      formatter: (row: AppRouteRecord) => {
        if (row.updateTime) {
          return row.updateTime
        }
        if (row.createTime) {
          return row.createTime
        }
        return h('span', { style: 'color: #999' }, '暂无')
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
        const status = row.status || '0'
        const statusMap = {
          '0': { type: 'success' as const, text: '正常' },
          '1': { type: 'danger' as const, text: '停用' }
        }
        const statusInfo = statusMap[status as keyof typeof statusMap] || statusMap['0']
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

      if (item.meta?.authList?.length) {
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

    for (const item of items) {
      const searchName = appliedFilters.menuName?.toLowerCase().trim() || ''
      const searchRoute = appliedFilters.route?.toLowerCase().trim() || ''
      const searchPerms = appliedFilters.perms?.toLowerCase().trim() || ''
      const searchStatus = appliedFilters.status?.trim() || ''
      
      const menuTitle = formatMenuTitle(item.meta?.title || '').toLowerCase()
      const menuPath = (item.path || '').toLowerCase()
      
      // 权限标识匹配：检查权限列表或权限标识
      let permsMatch = true
      if (searchPerms) {
        const authMarks = item.meta?.authList?.map(auth => auth.authMark.toLowerCase()) || []
        const authMark = item.meta?.authMark?.toLowerCase() || ''
        permsMatch = authMarks.some(mark => mark.includes(searchPerms)) || 
                     authMark.includes(searchPerms) ||
                     (item.meta?.authList?.some(auth => auth.title.toLowerCase().includes(searchPerms)) ?? false)
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
    editData.value = parentRow ? { ...parentRow, parentId: parentRow.id } : null
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
    const parentId = findParentIdFromRawData(row.id, rawMenuData.value)
    const rawMenu = findMenuFromRawData(row.id, rawMenuData.value)
    
    // 使用原始数据中的 path 和 component，而不是规范化后的
    editData.value = { 
      ...row, 
      parentId,
      path: rawMenu?.path || row.path || '',
      component: rawMenu?.component || row.component || ''
    }
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
    }
    lockMenuType.value = false
    dialogVisible.value = true
  }

  /**
   * 提交表单数据
   * @param formData 表单数据
   */
  const handleSubmit = async (formData: MenuFormData): Promise<void> => {
    try {
      if (formData.menuId) {
        // 编辑模式
        await fetchUpdateMenu(formData)
        ElMessage.success('修改菜单成功')
      } else {
        // 新增模式
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
   * 删除菜单
   * @param row 菜单行数据
   */
  const handleDeleteMenu = async (row: AppRouteRecord): Promise<void> => {
    if (!row.id) {
      ElMessage.warning('菜单ID不存在，无法删除')
      return
    }

    try {
      await ElMessageBox.confirm('确定要删除该菜单吗？删除后无法恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteMenu(row.id)
      ElMessage.success('删除成功')
      await getMenuList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除菜单失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 删除权限按钮
   * @param row 权限行数据
   */
  const handleDeleteAuth = async (row: AppRouteRecord): Promise<void> => {
    if (!row.id) {
      ElMessage.warning('权限ID不存在，无法删除')
      return
    }

    try {
      await ElMessageBox.confirm('确定要删除该权限吗？删除后无法恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteMenu(row.id)
      ElMessage.success('删除成功')
      await getMenuList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除权限失败:', error)
        ElMessage.error('删除失败')
      }
    }
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
