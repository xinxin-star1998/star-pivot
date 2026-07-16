<template>
  <div class="file-folder-tree">
    <div class="tree-header">
      <span class="tree-title">文件夹</span>
      <ElButton
        v-if="hasAuth('file:folder:add')"
        link
        type="primary"
        @click="emit('add-folder', { category: activeCategory || categories[0]?.category })"
      >
        <ArtSvgIcon class="mr-0.5" icon="ri:add-line" />
        新建
      </ElButton>
    </div>

    <ElInput
      v-model="filterText"
      :prefix-icon="Search"
      class="tree-filter"
      clearable
      placeholder="搜索分类或文件夹"
    />

    <ElScrollbar class="tree-scroll">
      <ElTree
        ref="treeRef"
        :current-node-key="currentNodeKey"
        :data="filteredTreeData"
        :expand-on-click-node="false"
        :props="treeProps"
        default-expand-all
        highlight-current
        node-key="nodeKey"
        @node-click="handleNodeClick"
      >
        <template #default="{ data }">
          <div
            :class="{
              'is-folder': !!data.folderId,
              'is-all': !!data.isAll,
              'is-drop-target': dropTargetKey === data.nodeKey
            }"
            class="tree-node"
            @dragenter.prevent="onNodeDragEnter(data, $event)"
            @dragover.prevent="onNodeDragOver(data, $event)"
            @dragleave.prevent="onNodeDragLeave(data)"
            @drop.prevent="onNodeDrop(data, $event)"
          >
            <ArtSvgIcon :icon="resolveNodeIcon(data)" class="node-icon" />
            <span :title="data.label" class="node-label">{{ data.label }}</span>
            <span v-if="data.fileCount != null" class="node-count">{{ data.fileCount }}</span>
            <ElDropdown
              v-if="data.folderId && hasFolderManage"
              trigger="click"
              @command="(cmd: string) => handleFolderCommand(cmd, data)"
            >
              <ElButton :icon="MoreFilled" class="node-more" link @click.stop />
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem v-if="hasAuth('file:folder:add')" command="add-child">
                    新建子文件夹
                  </ElDropdownItem>
                  <ElDropdownItem
                    v-if="hasAuth('file:folder:edit') && data.folderName !== '默认'"
                    command="edit"
                  >
                    重命名
                  </ElDropdownItem>
                  <ElDropdownItem
                    v-if="hasAuth('file:folder:delete') && data.folderName !== '默认'"
                    command="delete"
                    divided
                  >
                    删除
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </div>
        </template>
      </ElTree>
      <ElEmpty v-if="filteredTreeData.length === 0" :image-size="64" description="无匹配文件夹" />
    </ElScrollbar>
  </div>
</template>

