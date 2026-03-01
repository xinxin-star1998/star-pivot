<template>
  <ElDialog
    v-model="dialogVisible"
    title="代码预览"
    width="80%"
    align-center
    class="code-preview-dialog"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <div v-loading="loading" class="preview-content">
      <ElTabs
        v-model="activePreviewFile"
        type="card"
        v-if="Object.keys(previewCodeMap).length > 0"
        ref="tabsRef"
        class="preview-tabs"
      >
        <ElTabPane
          v-for="(code, fileName) in previewCodeMap"
          :key="fileName"
          :label="fileName"
          :name="fileName"
        >
          <ElScrollbar class="code-scroll">
            <pre class="code-block">{{ code }}</pre>
          </ElScrollbar>
        </ElTabPane>
      </ElTabs>
      <div v-else class="empty-tip">
        <ElEmpty description="暂无预览代码" />
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="handleClose">关闭</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  /**
   * 代码预览弹窗组件
   * 用于预览代码生成器生成的代码文件
   */
  import {
    ElDialog,
    ElTabs,
    ElTabPane,
    ElScrollbar,
    ElButton,
    ElEmpty,
    ElMessage
  } from 'element-plus'
  import { fetchPreviewCode } from '@/api/generator/gen-table'

  interface Props {
    /** 弹窗可见性，由父组件控制 */
    visible: boolean
    /** 表ID，用于获取预览代码 */
    tableId?: number
  }

  interface Emits {
    /** 更新弹窗显示状态 */
    (e: 'update:visible', value: boolean): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 弹窗显示控制，与父组件形成 v-model:visible
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value: boolean) => emit('update:visible', value)
  })

  // 代码预览加载状态
  const loading = ref(false)
  // 代码预览数据 Map<文件名, 代码内容>
  const previewCodeMap = ref<Record<string, string>>({})
  // 当前激活的预览文件名
  const activePreviewFile = ref('')
  // 标签页组件引用
  const tabsRef = ref<InstanceType<typeof ElTabs> | null>(null)

  /**
   * 加载预览代码
   * @param tableId 表ID
   */
  const loadPreviewCode = async (tableId: number): Promise<void> => {
    if (!tableId) {
      ElMessage.warning('表ID不能为空')
      return
    }

    try {
      loading.value = true
      // 调用后端接口获取预览代码 Map<文件名, 代码内容>
      const res = await fetchPreviewCode(tableId)
      previewCodeMap.value = res as unknown as Record<string, string>
      const fileNames = Object.keys(previewCodeMap.value)
      // 设置默认选中的文件为第一个
      activePreviewFile.value = fileNames.length > 0 ? fileNames[0] : ''
      if (fileNames.length === 0) {
        ElMessage.warning('未获取到预览代码')
      }
    } catch (error) {
      console.error('预览代码失败:', error)
      ElMessage.error('预览代码失败')
      previewCodeMap.value = {}
      activePreviewFile.value = ''
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置预览数据
   */
  const resetPreviewData = (): void => {
    previewCodeMap.value = {}
    activePreviewFile.value = ''
  }

  /**
   * 关闭弹窗
   */
  const handleClose = (): void => {
    dialogVisible.value = false
  }

  /**
   * 弹窗关闭后的回调
   */
  const handleClosed = (): void => {
    resetPreviewData()
  }

  /**
   * 获取标签页的滚动容器
   */
  const getScrollContainer = (): HTMLElement | null => {
    if (!tabsRef.value) return null

    const tabsHeader = tabsRef.value.$el?.querySelector('.el-tabs__header') as HTMLElement
    if (!tabsHeader) return null

    // Element Plus Tabs 的可滚动容器可能是 .el-tabs__nav-scroll 或 .el-tabs__nav-wrap
    // 优先查找 .el-tabs__nav-scroll（Element Plus 2.x+）
    let scrollContainer = tabsHeader.querySelector('.el-tabs__nav-scroll') as HTMLElement
    if (!scrollContainer) {
      // 如果没有找到，尝试 .el-tabs__nav-wrap（Element Plus 1.x）
      scrollContainer = tabsHeader.querySelector('.el-tabs__nav-wrap') as HTMLElement
    }
    // 如果还是没找到，尝试直接使用 header 作为滚动容器
    if (!scrollContainer && tabsHeader.scrollWidth > tabsHeader.clientWidth) {
      scrollContainer = tabsHeader
    }

    return scrollContainer
  }

  /**
   * 处理标签页鼠标滚轮事件
   * @param event 滚轮事件
   */
  const handleTabsWheel = (event: WheelEvent): void => {
    const scrollContainer = getScrollContainer()
    if (!scrollContainer) return

    // 检查是否需要滚动（内容宽度大于容器宽度）
    if (scrollContainer.scrollWidth <= scrollContainer.clientWidth) return

    // 阻止默认滚动行为，避免页面滚动
    event.preventDefault()
    event.stopPropagation()

    // 计算滚动距离，优先使用 deltaX（横向），否则使用 deltaY（纵向转横向）
    // deltaY > 0 表示向下滚动，应该向右滚动标签（scrollLeft 增加）
    const delta = Math.abs(event.deltaX) > Math.abs(event.deltaY) ? event.deltaX : event.deltaY

    // 根据滚轮速度调整滚动步长，让滚动更平滑
    // 使用较小的步长以获得更精细的控制
    const scrollStep = Math.abs(delta) > 50 ? 80 : 40
    const scrollDirection = delta > 0 ? 1 : -1

    // 计算目标滚动位置，确保不超出边界
    const currentScroll = scrollContainer.scrollLeft
    const maxScroll = scrollContainer.scrollWidth - scrollContainer.clientWidth
    const targetScroll = Math.max(
      0,
      Math.min(currentScroll + scrollStep * scrollDirection, maxScroll)
    )

    // 直接设置滚动位置
    scrollContainer.scrollLeft = targetScroll
  }

  /**
   * 设置标签页滚轮事件监听
   */
  const setupTabsWheelListener = (): void => {
    nextTick(() => {
      if (!tabsRef.value) return

      const tabsHeader = tabsRef.value.$el?.querySelector('.el-tabs__header') as HTMLElement
      if (!tabsHeader) return

      // 在 header 上监听滚轮事件，这是最可靠的方式
      tabsHeader.addEventListener('wheel', handleTabsWheel, { passive: false })
    })
  }

  /**
   * 移除标签页滚轮事件监听
   */
  const removeTabsWheelListener = (): void => {
    if (!tabsRef.value) return

    const tabsHeader = tabsRef.value.$el?.querySelector('.el-tabs__header') as HTMLElement
    if (tabsHeader) {
      tabsHeader.removeEventListener('wheel', handleTabsWheel)
    }
  }

  /**
   * 监听弹窗显示状态和 tableId 变化
   */
  watch(
    () => [props.visible, props.tableId],
    ([newVisible, newTableId]: [boolean, number | undefined]) => {
      if (newVisible && newTableId) {
        loadPreviewCode(newTableId)
        // 延迟设置滚轮监听，确保 DOM 已渲染
        setTimeout(() => {
          setupTabsWheelListener()
        }, 200)
      } else if (!newVisible) {
        resetPreviewData()
        removeTabsWheelListener()
      }
    },
    { immediate: true }
  )

  /**
   * 监听预览代码数据变化，设置滚轮监听
   */
  watch(
    () => previewCodeMap.value,
    () => {
      if (props.visible && Object.keys(previewCodeMap.value).length > 0) {
        // 延迟设置，确保 DOM 完全渲染
        setTimeout(() => {
          setupTabsWheelListener()
        }, 100)
      }
    }
  )

  /**
   * 组件卸载时清理事件监听
   */
  onUnmounted(() => {
    removeTabsWheelListener()
  })
</script>

<style scoped lang="scss">
  .code-preview-dialog {
    :deep(.el-dialog) {
      border-radius: 16px;
      overflow: hidden;

      .el-dialog__header {
        padding: 20px 24px;
        margin: 0;
        background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-8) 100%);
        border-bottom: 1px solid var(--art-card-border);

        .el-dialog__title {
          font-size: 18px;
          font-weight: 600;
          color: var(--art-gray-900);
        }
      }

      .el-dialog__body {
        padding: 24px;
      }

      .el-dialog__footer {
        padding: 16px 24px;
        border-top: 1px solid var(--art-card-border);
        background-color: var(--art-gray-50);
      }
    }
  }

  .preview-content {
    display: flex;
    flex-direction: column;
    height: 70vh;
    min-height: 400px;

    :deep(.preview-tabs) {
      display: flex;
      flex-direction: column;
      height: 100%;

      .el-tabs__header {
        position: relative;
        flex-shrink: 0;
        margin-bottom: 0;
        user-select: none;

        .el-tabs__nav-wrap,
        .el-tabs__nav-scroll {
          overflow: auto hidden;
          scrollbar-width: thin;
          scrollbar-color: rgb(155 155 155 / 50%) transparent;
          scroll-behavior: smooth;

          &::-webkit-scrollbar {
            height: 6px;
          }

          &::-webkit-scrollbar-track {
            background: transparent;
          }

          &::-webkit-scrollbar-thumb {
            background-color: rgb(155 155 155 / 50%);
            border-radius: 3px;

            &:hover {
              background-color: rgb(155 155 155 / 70%);
            }
          }
        }
      }

      .el-tabs__content {
        display: flex;
        flex: 1;
        flex-direction: column;
        overflow: hidden;

        .el-tab-pane {
          display: flex;
          flex-direction: column;
          height: 100%;
        }
      }
    }
  }

  .code-scroll {
    flex: 1;
    height: 100%;
    overflow: hidden;

    :deep(.el-scrollbar__wrap) {
      overflow: auto;
    }
  }

  .code-block {
    display: block;
    min-width: 100%;
    padding: 12px;
    margin: 0;
    overflow-x: auto;
    font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.5;
    color: #dcdcdc;
    white-space: pre;
    background-color: #1e1e1e;
    border-radius: 4px;
  }

  .empty-tip {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 400px;
  }

  .dialog-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }
</style>
