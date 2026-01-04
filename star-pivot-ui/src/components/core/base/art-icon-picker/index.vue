<template>
  <ElPopover
    v-model:visible="visible"
    placement="bottom-start"
    :width="600"
    popper-class="icon-picker-popover"
    :teleported="false"
  >
    <template #reference>
      <div ref="referenceRef" @focusin="handleFocus" @click="handleClick">
        <slot></slot>
      </div>
    </template>
    <div class="icon-picker">
      <ElInput
        v-model="iconSearchText"
        placeholder="搜索图标（支持在线搜索，如：user、home、settings）..."
        clearable
        style="margin-bottom: 12px"
        @input="handleIconSearch"
      >
        <template #prefix>
          <ElIcon><Search /></ElIcon>
        </template>
      </ElInput>
      <div v-if="isSearchingIcons" class="icon-loading">
        <ElIcon class="is-loading"><Loading /></ElIcon>
        <span style="margin-left: 8px">搜索中...</span>
      </div>
      <div
        v-if="!isSearchingIcons && filteredIcons.length === 0 && iconSearchText"
        class="icon-empty"
      >
        未找到相关图标
      </div>
      <!-- 分类显示图标 - 水平块状 -->
      <div v-if="!iconSearchText || iconSearchText.trim().length < 2" class="icon-categories">
        <!-- 分类标签栏 - 水平显示 -->
        <div class="category-tabs">
          <div
            v-for="category in iconCategories"
            :key="category.name"
            class="category-tab"
            :class="{ active: currentCategory === category.name }"
            @click="switchCategory(category.name)"
          >
            {{ category.name }}
          </div>
        </div>
        <!-- 当前分类的图标列表 -->
        <div class="icon-list-horizontal">
          <div
            v-for="iconItem in currentCategoryIcons"
            :key="iconItem"
            class="icon-item-horizontal"
            :class="{ active: (modelValue || '') === iconItem }"
            @click="selectIcon(iconItem)"
          >
            <Icon :icon="iconItem" style="font-size: 20px" />
            <span class="icon-name">{{ iconItem }}</span>
          </div>
        </div>
      </div>
      <!-- 搜索结果 - 水平块状 -->
      <div v-else class="icon-list-horizontal">
        <div
          v-for="iconItem in filteredIcons"
          :key="iconItem"
          class="icon-item-horizontal"
          :class="{ active: (modelValue || '') === iconItem }"
          @click="selectIcon(iconItem)"
        >
          <Icon :icon="iconItem" style="font-size: 20px" />
          <span class="icon-name">{{ iconItem }}</span>
        </div>
      </div>
    </div>
  </ElPopover>
</template>

