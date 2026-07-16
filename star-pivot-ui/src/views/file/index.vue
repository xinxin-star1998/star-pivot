<template>
  <div class="file-page art-full-height">
    <div class="file-layout">
      <ElCard v-if="activeTab === 'all'" class="file-sidebar" shadow="never">
        <FileFolderTree
          :active-folder-id="selectedFolderId"
          :categories="categoryTree"
          :list-scope="fileListScope"
          @select-all="handleSelectAll"
          @select-favorite="handleSelectFavorite"
          @select-recent="handleSelectRecent"
          @select-folder="handleSelectFolder"
          @add-folder="handleAddFolder"
          @edit-folder="handleEditFolder"
          @delete-folder="handleDeleteFolder"
          @drop-files="onDropFilesToFolder"
          @drop-move="onDropMoveToFolder"
        />
      </ElCard>

      <div
        class="file-main"
        :class="{ 'is-dragover': dragOverMain }"
        @dragenter.prevent="onMainDragEnter"
        @dragover.prevent="onMainDragOver"
        @dragleave.prevent="onMainDragLeave"
        @drop.prevent="onMainDrop"
      >
        <div v-if="dragOverMain && activeTab === 'all'" class="drop-mask">
          松开以上传到{{ selectedFolderId ? '当前文件夹' : '所选目标（将打开上传框）' }}
        </div>
        <FileSearch
          v-model="searchForm"
          :recycle="activeTab === 'recycle'"
          @reset="resetSearch"
          @search="handleSearch"
        />

        <ElCard class="art-table-card file-table-card" shadow="never">
          <div class="file-table-card__top">
            <div class="panel-toolbar">
              <ElTabs v-model="activeTab" class="panel-tabs" @tab-change="handleTabChange">
                <ElTabPane label="全部文件" name="all" />
                <ElTabPane label="回收站" name="recycle" />
              </ElTabs>

              <div v-if="activeTab === 'all' && locationText" class="location-bar">
                <ArtSvgIcon class="location-icon" icon="ri:folder-open-line" />
                <span class="location-text">{{ locationText }}</span>
              </div>
            </div>

            <div v-if="activeTab === 'all'" class="media-filter">
              <ElRadioGroup v-model="mediaTypeFilter" size="small" @change="handleSearch">
                <ElRadioButton v-for="item in MEDIA_TYPES" :key="item.code" :value="item.code">
                  <ArtSvgIcon
                    v-if="item.code"
                    :icon="getMediaTypeIcon(item.code)"
                    class="filter-icon"
                  />
                  {{ item.label }}
                </ElRadioButton>
              </ElRadioGroup>
              <ElSelect
                v-if="hasAuth('file:resource:tag')"
                v-model="tagFilterId"
                clearable
                placeholder="标签筛选"
                style="width: 140px"
                size="small"
                @change="handleSearch"
              >
                <ElOption
                  v-for="tag in tagOptions"
                  :key="tag.tagId"
                  :label="tag.tagName"
                  :value="tag.tagId!"
                />
              </ElSelect>
              <ElSegmented v-model="viewMode" :options="viewModeOptions" size="small" />
            </div>

            <ArtTableHeader
              v-model:columns="columnChecks"
              :loading="loading"
              @refresh="refreshData"
            >
              <template #left>
                <ElSpace wrap>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:add'"
                    v-ripple
                    type="primary"
                    @click="uploadVisible = true"
                  >
                    <ArtSvgIcon class="mr-1" icon="ri:upload-2-line" />
                    上传
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:move'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    @click="openMoveDialog(selectedRows.map((r) => r.fileId!))"
                  >
                    <ArtSvgIcon class="mr-1" icon="ri:folder-transfer-line" />
                    迁移
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:download'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    :loading="zipDownloading"
                    @click="handleBatchZipDownload"
                  >
                    <ArtSvgIcon class="mr-1" icon="ri:download-cloud-2-line" />
                    打包下载
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:tag'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    @click="openTagBind(selectedRows.map((r) => r.fileId!))"
                  >
                    <ArtSvgIcon class="mr-1" icon="ri:price-tag-3-line" />
                    打标签
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:tag'"
                    v-ripple
                    @click="tagDialogMode = 'manage'; tagDialogVisible = true"
                  >
                    标签管理
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'all'"
                    v-auth="'file:resource:delete'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    type="danger"
                    @click="handleBatchDelete"
                  >
                    批量删除
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'recycle'"
                    v-auth="'file:resource:restore'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    type="primary"
                    @click="handleBatchRestore"
                  >
                    批量恢复
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'recycle'"
                    v-auth="'file:resource:purge'"
                    v-ripple
                    :disabled="selectedRows.length === 0"
                    type="danger"
                    @click="handleBatchPurge"
                  >
                    彻底删除
                  </ElButton>
                  <ElButton
                    v-if="activeTab === 'recycle'"
                    v-auth="'file:resource:purge'"
                    v-ripple
                    type="danger"
                    plain
                    @click="handleClearRecycle"
                  >
                    清空回收站
                  </ElButton>
                </ElSpace>
              </template>
            </ArtTableHeader>
          </div>

          <div class="file-table-card__body">
            <ArtTable
              v-if="viewMode === 'table' || activeTab === 'recycle'"
              :key="`file-table-${activeTab}-${listMode}`"
              :columns="columns"
              :data="data"
              :loading="loading"
              :pagination="pagination"
              :pagination-options="{ align: 'right' }"
              :show-table-header="false"
              @selection-change="handleSelectionChange"
              @pagination:size-change="handleSizeChange"
              @pagination:current-change="handleCurrentChange"
            />
            <template v-else>
              <FileGridView
                v-if="viewMode === 'grid'"
                v-model:selected-rows="selectedRows"
                :data="data"
                :loading="loading"
                @preview="openPreview"
              />
              <FileTimelineView
                v-else
                v-model:selected-rows="selectedRows"
                :data="data"
                :loading="loading"
                @preview="openPreview"
              />
              <div class="view-pagination">
                <ElPagination
                  v-model:current-page="pagination.current"
                  v-model:page-size="pagination.size"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="pagination.total"
                  background
                  layout="total, sizes, prev, pager, next"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </template>
          </div>
        </ElCard>
      </div>
    </div>

    <FileUploadDialog
      v-model:visible="uploadVisible"
      :categories="categoryTree"
      :default-folder-id="uploadTargetFolderId ?? selectedFolderId"
      :seed-files="uploadSeedFiles"
      @success="onUploadSuccess"
      @closed="uploadSeedFiles = []; uploadTargetFolderId = undefined"
    />

    <FilePreviewDialog
      v-model:visible="previewVisible"
      :file="previewFile"
      @delete="handlePreviewDelete"
      @move="openMoveDialog"
      @renamed="refreshData"
    />

    <FileMoveDialog
      v-model:visible="moveVisible"
      :categories="categoryTree"
      :exclude-folder-id="selectedFolderId"
      :file-ids="moveFileIds"
      @success="onMoveSuccess"
    />

    <FolderDialog
      v-model:visible="folderDialogVisible"
      :data="folderDialogData"
      :default-category="folderDialogCategory"
      :parent-id="folderDialogParentId"
      :parent-label="folderDialogParentLabel"
      :type="folderDialogType"
      @success="loadFolderTree"
    />

    <FileTagDialog
      v-model="tagDialogVisible"
      :mode="tagDialogMode"
      :file-ids="tagBindFileIds"
      :initial-tag-ids="tagBindInitialIds"
      @success="onTagSuccess"
    />

    <FileVersionDialog
      v-model="versionDialogVisible"
      :file="versionFile"
      @success="refreshData"
    />
  </div>
