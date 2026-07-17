<template>
  <div class="i18n-page art-full-height">
    <ElTabs v-model="activeTab" class="i18n-tabs">
      <ElTabPane :label="t('system.i18n.tabLang')" name="lang">
        <ElCard class="art-table-card" shadow="never">
          <ArtTableHeader
            v-model:columns="columnChecks"
            :loading="loading"
            layout="refresh"
            @refresh="loadData"
          >
            <template #left>
              <ElSpace wrap>
                <ElButton
                  v-auth="'system:i18n:edit'"
                  v-ripple
                  type="primary"
                  @click="openDialog('add')"
                >
                  {{ t('system.i18n.addLang') }}
                </ElButton>
              </ElSpace>
            </template>
          </ArtTableHeader>

          <ElTable v-loading="loading" :data="tableData" border stripe>
            <ElTableColumn prop="langCode" :label="t('system.i18n.langCode')" min-width="100" />
            <ElTableColumn prop="langName" :label="t('system.i18n.langName')" min-width="140" />
            <ElTableColumn :label="t('system.i18n.isDefault')" width="110" align="center">
              <template #default="{ row }">
                <ElTag :type="row.isDefault === '1' ? 'success' : 'info'" size="small">
                  {{ row.isDefault === '1' ? t('system.i18n.yes') : t('system.i18n.no') }}
                </ElTag>
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="orderNum"
              :label="t('system.i18n.orderNum')"
              width="90"
              align="center"
            />
            <ElTableColumn :label="t('system.i18n.status')" width="110" align="center">
              <template #default="{ row }">
                <ElSwitch
                  :model-value="row.status === '0'"
                  :disabled="!hasAuth('system:i18n:edit') || row.isDefault === '1'"
                  inline-prompt
                  :active-text="t('system.i18n.enabled')"
                  :inactive-text="t('system.i18n.disabled')"
                  @change="
                    (val: string | number | boolean) => handleStatusChange(row, val === true)
                  "
                />
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="t('system.i18n.operation')"
              width="120"
              fixed="right"
              align="center"
            >
              <template #default="{ row }">
                <ArtButtonTable
                  v-if="hasAuth('system:i18n:edit')"
                  type="edit"
                  @click="openDialog('edit', row)"
                />
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>
      </ElTabPane>

      <ElTabPane :label="t('system.i18n.tabUi')" name="ui">
        <ElCard class="art-table-card" shadow="never">
          <div class="ui-toolbar">
            <ElSelect v-model="uiNamespace" style="width: 140px" @change="loadUiBundle">
              <ElOption value="ui" :label="t('system.i18n.nsUi')" />
              <ElOption value="menu" :label="t('system.i18n.nsMenu')" />
            </ElSelect>
            <ElSelect v-model="uiLang" style="width: 160px" @change="loadUiBundle">
              <ElOption
                v-for="lang in enabledLangs"
                :key="lang.langCode"
                :label="lang.langName"
                :value="lang.langCode"
              />
            </ElSelect>
            <ElInput
              v-model="uiKeyword"
              clearable
              :placeholder="
                uiNamespace === 'menu' ? t('system.i18n.searchMenu') : t('system.i18n.searchKey')
              "
              style="width: 260px"
              @keyup.enter="filterUiRows"
              @clear="filterUiRows"
            />
            <ElButton type="primary" :loading="uiLoading" @click="loadUiBundle">
              {{ t('system.i18n.refresh') }}
            </ElButton>
          </div>

          <ElTable v-loading="uiLoading" :data="uiPagedRows" border stripe max-height="560">
            <ElTableColumn
              prop="resourceKey"
              :label="uiNamespace === 'menu' ? t('system.i18n.menuId') : t('system.i18n.key')"
              min-width="140"
              show-overflow-tooltip
            />
            <ElTableColumn
              prop="content"
              :label="t('system.i18n.content')"
              min-width="220"
              show-overflow-tooltip
            />
            <ElTableColumn
              :label="t('system.i18n.operation')"
              width="100"
              fixed="right"
              align="center"
            >
              <template #default="{ row }">
                <ArtButtonTable
                  v-if="hasAuth('system:i18n:edit')"
                  type="edit"
                  @click="openUiEdit(row.resourceKey)"
                />
              </template>
            </ElTableColumn>
          </ElTable>

          <div class="ui-pagination">
            <ElPagination
              v-model:current-page="uiPage"
              v-model:page-size="uiPageSize"
              background
              layout="total, prev, pager, next"
              :total="uiFilteredRows.length"
            />
          </div>
        </ElCard>
      </ElTabPane>

      <ElTabPane :label="t('system.i18n.tabCoverage')" name="coverage">
        <ElCard class="art-table-card" shadow="never">
          <div class="ui-toolbar">
            <ElSelect v-model="covNamespace" style="width: 140px">
              <ElOption value="ui" :label="t('system.i18n.nsUi')" />
              <ElOption value="menu" :label="t('system.i18n.nsMenu')" />
              <ElOption value="dict_data" :label="t('system.i18n.nsDict')" />
            </ElSelect>
            <ElSelect v-model="covLang" style="width: 160px">
              <ElOption
                v-for="lang in enabledLangs"
                :key="lang.langCode"
                :label="lang.langName"
                :value="lang.langCode"
              />
            </ElSelect>
            <ElButton type="primary" :loading="covLoading" @click="loadCoverage">
              {{ t('system.i18n.checkCoverage') }}
            </ElButton>
            <ElButton :loading="exportLoading" @click="handleExport">
              {{ t('system.i18n.export') }}
            </ElButton>
            <ElButton v-auth="'system:i18n:edit'" @click="importVisible = true">
              {{ t('system.i18n.import') }}
            </ElButton>
          </div>

          <div v-if="coverage" class="cov-stats">
            <ElTag type="info">{{ t('system.i18n.total') }}: {{ coverage.total }}</ElTag>
            <ElTag type="success"
              >{{ t('system.i18n.translated') }}: {{ coverage.translated }}</ElTag
            >
            <ElTag type="danger">{{ t('system.i18n.missing') }}: {{ coverage.missing }}</ElTag>
            <ElTag type="warning">
              {{ t('system.i18n.coverageRate') }}: {{ coverage.coverageRate }}%
            </ElTag>
          </div>

          <ElTable
            v-loading="covLoading"
            :data="coverage?.missingItems || []"
            border
            stripe
            max-height="520"
          >
            <ElTableColumn
              prop="resourceKey"
              :label="t('system.i18n.key')"
              min-width="220"
              show-overflow-tooltip
            />
            <ElTableColumn
              prop="baseContent"
              :label="t('system.i18n.baseContent')"
              min-width="240"
              show-overflow-tooltip
            />
            <ElTableColumn
              :label="t('system.i18n.operation')"
              width="100"
              fixed="right"
              align="center"
            >
              <template #default="{ row }">
                <ArtButtonTable
                  v-if="hasAuth('system:i18n:edit') && covNamespace === 'ui'"
                  type="edit"
                  @click="openUiEdit(row.resourceKey)"
                />
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>
      </ElTabPane>
    </ElTabs>

    <ElDialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? t('system.i18n.addLang') : t('system.i18n.editLang')"
      width="480px"
      align-center
      destroy-on-close
      @closed="resetForm"
    >
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="96px">
        <ElFormItem :label="t('system.i18n.langCode')" prop="langCode">
          <ElInput
            v-model="form.langCode"
            :placeholder="t('system.i18n.langCodePlaceholder')"
            :disabled="dialogType === 'edit'"
            maxlength="16"
          />
        </ElFormItem>
        <ElFormItem :label="t('system.i18n.langName')" prop="langName">
          <ElInput
            v-model="form.langName"
            :placeholder="t('system.i18n.langNamePlaceholder')"
            maxlength="50"
          />
        </ElFormItem>
        <ElFormItem :label="t('system.i18n.displayOrder')" prop="orderNum">
          <ElInputNumber
            v-model="form.orderNum"
            :min="0"
            controls-position="right"
            class="w-full"
          />
        </ElFormItem>
        <ElFormItem :label="t('system.i18n.isDefault')" prop="isDefault">
          <ElSwitch
            v-model="form.isDefault"
            active-value="1"
            inactive-value="0"
            inline-prompt
            :active-text="t('system.i18n.yes')"
            :inactive-text="t('system.i18n.no')"
          />
        </ElFormItem>
        <ElFormItem :label="t('system.i18n.status')" prop="status">
          <ElSwitch
            v-model="form.status"
            active-value="0"
            inactive-value="1"
            :disabled="form.isDefault === '1'"
            inline-prompt
            :active-text="t('system.i18n.normal')"
            :inactive-text="t('system.i18n.stopped')"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">{{ t('system.i18n.cancel') }}</ElButton>
        <ElButton type="primary" :loading="submitting" @click="handleSubmit">
          {{ t('system.i18n.confirm') }}
        </ElButton>
      </template>
    </ElDialog>

    <ElDialog
      v-model="uiEditVisible"
      :title="uiNamespace === 'menu' ? t('system.i18n.editMenu') : t('system.i18n.editUi')"
      width="560px"
      align-center
      destroy-on-close
    >
      <ElForm label-width="96px">
        <ElFormItem
          :label="uiNamespace === 'menu' ? t('system.i18n.menuId') : t('system.i18n.key')"
        >
          <ElInput :model-value="uiEditKey" disabled />
        </ElFormItem>
        <ElFormItem v-for="lang in enabledLangs" :key="lang.langCode" :label="lang.langName">
          <ElInput
            v-model="uiEditTranslations[lang.langCode]"
            type="textarea"
            :rows="2"
            maxlength="2000"
            show-word-limit
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="uiEditVisible = false">{{ t('system.i18n.cancel') }}</ElButton>
        <ElButton type="primary" :loading="uiSubmitting" @click="saveUiEdit">
          {{ t('system.i18n.save') }}
        </ElButton>
      </template>
    </ElDialog>

    <ElDialog
      v-model="importVisible"
      :title="t('system.i18n.import')"
      width="640px"
      align-center
      destroy-on-close
    >
      <p class="import-tip">{{ t('system.i18n.importTip') }}</p>
      <ElInput
        v-model="importJson"
        type="textarea"
        :rows="12"
        placeholder='{"login.title":"Login"}'
      />
      <template #footer>
        <ElButton @click="importVisible = false">{{ t('system.i18n.cancel') }}</ElButton>
        <ElButton type="primary" :loading="importLoading" @click="handleImport">
          {{ t('system.i18n.confirm') }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { handleMutationError } from '@/utils/http/mutation'
  import { loadLanguageOptions, loadRemoteUiMessages } from '@/locales'
  import {
    downloadI18nExport,
    fetchAddI18nLang,
    fetchI18nBundle,
    fetchI18nCoverage,
    fetchI18nLangAll,
    fetchI18nLangList,
    fetchI18nResource,
    fetchImportI18n,
    fetchSaveI18nResource,
    fetchUpdateI18nLang,
    fetchUpdateI18nLangStatus,
    type I18nCoverage,
    type SysLang,
    type SysLangForm
  } from '@/api/system/i18n'

  defineOptions({ name: 'I18nManage' })

  const { t } = useI18n()
  const { hasAuth } = useAuth()

  const activeTab = ref('lang')
  const loading = ref(false)
  const submitting = ref(false)
  const tableData = ref<SysLang[]>([])
  const columnChecks = ref([])
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>()

  const form = reactive<SysLangForm>({
    langCode: '',
    langName: '',
    isDefault: '0',
    status: '0',
    orderNum: 0
  })

  const rules = computed<FormRules>(() => ({
    langCode: [
      { required: true, message: t('system.i18n.langCodePlaceholder'), trigger: 'blur' },
      { max: 16, message: 'max 16', trigger: 'blur' }
    ],
    langName: [
      { required: true, message: t('system.i18n.langNamePlaceholder'), trigger: 'blur' },
      { max: 50, message: 'max 50', trigger: 'blur' }
    ]
  }))

  const enabledLangs = ref<SysLang[]>([])
  const uiNamespace = ref<'ui' | 'menu'>('ui')
  const uiLang = ref('zh')
  const uiKeyword = ref('')
  const uiLoading = ref(false)
  const uiSubmitting = ref(false)
  const uiRows = ref<{ resourceKey: string; content: string }[]>([])
  const uiFilteredRows = ref<{ resourceKey: string; content: string }[]>([])
  const uiPage = ref(1)
  const uiPageSize = ref(20)
  const uiEditVisible = ref(false)
  const uiEditKey = ref('')
  const uiEditTranslations = reactive<Record<string, string>>({})

  const uiFieldName = computed(() => (uiNamespace.value === 'menu' ? 'menu_name' : '_'))

  const covNamespace = ref('ui')
  const covLang = ref('en')
  const covLoading = ref(false)
  const coverage = ref<I18nCoverage | null>(null)
  const exportLoading = ref(false)
  const importVisible = ref(false)
  const importLoading = ref(false)
  const importJson = ref('')

  const uiPagedRows = computed(() => {
    const start = (uiPage.value - 1) * uiPageSize.value
    return uiFilteredRows.value.slice(start, start + uiPageSize.value)
  })

  const loadData = async () => {
    loading.value = true
    try {
      const list = await fetchI18nLangAll()
      tableData.value = Array.isArray(list) ? list : []
    } catch (error) {
      handleMutationError(error, t('system.i18n.loadLangFail'))
    } finally {
      loading.value = false
    }
  }

  const loadEnabledLangs = async () => {
    try {
      const list = await fetchI18nLangList()
      enabledLangs.value = Array.isArray(list) ? list : []
      const def = enabledLangs.value.find((l) => l.isDefault === '1')
      uiLang.value = def?.langCode || enabledLangs.value[0]?.langCode || 'zh'
      const nonDefault = enabledLangs.value.find((l) => l.isDefault !== '1')
      covLang.value = nonDefault?.langCode || enabledLangs.value[0]?.langCode || 'en'
    } catch {
      enabledLangs.value = [
        { langCode: 'zh', langName: '简体中文', isDefault: '1', status: '0' },
        { langCode: 'en', langName: 'English', isDefault: '0', status: '0' }
      ]
      uiLang.value = 'zh'
      covLang.value = 'en'
    }
  }

  const filterUiRows = () => {
    const kw = uiKeyword.value.trim().toLowerCase()
    uiFilteredRows.value = !kw
      ? [...uiRows.value]
      : uiRows.value.filter(
          (row) =>
            row.resourceKey.toLowerCase().includes(kw) || row.content.toLowerCase().includes(kw)
        )
    uiPage.value = 1
  }

  const loadUiBundle = async () => {
    uiLoading.value = true
    try {
      const map = (await fetchI18nBundle(uiNamespace.value, uiLang.value)) || {}
      uiRows.value = Object.entries(map)
        .map(([resourceKey, content]) => ({ resourceKey, content }))
        .sort((a, b) => {
          if (uiNamespace.value === 'menu') {
            return Number(a.resourceKey) - Number(b.resourceKey)
          }
          return a.resourceKey.localeCompare(b.resourceKey)
        })
      filterUiRows()
    } catch (error) {
      handleMutationError(error, t('system.i18n.loadUiFail'))
    } finally {
      uiLoading.value = false
    }
  }

  const loadCoverage = async () => {
    covLoading.value = true
    try {
      coverage.value = await fetchI18nCoverage({
        namespace: covNamespace.value,
        lang: covLang.value
      })
    } catch (error) {
      handleMutationError(error, t('system.i18n.coverageFail'))
    } finally {
      covLoading.value = false
    }
  }

  const handleExport = async () => {
    exportLoading.value = true
    try {
      await downloadI18nExport(covNamespace.value, covLang.value)
    } catch (error) {
      handleMutationError(error, t('system.i18n.exportFail'))
    } finally {
      exportLoading.value = false
    }
  }

  const handleImport = async () => {
    let bundle: Record<string, string>
    try {
      bundle = JSON.parse(importJson.value || '{}')
      if (!bundle || typeof bundle !== 'object' || Array.isArray(bundle)) {
        throw new Error('invalid')
      }
    } catch {
      ElMessage.error(t('system.i18n.importFail'))
      return
    }
    importLoading.value = true
    try {
      await fetchImportI18n({
        namespace: covNamespace.value,
        lang: covLang.value,
        overwrite: true,
        bundle
      })
      ElMessage.success(t('system.i18n.importSuccess'))
      importVisible.value = false
      importJson.value = ''
      await loadCoverage()
      if (covNamespace.value === 'ui' || covNamespace.value === 'menu') {
        if (covNamespace.value === 'ui') {
          await loadRemoteUiMessages(covLang.value)
        }
        if (activeTab.value === 'ui' && uiNamespace.value === covNamespace.value) {
          await loadUiBundle()
        }
      }
    } catch (error) {
      handleMutationError(error, t('system.i18n.importFail'))
    } finally {
      importLoading.value = false
    }
  }

  const openUiEdit = async (resourceKey: string) => {
    uiEditKey.value = resourceKey
    Object.keys(uiEditTranslations).forEach((k) => delete uiEditTranslations[k])
    enabledLangs.value.forEach((lang) => {
      uiEditTranslations[lang.langCode] = ''
    })
    try {
      const map = await fetchI18nResource({
        namespace: uiNamespace.value,
        resourceKey,
        fieldName: uiFieldName.value
      })
      Object.assign(uiEditTranslations, map || {})
    } catch {
      const current = uiRows.value.find((r) => r.resourceKey === resourceKey)
      if (current) {
        uiEditTranslations[uiLang.value] = current.content
      }
    }
    uiEditVisible.value = true
  }

  const saveUiEdit = async () => {
    uiSubmitting.value = true
    try {
      await fetchSaveI18nResource({
        namespace: uiNamespace.value,
        resourceKey: uiEditKey.value,
        fieldName: uiFieldName.value,
        translations: { ...uiEditTranslations }
      })
      ElMessage.success(t('system.i18n.saveSuccess'))
      uiEditVisible.value = false
      await loadUiBundle()
      if (uiNamespace.value === 'ui') {
        await loadRemoteUiMessages(uiLang.value)
      }
      if (activeTab.value === 'coverage') {
        await loadCoverage()
      }
    } catch (error) {
      handleMutationError(error, t('system.i18n.saveUiFail'))
    } finally {
      uiSubmitting.value = false
    }
  }

  const resetForm = () => {
    Object.assign(form, {
      langId: undefined,
      langCode: '',
      langName: '',
      isDefault: '0',
      status: '0',
      orderNum: 0
    })
    formRef.value?.clearValidate()
  }

  const openDialog = (type: 'add' | 'edit', row?: SysLang) => {
    dialogType.value = type
    resetForm()
    if (type === 'edit' && row) {
      Object.assign(form, {
        langId: row.langId,
        langCode: row.langCode,
        langName: row.langName,
        isDefault: row.isDefault ?? '0',
        status: row.status ?? '0',
        orderNum: row.orderNum ?? 0
      })
    }
    dialogVisible.value = true
  }

  const handleSubmit = async () => {
    if (!formRef.value) return
    try {
      await formRef.value.validate()
      submitting.value = true
      const payload: SysLangForm = {
        langId: form.langId,
        langCode: form.langCode.trim(),
        langName: form.langName.trim(),
        isDefault: form.isDefault ?? '0',
        status: form.isDefault === '1' ? '0' : (form.status ?? '0'),
        orderNum: form.orderNum ?? 0
      }
      if (dialogType.value === 'add') {
        await fetchAddI18nLang(payload)
        ElMessage.success(t('system.i18n.addSuccess'))
      } else {
        await fetchUpdateI18nLang(payload)
        ElMessage.success(t('system.i18n.editSuccess'))
      }
      dialogVisible.value = false
      await loadData()
      await loadEnabledLangs()
      await loadLanguageOptions()
    } catch (error) {
      if (error !== false) {
        handleMutationError(
          error,
          dialogType.value === 'add' ? t('system.i18n.addFail') : t('system.i18n.editFail')
        )
      }
    } finally {
      submitting.value = false
    }
  }

  const handleStatusChange = async (row: SysLang, enabled: boolean) => {
    if (!row.langId) return
    const status = enabled ? '0' : '1'
    try {
      await fetchUpdateI18nLangStatus(row.langId, status)
      ElMessage.success(enabled ? t('system.i18n.enabledMsg') : t('system.i18n.disabledMsg'))
      await loadData()
      await loadEnabledLangs()
      await loadLanguageOptions()
    } catch (error) {
      handleMutationError(error, t('system.i18n.statusFail'))
    }
  }

  watch(activeTab, (tab) => {
    if (tab === 'ui' && uiRows.value.length === 0) {
      void loadUiBundle()
    }
    if (tab === 'coverage' && !coverage.value) {
      void loadCoverage()
    }
  })

  watch(uiKeyword, () => {
    filterUiRows()
  })

  onMounted(async () => {
    await loadData()
    await loadEnabledLangs()
  })
</script>

<style scoped lang="scss">
  .i18n-page {
    :deep(.w-full) {
      width: 100%;
    }
  }

  .i18n-tabs {
    height: 100%;
  }

  .ui-toolbar {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
    margin-bottom: 12px;
  }

  .ui-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }

  .cov-stats {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 12px;
  }

  .import-tip {
    margin: 0 0 12px;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
</style>