<script setup lang="ts">
  import { ElIcon, ElPopover, ElInput } from 'element-plus'
  import { Search, Loading } from '@element-plus/icons-vue'
  import { Icon } from '@iconify/vue'

  interface Props {
    modelValue: string | undefined
  }

  interface Emits {
    (e: 'update:modelValue', value: string): void
  }

  defineProps<Props>()
  const emit = defineEmits<Emits>()

  const visible = ref(false)
  const iconSearchText = ref('')
  const isSearchingIcons = ref(false)
  const onlineSearchResults = ref<string[]>([])
  const referenceRef = ref<HTMLElement>()
  const currentCategory = ref('常用')

  // 处理输入框聚焦
  const handleFocus = () => {
    // 使用 nextTick 确保在 DOM 更新后打开
    nextTick(() => {
      visible.value = true
    })
  }

  // 处理点击事件，阻止冒泡并打开选择器
  const handleClick = (event: MouseEvent) => {
    event.stopPropagation()
    // 延迟打开，避免与点击外部监听冲突
    setTimeout(() => {
      visible.value = true
    }, 10)
  }

  // 监听点击外部关闭
  const handleClickOutside = (event: MouseEvent) => {
    if (!visible.value) return

    try {
      const target = event.target as HTMLElement
      if (!target) return

      const popoverEl = document.querySelector('.icon-picker-popover')
      const referenceEl = referenceRef.value

      // 检查元素是否仍在 DOM 中
      const isPopoverValid = popoverEl && document.body.contains(popoverEl)
      const isReferenceValid = referenceEl && document.body.contains(referenceEl)

      // 如果点击的不是 Popover 内部和 reference 区域，则关闭
      if (
        isPopoverValid &&
        !popoverEl.contains(target) &&
        isReferenceValid &&
        !referenceEl.contains(target)
      ) {
        // 延迟关闭，避免与打开事件冲突
        setTimeout(() => {
          if (visible.value) {
            visible.value = false
          }
        }, 10)
      }
    } catch (error) {
      // 忽略 DOM 访问错误，可能发生在组件更新过程中
      console.warn('Icon picker click outside handler error:', error)
    }
  }

  // 监听 visible 变化，添加/移除点击外部监听
  watch(visible, (newVal) => {
    if (newVal) {
      // 延迟添加监听，避免立即触发
      setTimeout(() => {
        document.addEventListener('click', handleClickOutside, true)
      }, 100)
    } else {
      document.removeEventListener('click', handleClickOutside, true)
    }
  })

  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside, true)
    // 清理搜索定时器
    if (searchTimer) {
      clearTimeout(searchTimer)
      searchTimer = null
    }
  })

  // 暴露方法供外部调用
  defineExpose({
    open: () => {
      visible.value = true
    },
    close: () => {
      visible.value = false
    }
  })

  // 图标分类
  const iconCategories = [
    {
      name: '常用',
      icons: [
        'ri:home-line',
        'ri:user-line',
        'ri:user-3-line',
        'ri:settings-line',
        'ri:menu-line',
        'ri:search-line'
      ]
    },
    {
      name: '文件管理',
      icons: [
        'ri:file-list-line',
        'ri:folder-line',
        'ri:folder-open-line',
        'ri:file-line',
        'ri:file-text-line',
        'ri:file-edit-line',
        'ri:folder-add-line'
      ]
    },
    {
      name: '操作',
      icons: [
        'ri:add-line',
        'ri:edit-line',
        'ri:delete-line',
        'ri:save-line',
        'ri:close-line',
        'ri:check-line',
        'ri:refresh-line'
      ]
    },
    {
      name: '方向',
      icons: ['ri:arrow-right-line', 'ri:arrow-left-line', 'ri:arrow-up-line', 'ri:arrow-down-line']
    },
    {
      name: '上传下载',
      icons: ['ri:download-line', 'ri:upload-line']
    },
    {
      name: '安全',
      icons: [
        'ri:eye-line',
        'ri:eye-off-line',
        'ri:lock-line',
        'ri:unlock-line',
        'ri:key-line',
        'ri:shield-line',
        'ri:shield-check-line'
      ]
    },
    {
      name: '通知',
      icons: [
        'ri:notification-line',
        'ri:bell-line',
        'ri:mail-line',
        'ri:message-line',
        'ri:chat-line'
      ]
    },
    {
      name: '时间',
      icons: ['ri:calendar-line', 'ri:time-line']
    },
    {
      name: '图表',
      icons: ['ri:chart-line', 'ri:bar-chart-line', 'ri:pie-chart-line']
    },
    {
      name: '系统',
      icons: [
        'ri:database-line',
        'ri:server-line',
        'ri:computer-line',
        'ri:phone-line',
        'ri:global-line',
        'ri:link-line'
      ]
    },
    {
      name: '媒体',
      icons: ['ri:image-line']
    },
    {
      name: '其他',
      icons: [
        'ri:star-line',
        'ri:heart-line',
        'ri:share-line',
        'ri:grid-line',
        'ri:table-line',
        'ri:layout-line',
        'ri:apps-line',
        'ri:filter-line'
      ]
    },
    {
      name: '提示',
      icons: [
        'ri:question-line',
        'ri:information-line',
        'ri:error-warning-line',
        'ri:check-circle-line',
        'ri:close-circle-line',
        'ri:forbid-line'
      ]
    }
  ]

  // 所有常用图标（用于搜索）
  const commonIcons = iconCategories.flatMap((category) => category.icons)

  // 图标API配置 - 支持多个图标库
  const iconApis = [
    {
      name: 'Iconify 全库',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}`
    },
    {
      name: 'Remix Icon',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=ri`
    },
    {
      name: 'Material Design',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=mdi`
    },
    {
      name: 'Heroicons',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=heroicons`
    },
    {
      name: 'Ant Design',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=ant-design`
    },
    {
      name: 'Feather',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=feather`
    },
    {
      name: 'Font Awesome',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=fa,fa6`
    },
    {
      name: 'Bootstrap',
      url: (keyword: string, limit: number) =>
        `https://api.iconify.design/search?query=${encodeURIComponent(keyword)}&limit=${limit}&prefixes=bx`
    }
  ]

  // 从多个图标API在线搜索图标
  const searchIconsOnline = async (keyword: string): Promise<void> => {
    if (!keyword || keyword.trim().length < 2) {
      onlineSearchResults.value = []
      return
    }

    isSearchingIcons.value = true
    try {
      // 并行请求多个API端点
      const searchPromises = iconApis.map(async (api) => {
        try {
          const response = await fetch(api.url(keyword, 30))
          const data = await response.json()
          return data.icons && Array.isArray(data.icons) ? data.icons : []
        } catch (error) {
          console.warn(`${api.name} API 搜索失败:`, error)
          return []
        }
      })

      // 等待所有请求完成
      const results = await Promise.all(searchPromises)

      // 合并所有结果并去重
      const allIcons = results.flat()
      const uniqueIcons = Array.from(new Set(allIcons))

      // 限制最终结果数量，优先显示常用图标库
      onlineSearchResults.value = uniqueIcons.slice(0, 150)
    } catch (error) {
      console.error('搜索图标失败:', error)
      onlineSearchResults.value = []
    } finally {
      isSearchingIcons.value = false
    }
  }

  // 防抖搜索
  let searchTimer: ReturnType<typeof setTimeout> | null = null
  const handleIconSearch = (): void => {
    if (searchTimer) {
      clearTimeout(searchTimer)
    }

    searchTimer = setTimeout(() => {
      const keyword = iconSearchText.value.trim()
      if (keyword.length >= 2) {
        searchIconsOnline(keyword)
      } else {
        onlineSearchResults.value = []
      }
    }, 500)
  }

  // 过滤图标列表
  const filteredIcons = computed(() => {
    const keyword = iconSearchText.value.trim().toLowerCase()

    // 如果有搜索关键词且长度>=2，使用在线搜索结果
    if (keyword.length >= 2 && onlineSearchResults.value.length > 0) {
      return onlineSearchResults.value
    }

    // 如果有搜索关键词但长度<2，搜索本地常用图标
    if (keyword.length > 0) {
      return commonIcons.filter((icon) => icon.toLowerCase().includes(keyword))
    }

    return []
  })

  // 切换分类
  const switchCategory = (categoryName: string) => {
    currentCategory.value = categoryName
  }

  // 当前分类的图标列表
  const currentCategoryIcons = computed(() => {
    const category = iconCategories.find((cat) => cat.name === currentCategory.value)
    return category ? category.icons : []
  })

  // 选择图标
  const selectIcon = (icon: string) => {
    emit('update:modelValue', icon)
    // 延迟关闭，确保值已更新
    setTimeout(() => {
      visible.value = false
      iconSearchText.value = ''
      onlineSearchResults.value = []
    }, 100)
  }