</template>

<script lang="ts" setup>
  import {
    deleteFiles,
    downloadFilesZip,
    fetchFileList,
    fetchFilePreviewUrl,
    fetchFileTagList,
    fetchRecycleList,
    moveFiles,
    purgeFiles,
    clearRecycleBin,
    renameFile,
    restoreFiles,
    toggleFileFavorite
  } from '@/api/file/file'
  import { deleteFolder, fetchFolderTree } from '@/api/file/folder'
  import type { FileCategoryNode, SysFile, SysFileFolderForm, SysFileTag } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { useAuth } from '@/hooks/core/useAuth'
  import { formatFileSize, openFileUrl, resolveFileDisplayUrl } from '@/utils/file/file-center'
  import { findFolderInTree } from '@/utils/file/folder-tree'
  import { handleMutationError } from '@/utils/http/mutation'
  import { ElImage, ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { computed, h, onActivated, onMounted, ref } from 'vue'
  import { getCategoryLabel, getMediaTypeIcon, MEDIA_TYPE_TAG, MEDIA_TYPES } from './constants'
  import FileFolderTree from './modules/file-folder-tree.vue'
  import FileGridView from './modules/file-grid-view.vue'
  import FileMoveDialog from './modules/file-move-dialog.vue'
  import FilePreviewDialog from './modules/file-preview-dialog.vue'
  import FileSearch from './modules/file-search.vue'
  import FileTagDialog from './modules/file-tag-dialog.vue'
  import FileVersionDialog from './modules/file-version-dialog.vue'
  import FileTimelineView from './modules/file-timeline-view.vue'
  import FileUploadDialog from './modules/file-upload-dialog.vue'
  import FolderDialog from './modules/folder-dialog.vue'

  defineOptions({ name: 'FileManage' })

  const { hasAuth } = useAuth()

  const activeTab = ref<'all' | 'recycle'>('all')
  /** 与 activeTab 同步，供 listApi 读取，避免 Tab 切换瞬间 API 选错 */
  const listMode = ref<'all' | 'recycle'>('all')
  const categoryTree = ref<FileCategoryNode[]>([])
  const selectedFolderId = ref<number>()
  const selectedCategory = ref('')
  const selectedFolderName = ref('')
  /** all | favorite | recent */
  const fileListScope = ref('all')
  const mediaTypeFilter = ref('')
  const tagFilterId = ref<number>()
  const tagOptions = ref<SysFileTag[]>([])
  const selectedRows = ref<SysFile[]>([])
  const viewMode = ref<'table' | 'grid' | 'timeline'>('table')
  const viewModeOptions = [
    { label: '列表', value: 'table' },
    { label: '网格', value: 'grid' },
    { label: '时间', value: 'timeline' }
  ]

  const uploadVisible = ref(false)
  const uploadSeedFiles = ref<File[]>([])
  const uploadTargetFolderId = ref<number>()
  const dragOverMain = ref(false)
  let dragEnterCount = 0
  const moveVisible = ref(false)
  const moveFileIds = ref<number[]>([])
  const pageInitialized = ref(false)
  const previewVisible = ref(false)
  const previewFile = ref<SysFile | null>(null)
  const zipDownloading = ref(false)
  const tagDialogVisible = ref(false)
  const tagDialogMode = ref<'manage' | 'bind'>('manage')
  const tagBindFileIds = ref<number[]>([])
  const tagBindInitialIds = ref<number[]>([])
  const versionDialogVisible = ref(false)
  const versionFile = ref<SysFile | null>(null)

  const folderDialogVisible = ref(false)
  const folderDialogType = ref<'add' | 'edit'>('add')
  const folderDialogData = ref<SysFileFolderForm>()
  const folderDialogCategory = ref('')
  const folderDialogParentId = ref(0)
  const folderDialogParentLabel = ref('')

  const searchForm = ref<Record<string, unknown>>({
    fileName: undefined,
    createBy: undefined,
    deleteBy: undefined,
    category: undefined,
    timeRange: undefined
  })

  const isRecycle = computed(() => activeTab.value === 'recycle')

  const locationText = computed(() => {
    if (fileListScope.value === 'favorite') return '我的收藏'
    if (fileListScope.value === 'recent') return '最近访问'
    if (!selectedFolderId.value) return '全部文件'
    const found = findFolderInTree(categoryTree.value, selectedFolderId.value)
    if (found?.pathNames?.length) {
      return found.pathNames.join(' / ')
    }
    const catLabel = getCategoryLabel(selectedCategory.value)
    return `${catLabel} / ${selectedFolderName.value || '默认'}`
  })

  const listApi = (params: Record<string, unknown>) => {
    if (listMode.value === 'recycle') {
      return fetchRecycleList(params as never)
    }
    return fetchFileList(params as never)
  }

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    clearData,
    cancelRequest,
    handleSizeChange,
    handleCurrentChange,
    refreshData,
    resetColumns
  } = useTable({
    core: {
      apiFn: listApi,
      apiParams: {
        pageNum: 1,
        pageSize: 20
      },
      columnsFactory: () => buildColumns(isRecycle.value),
      immediate: false
    }
  })

  function renderFileThumb(row: SysFile) {
    const url = resolveFileDisplayUrl(row)
    if (row.mediaType === 'IMAGE' && url) {
      return h(ElImage, {
        key: `thumb-${row.fileId}-${url}`,
        src: url,
        fit: 'cover',
        previewSrcList: [url],
        previewTeleported: true,
        class: 'file-thumb',
        onClick: (event: Event) => event.stopPropagation()
      })
    }
    return h(ArtSvgIcon, {
      icon: getMediaTypeIcon(row.mediaType),
      class: 'file-type-icon'
    })
  }

  function buildColumns(recycle: boolean) {
    const cols = [
      { type: 'selection' as const },
      { type: 'index' as const, width: 60, label: '序号' },
      {
        prop: 'fileName',
        label: '文件名',
        minWidth: 280,
        formatter: (row: SysFile) =>
          h(
            'div',
            {
              class: 'file-name-cell',
              draggable: !recycle,
              onDragstart: (e: DragEvent) => onFileRowDragStart(row, e),
              onClick: () => !recycle && hasAuth('file:resource:query') && openPreview(row)
            },
            [
              renderFileThumb(row),
              h('div', { class: 'file-name-main' }, [
                h(
                  'span',
                  {
                    class: [
                      'file-name-text',
                      !recycle && hasAuth('file:resource:query') ? 'is-link' : ''
                    ]
                  },
                  row.fileName
                ),
                !recycle &&
                  row.tags?.length &&
                  h(
                    'div',
                    { class: 'file-tag-row', onClick: (e: Event) => e.stopPropagation() },
                    row.tags.slice(0, 3).map((tag) =>
                      h(
                        ElTag,
                        {
                          size: 'small',
                          effect: 'plain',
                          style: { borderColor: tag.tagColor, color: tag.tagColor }
                        },
                        () => tag.tagName
                      )
                    )
                  )
              ])
            ]
          )
      }
    ]

    if (recycle || !selectedFolderId.value) {
      cols.push({
        prop: 'categoryLabel',
        label: '业务分类',
        width: 110,
        formatter: (row: SysFile) => row.categoryLabel || getCategoryLabel(row.category)
      } as never)
    }

    cols.push(
      {
        prop: 'mediaTypeLabel',
        label: '类型',
        width: 96,
        formatter: (row: SysFile) =>
          h(
            ElTag,
            { type: MEDIA_TYPE_TAG[row.mediaType || ''] || 'info', size: 'small' },
            () => row.mediaTypeLabel || row.mediaType
          )
      } as never,
      {
        prop: 'fileSize',
        label: '大小',
        width: 96,
        formatter: (row: SysFile) => formatFileSize(row.fileSize)
      } as never,
      {
        prop: recycle ? 'deleteBy' : 'createBy',
        label: recycle ? '删除人' : '上传人',
        width: 100
      } as never,
      {
        prop: recycle ? 'deleteTime' : 'createTime',
        label: recycle ? '删除时间' : '上传时间',
        width: 168
      } as never,
      {
        prop: 'operation',
        label: '操作',
        width: recycle ? 160 : 240,
        fixed: 'right' as const,
        formatter: (row: SysFile) =>
          h('div', { class: 'file-op-cell' }, [
            !recycle &&
              hasAuth('file:resource:query') &&
              h(ArtButtonTable, { type: 'view', onClick: () => openPreview(row) }),
            !recycle &&
              hasAuth('file:resource:query') &&
              h(ArtButtonTable, {
                type: 'download',
                tooltip: '下载',
                onClick: () => handleDownload(row)
              }),
            !recycle &&
              hasAuth('file:resource:query') &&
              h(ArtButtonTable, {
                icon: row.favorited ? 'ri:star-fill' : 'ri:star-line',
                iconClass: row.favorited
                  ? 'bg-warning/12 text-warning'
                  : 'bg-g-300/40 text-g-600',
                tooltip: row.favorited ? '取消收藏' : '收藏',
                onClick: () => handleToggleFavorite(row)
              }),
            !recycle &&
              hasAuth('file:resource:tag') &&
              h(ArtButtonTable, {
                icon: 'ri:price-tag-3-line',
                tooltip: '打标签',
                onClick: () =>
                  openTagBind(
                    [row.fileId!],
                    (row.tags || []).map((t) => t.tagId!).filter(Boolean)
                  )
              }),
            !recycle &&
              hasAuth('file:resource:version') &&
              h(ArtButtonTable, {
                icon: 'ri:history-line',
                tooltip: '版本',
                onClick: () => openVersionDialog(row)
              }),
            !recycle &&
              hasAuth('file:resource:move') &&
              h(ArtButtonTable, {
                type: 'edit',
                tooltip: '迁移',
                onClick: () => openMoveDialog([row.fileId!])
              }),
            !recycle &&
              hasAuth('file:resource:edit') &&
              h(ArtButtonTable, {
                icon: 'ri:text',
                iconClass: 'bg-warning/12 text-warning',
                tooltip: '重命名',
                onClick: () => handleRename(row)
              }),
            !recycle &&
              hasAuth('file:resource:delete') &&
              h(ArtButtonTable, { type: 'delete', onClick: () => handleDelete([row.fileId!]) }),
            recycle &&
              hasAuth('file:resource:restore') &&
              h(ArtButtonTable, { type: 'resume', onClick: () => handleRestore([row.fileId!]) }),
            recycle &&
              hasAuth('file:resource:purge') &&
              h(ArtButtonTable, {
                type: 'delete',
                tooltip: '彻底删除',
                onClick: () => handlePurge([row.fileId!])
              })
          ])
      } as never
    )

    if (!recycle) {
      const op = cols[cols.length - 1] as { width?: number }
      op.width = 340
    }

    return cols
  }

  async function loadFolderTree() {
    const tree = await fetchFolderTree()
    categoryTree.value = tree || []
  }

  async function loadTagOptions() {
    if (!hasAuth('file:resource:tag')) {
      tagOptions.value = []
      return
    }
    try {
      tagOptions.value = (await fetchFileTagList()) || []
    } catch {
      tagOptions.value = []
    }
  }

  async function initPage() {
    await Promise.all([loadFolderTree(), loadTagOptions()])
    await handleSearch()
  }

  function handleSelectAll() {
    fileListScope.value = 'all'
    selectedFolderId.value = undefined
    selectedCategory.value = ''
    selectedFolderName.value = ''
    resetColumns?.()
    handleSearch()
  }

  function handleSelectFavorite() {
    fileListScope.value = 'favorite'
    selectedFolderId.value = undefined
    selectedCategory.value = ''
    selectedFolderName.value = ''
    resetColumns?.()
    handleSearch()
  }

  function handleSelectRecent() {
    fileListScope.value = 'recent'
    selectedFolderId.value = undefined
    selectedCategory.value = ''
    selectedFolderName.value = ''
    resetColumns?.()
    handleSearch()
  }

  function handleSelectFolder(payload: { folderId: number; category: string; folderName: string }) {
    fileListScope.value = 'all'
    selectedFolderId.value = payload.folderId
    selectedCategory.value = payload.category
    selectedFolderName.value = payload.folderName
    resetColumns?.()
    handleSearch()
  }

  function parseTimeRange(range: unknown) {
    if (!Array.isArray(range) || range.length !== 2) {
      return { beginTime: undefined, endTime: undefined }
    }
    return { beginTime: range[0], endTime: range[1] }
  }

  function clearTableSearchParams() {
    const paramsRecord = searchParams as Record<string, unknown>
    Object.keys(searchParams).forEach((key) => {
      delete paramsRecord[key]
    })
    Object.assign(searchParams, { pageNum: 1, pageSize: 20 })
  }

  async function handleSearch(tab: 'all' | 'recycle' = listMode.value) {
    const { beginTime, endTime } = parseTimeRange(searchForm.value.timeRange)
    const keyword = (searchForm.value.fileName as string | undefined)?.trim()
    const params: Record<string, unknown> = {
      pageNum: 1,
      beginTime,
      endTime
    }
    if (tab === 'recycle') {
      params.fileName = keyword || undefined
      params.keyword = undefined
      params.category = searchForm.value.category
      params.deleteBy = searchForm.value.deleteBy
      params.folderId = undefined
      params.mediaType = undefined
      params.createBy = undefined
      params.listScope = undefined
      params.tagId = undefined
    } else {
      const scoped = fileListScope.value === 'favorite' || fileListScope.value === 'recent'
      params.keyword = keyword || undefined
      params.fileName = undefined
      params.folderId = scoped ? undefined : selectedFolderId.value
      params.category = !scoped && selectedFolderId.value ? selectedCategory.value : undefined
      params.mediaType = mediaTypeFilter.value || undefined
      params.createBy = searchForm.value.createBy
      params.deleteBy = undefined
      params.listScope = scoped ? fileListScope.value : undefined
      params.tagId = tagFilterId.value || undefined
    }
    Object.assign(searchParams, params)
    await getData()
  }

  async function resetSearch(tab: 'all' | 'recycle' = listMode.value) {
    searchForm.value = {
      fileName: undefined,
      createBy: undefined,
      deleteBy: undefined,
      category: undefined,
      timeRange: undefined
    }
    mediaTypeFilter.value = ''
    tagFilterId.value = undefined
    clearTableSearchParams()
    await handleSearch(tab)
  }

  async function handleTabChange(tab: 'all' | 'recycle') {
    // 先切 listMode，避免 clearData/请求期间仍打到上一 Tab 的接口
    listMode.value = tab
    selectedRows.value = []
    cancelRequest()
    clearData()
    resetColumns?.()
    // resetSearch -> getData 会回到第一页，无需单独 resetPagination
    await resetSearch(tab)
  }

  function handleSelectionChange(rows: SysFile[]) {
    selectedRows.value = rows
  }

  function openMoveDialog(ids: number[]) {
    if (!ids.length) return
    moveFileIds.value = ids
    moveVisible.value = true
  }

  function onMoveSuccess(targetFolderId: number) {
    selectedRows.value = []
    previewVisible.value = false
    if (targetFolderId === selectedFolderId.value) {
      refreshData()
    } else {
      const found = findFolderInTree(categoryTree.value, targetFolderId)
      if (found) {
        handleSelectFolder({
          folderId: targetFolderId,
          category: found.category,
          folderName: found.folder.folderName || ''
        })
      }
    }
    loadFolderTree()
  }

  function openPreview(file: SysFile) {
    previewFile.value = file
    previewVisible.value = true
  }

  async function handlePreviewDelete(fileId: number) {
    await handleDelete([fileId])
  }

  async function handleDownload(file: SysFile) {
    try {
      const res = await fetchFilePreviewUrl(file.fileId!)
      openFileUrl(res.url, file.fileName)
    } catch {
      if (file.displayUrl) {
        openFileUrl(file.displayUrl, file.fileName)
      }
    }
  }

  async function handleToggleFavorite(file: SysFile) {
    if (!file.fileId) return
    try {
      const res = await toggleFileFavorite(file.fileId)
      file.favorited = res.favorited
      ElMessage.success(res.favorited ? '已收藏' : '已取消收藏')
      if (fileListScope.value === 'favorite' && !res.favorited) {
        refreshData()
      }
    } catch (error) {
      handleMutationError(error, '收藏操作失败')
    }
  }

  function openTagBind(fileIds: number[], initialTagIds: number[] = []) {
    tagDialogMode.value = 'bind'
    tagBindFileIds.value = fileIds
    tagBindInitialIds.value = initialTagIds
    tagDialogVisible.value = true
  }

  function openVersionDialog(file: SysFile) {
    versionFile.value = file
    versionDialogVisible.value = true
  }

  async function onTagSuccess() {
    await loadTagOptions()
    if (tagDialogMode.value === 'bind') {
      refreshData()
    }
  }

  async function handleBatchZipDownload() {
    const ids = selectedRows.value.map((r) => r.fileId!).filter(Boolean)
    if (!ids.length) return
    zipDownloading.value = true
    try {
      await downloadFilesZip(ids)
      ElMessage.success('打包下载已开始')
    } catch (error) {
      handleMutationError(error, '打包下载失败')
    } finally {
      zipDownloading.value = false
    }
  }

  async function handleRename(file: SysFile) {
    if (!file.fileId) return
    try {
      const { value } = await ElMessageBox.prompt('请输入新的文件名', '重命名', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: file.fileName || '',
        inputPlaceholder: '例如：avatar1.webp',
        inputValidator: (val) => {
          const name = val?.trim()
          if (!name) return '文件名不能为空'
          if (name.includes('/') || name.includes('\\')) return '文件名不能包含路径分隔符'
          return true
        }
      })
      const fileName = value.trim()
      await renameFile({ fileId: file.fileId, fileName })
      ElMessage.success('重命名成功')
      refreshData()
    } catch (error) {
      handleMutationError(error, '重命名失败')
    }
  }

  function openFolderDialog(
    type: 'add' | 'edit',
    data?: SysFileFolderForm,
    options?: { category?: string; parentId?: number; parentLabel?: string }
  ) {
    folderDialogType.value = type
    folderDialogData.value = data
    folderDialogCategory.value = options?.category || selectedCategory.value
    folderDialogParentId.value = options?.parentId || 0
    folderDialogParentLabel.value = options?.parentLabel || ''
    folderDialogVisible.value = true
  }

  function handleAddFolder(payload: { category?: string; parentId?: number }) {
    let parentLabel = ''
    if (payload.parentId) {
      const found = findFolderInTree(categoryTree.value, payload.parentId)
      parentLabel = found?.pathNames?.join(' / ') || ''
    }
    openFolderDialog('add', undefined, {
      category: payload.category,
      parentId: payload.parentId || 0,
      parentLabel
    })
  }

  function handleEditFolder(payload: { folderId: number; category: string; folderName: string }) {
    openFolderDialog(
      'edit',
      {
        folderId: payload.folderId,
        category: payload.category,
        folderName: payload.folderName
      },
      { category: payload.category }
    )
  }

  async function handleDeleteFolder(folderId: number) {
    try {
      await ElMessageBox.confirm('删除后文件夹不可恢复，且须为空文件夹。确认删除？', '提示', {
        type: 'warning'
      })
      await deleteFolder(folderId)
      ElMessage.success('文件夹已删除')
      if (selectedFolderId.value === folderId) {
        selectedFolderId.value = undefined
        selectedCategory.value = ''
        selectedFolderName.value = ''
        resetColumns?.()
      }
      await loadFolderTree()
      if (!selectedFolderId.value && activeTab.value === 'all') {
        await handleSearch()
      }
    } catch (error) {
      handleMutationError(error, '删除文件夹失败')
    }
  }

  async function handleDelete(ids: number[]) {
    try {
      await ElMessageBox.confirm('确认将选中文件移入回收站？', '提示', { type: 'warning' })
      await deleteFiles(ids)
      ElMessage.success('已移入回收站')
      selectedRows.value = []
      refreshData()
      loadFolderTree()
    } catch (error) {
      handleMutationError(error, '删除失败')
    }
  }

  async function handleBatchDelete() {
    await handleDelete(selectedRows.value.map((r) => r.fileId!))
  }

  async function handleRestore(ids: number[]) {
    try {
      await ElMessageBox.confirm('确认恢复选中文件？', '提示', { type: 'info' })
      await restoreFiles(ids)
      ElMessage.success('恢复成功')
      selectedRows.value = []
      refreshData()
    } catch (error) {
      handleMutationError(error, '恢复失败')
    }
  }

  async function handleBatchRestore() {
    await handleRestore(selectedRows.value.map((r) => r.fileId!))
  }

  async function handlePurge(ids: number[]) {
    try {
      await ElMessageBox.confirm(
        '彻底删除后不可恢复，并将清理无引用的 OSS 对象。确认继续？',
        '彻底删除',
        { type: 'warning' }
      )
      await purgeFiles(ids)
      ElMessage.success('已彻底删除')
      selectedRows.value = []
      refreshData()
      loadFolderTree()
    } catch (error) {
      handleMutationError(error, '彻底删除失败')
    }
  }

  async function handleBatchPurge() {
    await handlePurge(selectedRows.value.map((r) => r.fileId!))
  }

  async function handleClearRecycle() {
    try {
      await ElMessageBox.confirm(
        '将清空当前权限范围内回收站全部文件，且不可恢复。确认继续？',
        '清空回收站',
        { type: 'warning' }
      )
      const count = await clearRecycleBin()
      ElMessage.success(count ? `已清空 ${count} 个文件` : '回收站已为空')
      selectedRows.value = []
      refreshData()
      loadFolderTree()
    } catch (error) {
      handleMutationError(error, '清空回收站失败')
    }
  }

  function onUploadSuccess(folderId: number) {
    if (folderId && folderId !== selectedFolderId.value) {
      const found = findFolderInTree(categoryTree.value, folderId)
      if (found) {
        handleSelectFolder({
          folderId,
          category: found.category,
          folderName: found.folder.folderName || ''
        })
      } else {
        refreshData()
      }
    } else {
      refreshData()
    }
    loadFolderTree()
  }

  function openUploadWithFiles(files: File[], folderId?: number) {
    if (!files.length) return
    if (!hasAuth('file:resource:add')) {
      ElMessage.warning('无上传权限')
      return
    }
    uploadSeedFiles.value = files
    uploadTargetFolderId.value = folderId ?? selectedFolderId.value
    uploadVisible.value = true
  }

  function collectDroppedFiles(event: DragEvent): File[] {
    const list = event.dataTransfer?.files
    if (!list?.length) return []
    return Array.from(list).filter((f) => f && f.size >= 0)
  }

  function onMainDragEnter(e: DragEvent) {
    if (activeTab.value !== 'all') return
    if (!e.dataTransfer?.types?.includes('Files')) return
    dragEnterCount++
    dragOverMain.value = true
  }

  function onMainDragOver(e: DragEvent) {
    if (activeTab.value !== 'all') return
    if (e.dataTransfer) e.dataTransfer.dropEffect = 'copy'
  }

  function onMainDragLeave() {
    dragEnterCount = Math.max(0, dragEnterCount - 1)
    if (dragEnterCount === 0) dragOverMain.value = false
  }

  function onMainDrop(e: DragEvent) {
    dragEnterCount = 0
    dragOverMain.value = false
    if (activeTab.value !== 'all') return
    const files = collectDroppedFiles(e)
    if (!files.length) return
    openUploadWithFiles(files, selectedFolderId.value)
  }

  function onDropFilesToFolder(payload: { folderId: number; files: File[] }) {
    openUploadWithFiles(payload.files, payload.folderId)
  }

  function onFileRowDragStart(row: SysFile, e: DragEvent) {
    if (!row.fileId || !e.dataTransfer) return
    const ids = selectedRows.value.some((r) => r.fileId === row.fileId)
      ? selectedRows.value.map((r) => r.fileId!).filter(Boolean)
      : [row.fileId]
    e.dataTransfer.setData('application/x-star-file-ids', JSON.stringify(ids))
    e.dataTransfer.effectAllowed = 'move'
  }

  async function onDropMoveToFolder(payload: { folderId: number; fileIds: number[] }) {
    if (!hasAuth('file:resource:move')) {
      ElMessage.warning('无迁移权限')
      return
    }
    if (!payload.fileIds.length) return
    try {
      await moveFiles(payload.fileIds, payload.folderId)
      ElMessage.success('已迁移到目标文件夹')
      selectedRows.value = []
      refreshData()
      loadFolderTree()
    } catch (error) {
      handleMutationError(error, '迁移失败')
    }
  }

  onMounted(async () => {
    await initPage()
    pageInitialized.value = true
  })

  onActivated(() => {
    if (!pageInitialized.value) return
    if (activeTab.value === 'all') {
      handleSearch()
    }
  })
