/**
 * 根据 views 目录下的文件自动生成菜单表数据
 *
 * 使用方法：
 * node scripts/generate-menu-data.js
 * 或
 * npm run generate:menu
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 配置：需要排除的目录（这些目录不会生成菜单）
const EXCLUDE_DIRS = ['auth', 'exception', 'result', 'outside', 'index', 'modules']

// 配置：目录对应的图标（可以根据需要修改）
const DIR_ICONS = {
  dashboard: 'ri:pie-chart-line',
  system: 'ri:user-3-line',
  monitor: 'ri:monitor-line',
  tool: 'ri:tools-line',
  log: 'ri:file-list-line'
}

// 配置：目录对应的中文名称（可以根据需要修改）
const DIR_NAMES = {
  dashboard: '仪表盘',
  system: '系统管理',
  monitor: '系统监控',
  tool: '系统工具',
  log: '日志管理'
}

// 配置：页面对应的中文名称（可以根据需要修改）
const PAGE_NAMES = {
  console: '控制台',
  user: '用户管理',
  role: '角色管理',
  menu: '菜单管理',
  'user-center': '个人中心',
  druid: '数据监控',
  online: '在线用户',
  job: '定时任务',
  operlog: '操作日志',
  logininfor: '登录日志',
  gen: '代码生成',
  dict: '字典管理',
  config: '参数设置',
  notice: '通知公告'
}

/**
 * 将路径转换为路由名称（PascalCase）
 */
function pathToRouteName(filePath) {
  const parts = filePath.split('/').filter(Boolean)
  return parts
    .map((part) => {
      // 将 kebab-case 转换为 PascalCase
      return part
        .split('-')
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join('')
    })
    .join('')
}

/**
 * 获取文件的中文名称
 */
function getPageName(filePath) {
  const parts = filePath.split('/').filter(Boolean)
  const lastPart = parts[parts.length - 1]
  return PAGE_NAMES[lastPart] || lastPart
}

/**
 * 获取目录的中文名称
 */
function getDirName(dirPath) {
  const parts = dirPath.split('/').filter(Boolean)
  const firstPart = parts[0]
  return DIR_NAMES[firstPart] || firstPart
}

/**
 * 获取目录的图标
 */
function getDirIcon(dirPath) {
  const parts = dirPath.split('/').filter(Boolean)
  const firstPart = parts[0]
  return DIR_ICONS[firstPart] || 'ri:folder-line'
}

/**
 * 递归扫描 views 目录
 */
function scanViewsDir(dirPath, basePath = '') {
  const items = []
  const entries = fs.readdirSync(dirPath, { withFileTypes: true })

  // 处理目录
  const dirs = entries.filter((e) => e.isDirectory() && !EXCLUDE_DIRS.includes(e.name))
  dirs.forEach((dir) => {
    const fullPath = path.join(dirPath, dir.name)
    const relativePath = basePath ? `${basePath}/${dir.name}` : dir.name

    // 检查目录下是否有 dict-data.vue 文件
    const indexPath = path.join(fullPath, 'dict-data.vue')
    const hasIndex = fs.existsSync(indexPath)

    // 递归扫描子目录
    const children = scanViewsDir(fullPath, relativePath)

    // 如果有子目录或 dict-data.vue，添加到菜单
    if (children.length > 0 || hasIndex) {
      items.push({
        type: 'directory',
        name: dir.name,
        path: relativePath,
        fullPath: fullPath,
        children: children,
        hasIndex: hasIndex
      })
    }
  })

  return items
}

/**
 * 生成菜单数据
 */
function generateMenuData(items, parentId = 0, orderNum = 1) {
  const menus = []
  let menuIdCounter = 1

  // 递归函数，使用闭包共享 menuIdCounter
  function processItems(items, parentId, orderNum) {
    let currentOrderNum = orderNum
    const resultMenus = []

    items.forEach((item) => {
      const menuId = menuIdCounter++
      const isDirectory = item.type === 'directory'
      const hasChildren = item.children && item.children.length > 0
      const hasIndex = item.hasIndex

      // 生成路径
      const routePath = `/${item.path}`

      // 生成路由名称
      const routeName = pathToRouteName(item.path)

      // 如果是有子目录的目录，生成目录菜单（M类型）
      if (isDirectory && hasChildren) {
        const menuName = getDirName(item.path)
        const menu = {
          menuId: menuId,
          menuName: menuName,
          parentId: parentId,
          orderNum: currentOrderNum++,
          path: routePath,
          component: '/index/index', // Layout 组件
          routeName: routeName,
          isFrame: 1,
          isCache: 0,
          menuType: 'M', // M=目录
          visible: '0',
          status: '0',
          icon: getDirIcon(item.path),
          perms: null,
          remark: `${menuName}模块`
        }
        resultMenus.push(menu)

        // 如果有 dict-data.vue，作为第一个子菜单添加
        let childOrderNum = 1
        if (hasIndex) {
          const pageMenuId = menuIdCounter++
          const pageMenuName = getPageName(item.path)
          const pageMenu = {
            menuId: pageMenuId,
            menuName: pageMenuName,
            parentId: menuId,
            orderNum: childOrderNum++,
            path: routePath,
            component: `/${item.path}`,
            routeName: routeName,
            isFrame: 1,
            isCache: 0,
            menuType: 'C', // C=菜单
            visible: '0',
            status: '0',
            icon: null,
            perms: null,
            remark: `${pageMenuName}模块`
          }
          resultMenus.push(pageMenu)
        }

        // 递归处理子目录
        const childMenus = processItems(item.children, menuId, childOrderNum)
        resultMenus.push(...childMenus)
      }
      // 如果是只有 dict-data.vue 的目录（没有子目录），生成页面菜单（C类型）
      else if (isDirectory && hasIndex && !hasChildren) {
        const menuName = getPageName(item.path)
        const menu = {
          menuId: menuId,
          menuName: menuName,
          parentId: parentId,
          orderNum: currentOrderNum++,
          path: routePath,
          component: `/${item.path}`,
          routeName: routeName,
          isFrame: 1,
          isCache: 0,
          menuType: 'C', // C=菜单
          visible: '0',
          status: '0',
          icon: null,
          perms: null,
          remark: `${menuName}模块`
        }
        resultMenus.push(menu)
      }
    })

    return resultMenus
  }

  const result = processItems(items, parentId, orderNum)
  menus.push(...result)
  return menus
}