<script lang="ts" setup>
  import type { FileCategoryNode } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { findFolderInTree, mapFoldersToTreeNodes, sumFolderFileCount } from '@/utils/file/folder-tree'
  import { getCategoryIcon } from '../constants'
  import { MoreFilled, Search } from '@element-plus/icons-vue'
  import type { ElTree } from 'element-plus'
  import { computed, nextTick, ref, watch } from 'vue'

  const ALL_FILES_KEY = 'all-files'

  interface TreeNode {
    nodeKey: string
    label: string
    isAll?: boolean
    isFavorite?: boolean
    isRecent?: boolean
    category?: string
    folderId?: number
    folderName?: string
    fileCount?: number
    children?: TreeNode[]
  }

  const props = defineProps<{
    categories: FileCategoryNode[]
    activeFolderId?: number
    /** all | favorite | recent */
    listScope?: string
  }>()

  const emit = defineEmits<{
    'select-all': []
    'select-favorite': []
    'select-recent': []
    'select-folder': [payload: { folderId: number; category: string; folderName: string }]
    'add-folder': [payload: { category?: string; parentId?: number }]
    'edit-folder': [payload: { folderId: number; category: string; folderName: string; parentId?: number }]
    'delete-folder': [folderId: number]
    'drop-files': [payload: { folderId: number; files: File[] }]
    'drop-move': [payload: { folderId: number; fileIds: number[] }]
  }>()

  const { hasAuth } = useAuth()
  const treeRef = ref<InstanceType<typeof ElTree>>()
  const filterText = ref('')
  const dropTargetKey = ref('')

  const hasFolderManage = computed(
    () =>
      hasAuth('file:folder:add') || hasAuth('file:folder:edit') || hasAuth('file:folder:delete')
  )

  const activeCategory = computed(() => {
    if (!props.activeFolderId) return ''
    return findFolderInTree(props.categories, props.activeFolderId)?.category || ''
  })

  const currentNodeKey = computed(() => {
    if (props.listScope === 'favorite') return 'scope-favorite'
    if (props.listScope === 'recent') return 'scope-recent'
    if (props.activeFolderId) return `folder-${props.activeFolderId}`
    return ALL_FILES_KEY
  })

  const categoryNodes = computed<TreeNode[]>(() =>
    props.categories.map((cat) => ({
      nodeKey: `cat-${cat.category}`,
      label: cat.categoryLabel,
      category: cat.category,
      children: mapFoldersToTreeNodes(cat.children || [], cat.category)
    }))
  )

  const totalFileCount = computed(() =>
    props.categories.reduce((sum, cat) => sum + sumFolderFileCount(cat.children || []), 0)
  )

  const treeData = computed<TreeNode[]>(() => [
    {
      nodeKey: ALL_FILES_KEY,
      label: '全部文件',
      isAll: true,
      fileCount: totalFileCount.value,
      children: categoryNodes.value
    },
    {
      nodeKey: 'scope-favorite',
      label: '我的收藏',
      isFavorite: true
    },
    {
      nodeKey: 'scope-recent',
      label: '最近访问',
      isRecent: true
    }
  ])

  function filterNodes(nodes: TreeNode[], kw: string): TreeNode[] {
    return nodes
      .map((node) => {
        const selfMatch = node.label.toLowerCase().includes(kw)
        const children = filterNodes(node.children || [], kw)
        if (selfMatch || children.length) {
          return { ...node, children: selfMatch ? node.children || [] : children }
        }
        return null
      })
      .filter(Boolean) as TreeNode[]
  }

  const filteredTreeData = computed(() => {
    const kw = filterText.value.trim().toLowerCase()
    if (!kw) return treeData.value

    const filteredCategories = filterNodes(categoryNodes.value, kw)
    const extras = treeData.value.slice(1).filter((n) => n.label.toLowerCase().includes(kw))
    const showAll = '全部文件'.includes(kw) || filteredCategories.length > 0
    const result: TreeNode[] = []
    if (showAll) {
      result.push({ ...treeData.value[0], children: filteredCategories })
    }
    result.push(...extras)
    return result
  })

  const treeProps = {
    children: 'children',
    label: 'label'
  }

  watch(
    currentNodeKey,
    (key) => {
      nextTick(() => treeRef.value?.setCurrentKey(key))
    },
    { immediate: true }
  )

  function resolveNodeIcon(data: TreeNode) {
    if (data.isAll) return 'ri:file-list-3-line'
    if (data.isFavorite) return 'ri:star-fill'
    if (data.isRecent) return 'ri:history-line'
    if (data.folderId) return 'ri:folder-3-line'
    return getCategoryIcon(data.category)
  }

  function handleNodeClick(data: TreeNode) {
    if (data.isAll) {
      emit('select-all')
      return
    }
    if (data.isFavorite) {
      emit('select-favorite')
      return
    }
    if (data.isRecent) {
      emit('select-recent')
      return
    }
    if (data.folderId && data.category) {
      emit('select-folder', {
        folderId: data.folderId,
        category: data.category,
        folderName: data.folderName || data.label || ''
      })
    }
  }

  function handleFolderCommand(command: string, data: TreeNode) {
    if (command === 'add-child' && data.folderId && data.category) {
      emit('add-folder', { category: data.category, parentId: data.folderId })
    } else if (command === 'edit' && data.folderId && data.category) {
      emit('edit-folder', {
        folderId: data.folderId,
        category: data.category,
        folderName: data.folderName || data.label || ''
      })
    } else if (command === 'delete' && data.folderId) {
      emit('delete-folder', data.folderId)
    }
  }

  function onNodeDragEnter(data: TreeNode, event: DragEvent) {
    if (!data.folderId) return
    dropTargetKey.value = data.nodeKey
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = event.dataTransfer.types.includes('Files') ? 'copy' : 'move'
    }
  }

  function onNodeDragOver(data: TreeNode, event: DragEvent) {
    if (!data.folderId) return
    dropTargetKey.value = data.nodeKey
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = event.dataTransfer.types.includes('Files') ? 'copy' : 'move'
    }
  }

  function onNodeDragLeave(data: TreeNode) {
    if (dropTargetKey.value === data.nodeKey) {
      dropTargetKey.value = ''
    }
  }

  function onNodeDrop(data: TreeNode, event: DragEvent) {
    dropTargetKey.value = ''
    if (!data.folderId) return

    const fileList = event.dataTransfer?.files
    if (fileList?.length) {
      emit('drop-files', { folderId: data.folderId, files: Array.from(fileList) })
      return
    }

    const raw = event.dataTransfer?.getData('application/x-star-file-ids')
    if (!raw) return
    try {
      const fileIds = JSON.parse(raw) as number[]
      if (Array.isArray(fileIds) && fileIds.length) {
        emit('drop-move', { folderId: data.folderId, fileIds })
      }
    } catch {
      // ignore
    }
  }
</script>

<style lang="scss" scoped>
  .file-folder-tree {
    display: flex;
    flex-direction: column;
    height: 100%;
    min-height: 0;
  }

  .tree-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .tree-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .tree-filter {
    margin-bottom: 10px;
  }

  .tree-scroll {
    flex: 1;
    min-height: 0;
  }

  .tree-node {
    display: flex;
    flex: 1;
    gap: 6px;
    align-items: center;
    min-width: 0;
    padding-right: 4px;
    border-radius: 4px;

    &.is-folder:hover .node-more {
      opacity: 1;
    }

    &.is-drop-target {
      background: color-mix(in srgb, var(--el-color-primary) 14%, transparent);
      outline: 1px dashed var(--el-color-primary);
    }
  }

  .node-icon {
    flex-shrink: 0;
    font-size: 15px;
    color: var(--el-color-primary);
  }

  .node-label {
    flex: 1;
    overflow: hidden;
    font-size: 13px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .node-count {
    flex-shrink: 0;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
    font-size: 11px;
    line-height: 18px;
    color: var(--el-text-color-secondary);
    text-align: center;
    background: var(--el-fill-color);
    border-radius: 9px;
  }

  .node-more {
    flex-shrink: 0;
    padding: 0 2px;
    opacity: 0;
    transition: opacity 0.15s;
  }

  :deep(.el-tree-node__content) {
    height: 34px;
    border-radius: 6px;
  }

  :deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
    background: var(--el-color-primary-light-9);
  }
</style>