</script>

<style lang="scss" scoped>
  .file-page {
    min-height: 0;
  }

  .file-layout {
    display: flex;
    gap: 12px;
    height: 100%;
    min-height: 0;
  }

  .file-sidebar {
    flex-shrink: 0;
    width: 272px;

    :deep(.el-card__body) {
      height: calc(100vh - 148px);
      min-height: 420px;
      padding: 14px 12px;
    }
  }

  .file-main {
    position: relative;
    display: flex;
    flex: 1;
    flex-direction: column;
    gap: 12px;
    min-width: 0;
    min-height: 0;

    &.is-dragover .drop-mask {
      opacity: 1;
      pointer-events: none;
    }
  }

  .drop-mask {
    position: absolute;
    inset: 0;
    z-index: 20;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 600;
    color: var(--el-color-primary);
    pointer-events: none;
    background: color-mix(in srgb, var(--el-color-primary) 12%, transparent);
    border: 2px dashed var(--el-color-primary);
    border-radius: 8px;
    opacity: 0;
    transition: opacity 0.15s ease;
  }

  .file-table-card {
    flex: 1;
    min-height: 0;

    :deep(.el-card__body) {
      display: flex;
      flex-direction: column;
      min-height: 0;
    }
  }

  .file-table-card__top {
    flex-shrink: 0;
  }

  .file-table-card__body {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-height: 0;
  }

  .panel-toolbar {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 16px;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 4px;
  }

  .panel-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 0;
    }

    :deep(.el-tabs__nav-wrap::after) {
      display: none;
    }
  }

  .location-bar {
    display: inline-flex;
    gap: 6px;
    align-items: center;
    padding: 4px 12px;
    font-size: 13px;
    color: var(--el-text-color-regular);
    background: var(--el-fill-color-light);
    border-radius: 6px;
  }

  .location-icon {
    font-size: 16px;
    color: var(--el-color-primary);
  }

  .location-text {
    max-width: 360px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .media-filter {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 12px;
    margin-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .view-pagination {
    display: flex;
    justify-content: flex-end;
    padding: 8px 0 4px;
  }

  .filter-icon {
    margin-right: 2px;
    font-size: 13px;
    vertical-align: -2px;
  }

  :deep(.file-name-cell) {
    display: inline-flex;
    gap: 10px;
    align-items: center;
    max-width: 100%;
    cursor: default;
  }

  :deep(.file-thumb) {
    flex-shrink: 0;
    width: 44px;
    height: 44px;
    cursor: zoom-in;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
  }

  :deep(.file-thumb .el-image__inner) {
    border-radius: 4px;
  }

  :deep(.file-type-icon) {
    flex-shrink: 0;
    font-size: 18px;
    color: var(--el-color-primary);
  }

  :deep(.file-name-main) {
    display: flex;
    flex: 1;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
  }

  :deep(.file-tag-row) {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }

  :deep(.file-name-text) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &.is-link {
      color: var(--el-color-primary);
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  :deep(.file-op-cell) {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
  }
</style>