/**
 * 生成 SQL INSERT 语句
 */
function generateSQL(menus) {
  const sql = []
  sql.push('-- 自动生成的菜单数据')
  sql.push('-- 生成时间: ' + new Date().toLocaleString('zh-CN'))
  sql.push('-- 说明: 按层级顺序插入，确保父菜单先插入')
  sql.push('')
  sql.push('DELETE FROM sys_menu;')
  sql.push('')

  // 按层级排序：先插入父菜单，再插入子菜单
  const sortedMenus = [...menus].sort((a, b) => {
    // 先按 parentId 排序（父菜单在前）
    if (a.parentId !== b.parentId) {
      return a.parentId - b.parentId
    }
    // 同层级按 orderNum 排序
    return a.orderNum - b.orderNum
  })

  sql.push(
    'INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, icon, perms, create_by, create_time, remark) VALUES'
  )

  const values = sortedMenus.map((menu) => {
    const icon = menu.icon ? `'${menu.icon.replace(/'/g, "''")}'` : 'NULL'
    const component = menu.component ? `'${menu.component.replace(/'/g, "''")}'` : 'NULL'
    const perms = menu.perms ? `'${menu.perms.replace(/'/g, "''")}'` : 'NULL'
    const routeName = menu.routeName ? `'${menu.routeName.replace(/'/g, "''")}'` : 'NULL'
    const menuName = menu.menuName.replace(/'/g, "''")
    const remark = menu.remark.replace(/'/g, "''")
    const path = menu.path.replace(/'/g, "''")

    // 注意: parent_id 需要根据实际插入后的 menu_id 调整
    // 如果 parent_id 不为 0，需要查询对应的父菜单的实际 menu_id
    // 这里先使用相对位置（假设 parent_id=0 的菜单从 menu_id=1 开始）
    let parentId = menu.parentId
    if (parentId !== 0) {
      // 查找父菜单在排序后的位置
      const parentIndex = sortedMenus.findIndex((m) => m.menuId === parentId)
      if (parentIndex !== -1) {
        // 计算父菜单的实际 menu_id（假设从 1 开始自增）
        parentId = parentIndex + 1
      }
    }

    return `('${menuName}', ${parentId}, ${menu.orderNum}, '${path}', ${component}, ${routeName}, ${menu.isFrame}, ${menu.isCache}, '${menu.menuType}', '${menu.visible}', '${menu.status}', ${icon}, ${perms}, 'system', NOW(), '${remark}')`
  })

  sql.push(values.join(',\n') + ';')
  sql.push('')
  sql.push('-- 注意: 如果 parent_id 引用不正确，请手动调整')
  sql.push('-- 建议: 先执行上面的 INSERT，然后根据实际的 menu_id 更新 parent_id')

  return sql.join('\n')
}

/**
 * 生成 JSON 格式
 */
function generateJSON(menus) {
  return JSON.stringify(menus, null, 2)
}

/**
 * 主函数
 */
function main() {
  const viewsDir = path.join(__dirname, '../src/views')

  if (!fs.existsSync(viewsDir)) {
    console.error('错误: views 目录不存在:', viewsDir)
    process.exit(1)
  }

  console.log('开始扫描 views 目录...')
  const items = scanViewsDir(viewsDir)

  console.log('生成菜单数据...')
  const menus = generateMenuData(items)

  console.log(`共生成 ${menus.length} 条菜单数据`)

  // 生成 SQL 文件
  const sqlContent = generateSQL(menus)
  const sqlPath = path.join(__dirname, '../menu-data.sql')
  fs.writeFileSync(sqlPath, sqlContent, 'utf-8')
  console.log(`SQL 文件已生成: ${sqlPath}`)

  // 生成 JSON 文件
  const jsonContent = generateJSON(menus)
  const jsonPath = path.join(__dirname, '../menu-data.json')
  fs.writeFileSync(jsonPath, jsonContent, 'utf-8')
  console.log(`JSON 文件已生成: ${jsonPath}`)

  // 输出预览
  console.log('\n菜单数据预览:')
  menus.forEach((menu) => {
    const indent = '  '.repeat(menu.parentId > 0 ? 1 : 0)
    console.log(`${indent}- [${menu.menuType}] ${menu.menuName} (${menu.path})`)
  })
}

// 运行
main()
