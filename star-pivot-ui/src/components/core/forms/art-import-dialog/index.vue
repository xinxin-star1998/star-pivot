<!--
  通用导入弹窗组件
  提供 Excel 上传、模板下载、可选「覆盖已存在数据」等，对接通用导入导出 API
  使用：<ArtImportDialog v-model="visible" business-type="user" title="用户导入" :show-overwrite="true" @success="refreshData" />
-->
<template>
  <ElDialog
    :model-value="modelValue"
    :title="title"
    width="520px"
    destroy-on-close
    @update:model-value="handleClose"
  >
    <div class="art-import-dialog-content">
      <ElUpload
        drag
        :auto-upload="false"
        accept=".xls,.xlsx"
        :file-list="fileList"
        :limit="1"
        :on-change="handleUploadChange"
        :on-remove="handleUploadRemove"
      >
        <ElIcon class="art-import-upload-icon">
          <UploadFilled />
        </ElIcon>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
      </ElUpload>

      <div class="art-import-bottom">
        <ElCheckbox v-if="showOverwrite" v-model="overwrite"> {{ overwriteLabel }} </ElCheckbox>
        <slot name="extra-options" />
        <p class="art-import-desc">
          仅允许导入 xls、xlsx 格式文件。
          <ElButton type="primary" link @click="handleDownloadTemplate"> 下载模板 </ElButton>
        </p>
      </div>
    </div>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="loading" @click="handleConfirm">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import * as XLSX from 'xlsx'
  import { ElMessage } from 'element-plus'
  import { UploadFilled } from '@element-plus/icons-vue'
  import { fetchImportData, fetchDownloadTemplate } from '@/api/common/import-export'
  import type { UploadFile } from 'element-plus'

  defineOptions({ name: 'ArtImportDialog' })

  const props = withDefaults(
    defineProps<{
      /** 弹窗显隐，v-model */
      modelValue?: boolean
      /** 业务类型标识，如 user、dept、role */
      businessType: string
      /** 弹窗标题 */
      title?: string
      /** 是否显示「覆盖已存在数据」选项 */
      showOverwrite?: boolean
      /** 覆盖选项的文案 */
      overwriteLabel?: string
      /** 导入成功后是否自动关闭弹窗 */
      closeOnSuccess?: boolean
    }>(),
    {
      modelValue: false,
      title: '数据导入',
      showOverwrite: false,
      overwriteLabel: '是否更新已经存在的数据',
      closeOnSuccess: true
    }
  )

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    /** 导入成功，可在此回调中刷新列表 */
    success: [result: { successCount: number; failCount: number; errorMessages?: string[] }]
    /** 导入失败（接口或解析报错） */
    error: [err: unknown]
  }>()

  const fileList = ref<UploadFile[]>([])
  const overwrite = ref(false)
  const loading = ref(false)

  /** 关闭弹窗并重置状态 */
  function handleClose() {
    emit('update:modelValue', false)
    fileList.value = []
    overwrite.value = false
    loading.value = false
  }

  function handleUploadChange(_file: UploadFile, list: UploadFile[]) {
    fileList.value = list.slice(-1)
  }

  function handleUploadRemove(_file: UploadFile, list: UploadFile[]) {
    fileList.value = list
  }

  /** 解析 Excel 为行数据 */
  function parseExcelFile(file: File): Promise<Array<Record<string, unknown>>> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = e.target?.result
          const workbook = XLSX.read(data, { type: 'array' })
          const firstSheetName = workbook.SheetNames[0]
          const worksheet = workbook.Sheets[firstSheetName]
          const rows = XLSX.utils.sheet_to_json(worksheet) as Array<Record<string, unknown>>
          resolve(rows)
        } catch (err) {
          reject(err)
        }
      }
      reader.onerror = () => reject(reader.error)
      reader.readAsArrayBuffer(file)
    })
  }

  /** 下载模板 */
  async function handleDownloadTemplate() {
    try {
      await fetchDownloadTemplate(props.businessType)
    } catch (err) {
      console.error('下载模板失败:', err)
      emit('error', err)
    }
  }

  /** 确认导入 */
  async function handleConfirm() {
    if (!fileList.value.length || !fileList.value[0].raw) {
      ElMessage.warning('请先选择要导入的 Excel 文件')
      return
    }

    const file = fileList.value[0].raw
    let rows: Array<Record<string, unknown>>

    try {
      rows = await parseExcelFile(file)
    } catch (err) {
      console.error('解析 Excel 失败:', err)
      ElMessage.error('Excel 解析失败，请检查文件格式')
      emit('error', err)
      return
    }

    if (!rows?.length) {
      ElMessage.warning('Excel 文件中没有可导入的数据')
      return
    }

    loading.value = true
    try {
      const options: Record<string, unknown> = {}
      if (props.showOverwrite) {
        options.overwrite = overwrite.value
      }
      const result = await fetchImportData(props.businessType, rows, options)

      if (result.failCount > 0) {
        const errMsg = result.errorMessages?.length
          ? result.errorMessages.slice(0, 5).join('；') +
            (result.errorMessages.length > 5 ? '...' : '')
          : ''
        ElMessage.warning(
          `导入完成：成功 ${result.successCount} 条，失败 ${result.failCount} 条${errMsg ? '。' + errMsg : ''}`
        )
      } else {
        ElMessage.success(`导入成功：共导入 ${result.successCount} 条数据`)
      }

      emit('success', result)
      if (props.closeOnSuccess) {
        handleClose()
      } else {
        fileList.value = []
      }
    } catch (err) {
      console.error('导入失败:', err)
      ElMessage.error('导入失败，请检查 Excel 数据格式或联系管理员')
      emit('error', err)
    } finally {
      loading.value = false
    }
  }
</script>

<style lang="scss" scoped>
  .art-import-dialog-content {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .art-import-bottom {
    margin-top: 16px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }

  .art-import-desc {
    margin: 0;
    color: #909399;
    font-size: 12px;
  }
</style>