</script>

<style scoped lang="scss">
  .icon-picker {
    max-height: 500px;
    overflow-y: auto;
  }

  .icon-loading,
  .icon-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    color: #909399;
    font-size: 14px;
  }

  .icon-categories {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .category-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }

  .category-tab {
    padding: 6px 16px;
    font-size: 13px;
    color: #606266;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;

    &:hover {
      color: #409eff;
      border-color: #409eff;
      background: #ecf5ff;
    }

    &.active {
      color: #409eff;
      border-color: #409eff;
      background: #ecf5ff;
      font-weight: 600;
    }
  }

  .icon-list-horizontal {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .icon-item-horizontal {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 12px 16px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    background: #fff;
    min-width: 80px;
    flex-shrink: 0;

    &:hover {
      border-color: #409eff;
      background: #ecf5ff;
      transform: translateY(-2px);
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    &.active {
      border-color: #409eff;
      background: #ecf5ff;
      color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    }

    .icon-name {
      margin-top: 6px;
      font-size: 11px;
      color: #606266;
      text-align: center;
      word-break: break-all;
      line-height: 1.2;
      max-width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &.active .icon-name {
      color: #409eff;
    }
  }
</style>

<style lang="scss">
  .icon-picker-popover {
    .el-popover__title {
      display: none;
    }
  }
</style>
