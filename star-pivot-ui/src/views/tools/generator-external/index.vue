<template>
  <div class="gen-external-page art-full-height">
    <ElCard shadow="never" class="step-card">
      <template #header>
        <div class="card-header">
          <span class="title">{{ t('tools.genExt.pageTitle') }}</span>
          <ElSpace>
            <ElTag v-if="sessionId" type="success" effect="plain">
              {{ t('tools.genExt.connected') }} · {{ dbInfo }}
              <span v-if="sessionRemainingText" class="session-ttl">
                · {{ sessionRemainingText }}</span
              >
            </ElTag>
            <ExternalActionBtn
              v-if="sessionId"
              :what="t('tools.genExt.disconnectWhat')"
              :usage="t('tools.genExt.disconnectUsage')"
              link
              type="danger"
              @click="handleDisconnect"
            >
              {{ t('tools.genExt.disconnect') }}
            </ExternalActionBtn>
          </ElSpace>
        </div>
      </template>

      <ElSteps :active="currentStep" finish-status="success" align-center class="mb-6">
        <ElStep :title="t('tools.genExt.stepConnect')" />
        <ElStep :title="t('tools.genExt.stepSelectTable')" />
        <ElStep :title="t('tools.genExt.stepTemplate')" />
        <ElStep :title="t('tools.genExt.pathConfig')" />
        <ElStep :title="t('tools.genExt.stepPreviewDownload')" />
      </ElSteps>

      <!-- Step 0: 连接 -->
      <div v-show="currentStep === 0" class="step-panel">
        <ElForm label-width="100px" class="conn-preset-bar mb-4">
          <ElRow :gutter="16">
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.connPreset')">
                <ElSelect
                  v-model="selectedConnPreset"
                  :placeholder="t('tools.genExt.myConnPreset')"
                  clearable
                  class="w-full"
                  @change="applyConnPreset"
                >
                  <ElOption
                    v-for="p in connPresets"
                    :key="p.name"
                    :label="p.name"
                    :value="p.name"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="10">
              <ElFormItem :label="t('tools.genExt.savePreset')">
                <ElInput
                  v-model="newConnPresetName"
                  :placeholder="t('tools.genExt.presetName')"
                  maxlength="32"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="6">
              <ElFormItem label-width="0">
                <ElCheckbox v-model="saveConnPassword">{{
                  t('tools.genExt.rememberPassword')
                }}</ElCheckbox>
                <ExternalActionBtn
                  :what="t('tools.genExt.saveConnWhat')"
                  :usage="t('tools.genExt.saveConnUsage')"
                  link
                  type="primary"
                  class="ml-2"
                  @click="saveConnPreset"
                >
                  {{ t('common.save') }}
                </ExternalActionBtn>
                <ExternalActionBtn
                  v-if="selectedConnPreset"
                  :what="t('tools.genExt.deleteConnPresetWhat')"
                  :usage="t('tools.genExt.deleteConnPresetUsage')"
                  link
                  type="danger"
                  @click="deleteConnPreset"
                >
                  {{ t('common.delete') }}
                </ExternalActionBtn>
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElForm>
        <ElForm
          ref="connFormRef"
          :model="connection"
          :rules="connRules"
          label-width="100px"
          class="conn-form"
        >
          <ElRow :gutter="16">
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.dbType')">
                <ElSelect v-model="connection.dbType" class="w-full" @change="onDbTypeChange">
                  <ElOption label="MySQL" value="mysql" />
                  <ElOption label="PostgreSQL" value="postgresql" />
                  <ElOption label="Oracle" value="oracle" />
                  <ElOption label="SQL Server" value="sqlserver" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.host')" prop="host">
                <ElInput v-model="connection.host" placeholder="127.0.0.1" />
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.port')" prop="port">
                <ElInputNumber v-model="connection.port" :min="1" :max="65535" class="w-full" />
              </ElFormItem>
            </ElCol>
            <ElCol v-if="connection.dbType === 'oracle'" :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.connectMode')">
                <ElSelect v-model="connection.oracleConnectMode" class="w-full">
                  <ElOption :label="t('tools.genExt.oracleService')" value="service" />
                  <ElOption label="SID" value="sid" />
                  <ElOption :label="t('tools.genExt.oracleTns')" value="tns" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="oracleDatabaseLabel" prop="database">
                <ElInput v-model="connection.database" :placeholder="oracleDatabasePlaceholder" />
              </ElFormItem>
            </ElCol>
            <ElCol
              v-if="['postgresql', 'oracle', 'sqlserver'].includes(connection.dbType || 'mysql')"
              :xs="24"
              :sm="12"
              :md="8"
            >
              <ElFormItem label="Schema">
                <ElInput v-model="connection.schema" :placeholder="schemaPlaceholder" />
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.username')" prop="username">
                <ElInput v-model="connection.username" />
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12" :md="8">
              <ElFormItem :label="t('tools.genExt.password')" prop="password">
                <ElInput v-model="connection.password" type="password" show-password />
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :md="16">
              <ElFormItem :label="t('tools.genExt.jdbcParams')">
                <ElInput v-model="connection.params" :placeholder="jdbcParamsPlaceholder" />
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElForm>
      </div>

      <!-- Step 1: 选表 -->
      <div v-show="currentStep === 1" class="step-panel">
        <ElForm :inline="true" :model="tableSearch" class="mb-3">
          <ElFormItem :label="t('tools.gen.tableName')">
            <ElInput
              v-model="tableSearch.tableName"
              clearable
              :placeholder="t('tools.genExt.fuzzySearch')"
            />
          </ElFormItem>
          <ElFormItem :label="t('tools.gen.tableComment')">
            <ElInput
              v-model="tableSearch.tableComment"
              clearable
              :placeholder="t('tools.genExt.fuzzySearch')"
            />
          </ElFormItem>
          <ElFormItem>
            <ExternalActionBtn
              :what="t('tools.genExt.searchTableWhat')"
              :usage="t('tools.genExt.searchTableUsage')"
              type="primary"
              :loading="tableLoading"
              @click="loadTables"
            >
              {{ t('table.searchBar.search') }}
            </ExternalActionBtn>
            <ExternalActionBtn
              :what="t('tools.genExt.resetSearchWhat')"
              :usage="t('tools.genExt.resetSearchUsage')"
              @click="resetTableSearch"
            >
              {{ t('table.searchBar.reset') }}
            </ExternalActionBtn>
          </ElFormItem>
        </ElForm>
        <ArtTable
          :loading="tableLoading"
          :data="tableData"
          :columns="tableColumns"
          :pagination="tablePagination"
          @selection-change="onTableSelectionChange"
          @pagination:size-change="onTableSizeChange"
          @pagination:current-change="onTablePageChange"
        />
      </div>

      <!-- Step 2: 模板 -->
      <div v-show="currentStep === 2" class="step-panel">
        <ElForm label-width="120px" class="tpl-form">
          <ElFormItem :label="t('tools.genExt.selectedTables')">
            <ElTag v-for="tName in selectedTableNames" :key="tName" class="mr-2 mb-2">{{
              tName
            }}</ElTag>
          </ElFormItem>
          <ElRow :gutter="16">
            <ElCol :xs="24" :sm="12">
              <ElFormItem :label="t('tools.gen.genType')">
                <ElSelect v-model="genConfig.tplCategory" :placeholder="t('common.pleaseSelect')">
                  <ElOption
                    v-for="item in templateOptions.categories"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :xs="24" :sm="12">
              <ElFormItem :label="t('tools.gen.frontendType')">
                <ElSelect v-model="genConfig.tplWebType" :placeholder="t('common.pleaseSelect')">
                  <ElOption
                    v-for="item in templateOptions.webTypes"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElForm>

        <ElDivider content-position="left">{{ t('tools.genExt.tableExtra') }}</ElDivider>
        <ElTabs v-model="activeTableTab" type="border-card">
          <ElTabPane v-for="name in selectedTableNames" :key="name" :label="name" :name="name">
            <ExternalTableExtraForm
              :meta="tableMeta[name]"
              :tpl-category="genConfig.tplCategory"
              :columns="tableColumnsMap[name] || []"
              :current-table-name="name"
              :selected-table-names="selectedTableNames"
              :parent-menus="parentMenus"
              :all-columns-map="tableColumnsMap"
            />
            <ExternalColumnTable
              :columns="tableColumnsMap[name] || []"
              :dict-options="dictOptions"
              :max-height="420"
              class="mt-3"
            />
          </ElTabPane>
        </ElTabs>
      </div>

      <!-- Step 3: 路径 -->
      <div v-show="currentStep === 3" class="step-panel">
        <PathConfigForm v-model="pathProfile" v-model:author="genConfig.author" />
      </div>

      <!-- Step 4: 预览下载 -->
      <div v-show="currentStep === 4" class="step-panel">
        <ElForm label-width="100px" class="mb-4">
          <ElFormItem :label="t('tools.genExt.genScope')">
            <ElCheckbox v-model="genScope.genBackend">{{
              t('tools.genExt.genBackend')
            }}</ElCheckbox>
            <ElCheckbox v-model="genScope.genFrontend">{{
              t('tools.genExt.genFrontend')
            }}</ElCheckbox>
            <ElCheckbox v-model="genScope.genSql">{{ t('tools.genExt.genSql') }}</ElCheckbox>
          </ElFormItem>
        </ElForm>
        <ElForm :inline="true" class="mb-4">
          <ElFormItem :label="t('tools.genExt.previewTable')">
            <ElSelect
              v-model="previewTableName"
              :placeholder="t('tools.genExt.selectTable')"
              style="width: 240px"
            >
              <ElOption v-for="t in selectedTableNames" :key="t" :label="t" :value="t" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <div class="action-btn-row">
              <ExternalActionBtn
                :what="t('tools.genExt.previewWhat')"
                :usage="t('tools.genExt.previewUsage')"
                type="primary"
                v-auth="'tool:external:preview'"
                @click="openPreview"
              >
                {{ t('tools.genExt.previewCode') }}
              </ExternalActionBtn>
              <ExternalActionBtn
                :what="t('tools.genExt.downloadWhat')"
                :usage="t('tools.genExt.downloadUsage')"
                type="success"
                v-auth="'tool:external:create'"
                :loading="downloading"
                @click="handleDownload"
              >
                {{ t('tools.genExt.downloadZip') }}
              </ExternalActionBtn>
              <ExternalActionBtn
                :what="t('tools.genExt.importWhat')"
                :usage="t('tools.genExt.importUsage')"
                v-auth="'tool:external:create'"
                :loading="importing"
                @click="handleImportGenTable"
              >
                {{ t('tools.genExt.importTable') }}
              </ExternalActionBtn>
            </div>
          </ElFormItem>
        </ElForm>
        <template v-if="writeToDiskEnabled">
          <ElForm :inline="true" class="mb-4">
            <ElFormItem :label="t('tools.genExt.outputRoot')">
              <ElAutocomplete
                v-model="outputRoot"
                :fetch-suggestions="queryOutputRoots"
                :placeholder="t('tools.genExt.outputRootPlaceholder')"
                style="width: 360px"
                clearable
              />
              <ExternalActionBtn
                :what="t('tools.genExt.browseDirWhat')"
                :usage="t('tools.genExt.browseDirUsage')"
                class="ml-2"
                @click="outputRootPickerVisible = true"
              >
                {{ t('tools.genExt.browseDir') }}
              </ExternalActionBtn>
            </ElFormItem>
            <ElFormItem :label="t('tools.genExt.sessionTemplateDir')">
              <ElInput
                v-model="sessionTemplateDir"
                :placeholder="t('tools.genExt.sessionTemplatePlaceholder')"
                style="width: 320px"
                clearable
              />
            </ElFormItem>
            <ElFormItem>
              <ExternalActionBtn
                :what="t('tools.genExt.saveTemplateWhat')"
                :usage="t('tools.genExt.saveTemplateUsage')"
                @click="handleSaveTemplateDir"
              >
                {{ t('tools.genExt.saveTemplate') }}
              </ExternalActionBtn>
            </ElFormItem>
          </ElForm>
          <ElForm :inline="true" class="mb-4">
            <ElFormItem>
              <div class="action-btn-row">
                <ExternalActionBtn
                  :what="t('tools.genExt.writeDiffWhat')"
                  :usage="t('tools.genExt.writeDiffUsage')"
                  type="info"
                  v-auth="'tool:external:preview'"
                  :loading="writeDiffLoading"
                  @click="openWriteDiff"
                >
                  {{ t('tools.genExt.writeDiff') }}
                </ExternalActionBtn>
                <ExternalActionBtn
                  :what="t('tools.genExt.writeCodeWhat')"
                  :usage="t('tools.genExt.writeCodeUsage')"
                  type="warning"
                  v-auth="'tool:external:create'"
                  :loading="writing"
                  @click="handleWriteToDisk"
                >
                  {{ t('tools.genExt.writeCode') }}
                </ExternalActionBtn>
              </div>
            </ElFormItem>
          </ElForm>
          <ElForm :inline="true" class="mb-4">
            <ElFormItem>
              <ElTooltip :content="t('tools.genExt.backupTooltip')" placement="top">
                <ElCheckbox v-model="backupBeforeWrite">{{
                  t('tools.genExt.backupBeforeWrite')
                }}</ElCheckbox>
              </ElTooltip>
            </ElFormItem>
            <ElFormItem>
              <ElTooltip :content="t('tools.genExt.skipUnchangedTooltip')" placement="top">
                <ElCheckbox v-model="writeOnlyChanged">{{
                  t('tools.genExt.skipExisting')
                }}</ElCheckbox>
              </ElTooltip>
            </ElFormItem>
            <ElFormItem v-if="lastWriteBackup">
              <ExternalActionBtn
                :what="t('tools.genExt.rollbackWhat')"
                :usage="t('tools.genExt.rollbackUsage')"
                type="danger"
                :loading="rollingBack"
                v-auth="'tool:external:create'"
                @click="handleRollbackWrite"
              >
                {{ t('tools.genExt.rollbackWrite') }}
              </ExternalActionBtn>
            </ElFormItem>
          </ElForm>
        </template>
        <ElAlert
          v-else
          type="info"
          :closable="false"
          show-icon
          class="mb-4"
          :title="t('tools.genExt.writeDiskDisabled')"
        />
        <ElForm :inline="true" class="mb-4">
          <ElFormItem>
            <ElTooltip :content="t('tools.genExt.overwriteTooltip')" placement="top">
              <ElCheckbox v-model="importOverwrite">{{ t('tools.genExt.overwrite') }}</ElCheckbox>
            </ElTooltip>
          </ElFormItem>
        </ElForm>
        <ElDescriptions :column="2" border size="small">
          <ElDescriptionsItem :label="t('tools.gen.genType')"
            >{{ genConfig.tplCategory }} / {{ genConfig.tplWebType }}</ElDescriptionsItem
          >
          <ElDescriptionsItem :label="t('tools.genExt.effectiveTemplateDir')">{{
            effectiveTemplateDir || t('tools.genExt.builtinTemplate')
          }}</ElDescriptionsItem>
          <ElDescriptionsItem :label="t('tools.gen.packageName')">{{
            pathProfile.basePackage
          }}</ElDescriptionsItem>
          <ElDescriptionsItem :label="t('tools.genExt.apiPath')">{{
            pathProfile.apiPath
          }}</ElDescriptionsItem>
          <ElDescriptionsItem :label="t('tools.genExt.vuePagePath')">{{
            pathProfile.vuePagePath
          }}</ElDescriptionsItem>
        </ElDescriptions>
      </div>

      <div class="step-footer">
        <ElButton v-if="currentStep > 0" @click="prevStep">{{
          t('tools.genExt.prevStep')
        }}</ElButton>
        <ElButton v-if="currentStep < 4" type="primary" :loading="stepLoading" @click="nextStep">
          {{ t('tools.genExt.nextStep') }}
        </ElButton>
      </div>
    </ElCard>

    <ExternalPreviewDialog
      v-model:visible="previewVisible"
      :session-id="sessionId"
      :table-name="previewTableName"
      :gen-scope="genScope"
    />
    <ExternalWriteDiffDialog
      v-if="writeToDiskEnabled"
      ref="writeDiffDialogRef"
      v-model:visible="writeDiffVisible"
      :session-id="sessionId"
      :table-names="selectedTableNames"
      :output-root="outputRoot"
      :gen-scope="genScope"
      @confirm-write="handleConfirmWriteFromDiff"
    />
    <ExternalOutputRootPicker
      v-if="writeToDiskEnabled"
      v-model:visible="outputRootPickerVisible"
      @select="onOutputRootPicked"
    />
  </div>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import {
    ElAlert,
    ElAutocomplete,
    ElButton,
    ElCard,
    ElCheckbox,
    ElCol,
    ElDescriptions,
    ElDescriptionsItem,
    ElDivider,
    ElForm,
    ElFormItem,
    ElInput,
    ElInputNumber,
    ElMessage,
    ElMessageBox,
    ElOption,
    ElRow,
    ElSelect,
    ElSpace,
    ElStep,
    ElSteps,
    ElTabPane,
    ElTabs,
    ElTag,
    ElTooltip
  } from 'element-plus'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import type { ColumnOption } from '@/types'
  import FileSaver from 'file-saver'
  import { fetchGetDictTypeSelectList, type SysDictType } from '@/api/dict/type'
  import { fetchGetParentMenu } from '@/api/menu/menu'
  import PathConfigForm from './modules/path-config-form.vue'
  import ExternalPreviewDialog from './modules/external-preview-dialog.vue'
  import ExternalWriteDiffDialog from './modules/external-write-diff-dialog.vue'
  import ExternalActionBtn from './modules/external-action-btn.vue'
  import ExternalOutputRootPicker from './modules/external-output-root-picker.vue'
  import ExternalColumnTable from './modules/external-column-table.vue'
  import ExternalTableExtraForm, {
    type ExternalTableMeta
  } from './modules/external-table-extra-form.vue'
  import {
    type ExternalDbConnection,
    type ExternalGenScope,
    type ExternalTableItem,
    type ExternalWriteResult,
    fetchExternalCapabilities,
    fetchExternalColumns,
    fetchExternalConnect,
    fetchExternalDisconnect,
    fetchExternalDownload,
    fetchExternalImportGenTable,
    fetchExternalSaveDraft,
    fetchExternalSaveGenConfig,
    fetchExternalSavePathProfile,
    fetchExternalSaveTemplateDir,
    fetchExternalSessionStatus,
    fetchExternalTables,
    fetchExternalTemplates,
    fetchExternalWriteRollback,
    fetchExternalWriteToDisk,
    type GenPathProfile,
    type GenTableColumnItem
  } from '@/api/generator/gen-external'
  import { loadRecentOutputRoots, rememberOutputRoot } from '@/utils/generator/output-root-presets'
  import {
    clearLastWriteBackup,
    loadLastWriteBackup,
    saveLastWriteBackup
  } from '@/utils/generator/write-backup-state'
  import {
    type ConnectionPreset,
    loadConnectionPresets,
    loadLastConnection,
    persistConnectionPresets,
    saveLastConnection,
    toPresetConnection
  } from '@/utils/generator/external-connection-presets'
  import { useI18n } from 'vue-i18n'

  defineOptions({ name: 'GenExternal' })

  const { t } = useI18n()

  const SESSION_KEY = 'gen_external_session_id'

  const currentStep = ref(0)
  const stepLoading = ref(false)
  const sessionId = ref<string>(sessionStorage.getItem(SESSION_KEY) || '')
  const dbInfo = ref('')
  const sessionRemainingSeconds = ref(0)
  let sessionTimer: ReturnType<typeof setInterval> | undefined

  const sessionRemainingText = computed(() => {
    const s = sessionRemainingSeconds.value
    if (s <= 0) return ''
    const m = Math.floor(s / 60)
    const sec = s % 60
    return t('tools.genExt.sessionRemaining', { time: `${m}:${sec.toString().padStart(2, '0')}` })
  })

  const connPresets = ref<ConnectionPreset[]>([])
  const selectedConnPreset = ref('')
  const newConnPresetName = ref('')
  const saveConnPassword = ref(false)
  const sessionExpireWarned = ref(false)

  const connection = reactive<ExternalDbConnection>({
    dbType: 'mysql',
    oracleConnectMode: 'service',
    host: '127.0.0.1',
    port: 3306,
    database: '',
    username: 'root',
    password: '',
    params: 'useSSL=false&serverTimezone=Asia/Shanghai'
  })

  const connFormRef = ref<FormInstance>()
  const connRules = computed<FormRules>(() => ({
    host: [{ required: true, message: t('tools.genExt.hostRequired'), trigger: 'blur' }],
    port: [{ required: true, message: t('tools.genExt.portRequired'), trigger: 'blur' }],
    database: [{ required: true, message: t('tools.genExt.databaseRequired'), trigger: 'blur' }],
    username: [{ required: true, message: t('tools.genExt.usernameRequired'), trigger: 'blur' }],
    password: [{ required: true, message: t('tools.genExt.passwordRequired'), trigger: 'blur' }]
  }))

  const tableSearch = reactive({ tableName: '', tableComment: '' })
  const tableLoading = ref(false)
  const tableData = ref<ExternalTableItem[]>([])
  const selectedTables = ref<ExternalTableItem[]>([])
  const tablePagination = reactive({ current: 1, size: 10, total: 0 })

  const tableColumns = computed<ColumnOption[]>(() => [
    { type: 'selection', width: 55 },
    { prop: 'tableName', label: t('tools.gen.tableName'), minWidth: 160 },
    { prop: 'tableComment', label: t('tools.gen.tableComment'), minWidth: 200 },
    { prop: 'createTime', label: t('common.createTime'), width: 180 }
  ])

  const templateOptions = reactive<{
    categories: { value: string; label: string }[]
    webTypes: { value: string; label: string }[]
  }>({
    categories: [],
    webTypes: []
  })

  const genConfig = reactive({
    tplCategory: 'crud',
    tplWebType: 'art-design-pro',
    author: 'admin'
  })

  const pathProfile = ref<GenPathProfile>({
    basePackage: 'com.star.pivot.system'
  })

  const tableMeta = reactive<Record<string, ExternalTableMeta>>({})
  const tableColumnsMap = reactive<Record<string, GenTableColumnItem[]>>({})
  const dictOptions = ref<SysDictType[]>([])
  const parentMenus = ref<any[]>([])
  const activeTableTab = ref('')
  const previewTableName = ref('')
  const previewVisible = ref(false)
  const downloading = ref(false)
  const importing = ref(false)
  const writing = ref(false)
  const writeDiffLoading = ref(false)
  const writeDiffVisible = ref(false)
  const writeDiffDialogRef = ref<InstanceType<typeof ExternalWriteDiffDialog>>()
  const importOverwrite = ref(false)
  const writeOnlyChanged = ref(true)
  const backupBeforeWrite = ref(true)
  const outputRoot = ref('')
  const outputRootPickerVisible = ref(false)
  const recentOutputRoots = ref<string[]>([])
  const lastWriteBackup = ref<{ backupId: string; outputRoot: string } | null>(null)
  const rollingBack = ref(false)
  const configuredDefaultOutputRoot = ref('')
  const writeToDiskEnabled = ref(true)
  const sessionTemplateDir = ref('')
  const effectiveTemplateDir = ref('')

  const schemaPlaceholder = computed(() => {
    if (connection.dbType === 'postgresql') return 'public'
    if (connection.dbType === 'oracle') return t('tools.genExt.schemaOracle')
    if (connection.dbType === 'sqlserver') return 'dbo'
    return ''
  })

  function dbTypeShortName(dbType?: string) {
    if (dbType === 'postgresql') return 'PG'
    if (dbType === 'oracle') return 'Oracle'
    if (dbType === 'sqlserver') return 'SQL Server'
    return 'MySQL'
  }

  const oracleDatabaseLabel = computed(() => {
    if (connection.dbType !== 'oracle') return t('tools.genExt.databaseName')
    if (connection.oracleConnectMode === 'sid') return 'SID'
    if (connection.oracleConnectMode === 'tns') return t('tools.genExt.tnsOrService')
    return t('tools.genExt.serviceName')
  })

  const oracleDatabasePlaceholder = computed(() => {
    if (connection.dbType !== 'oracle') return 'star_pivot'
    if (connection.oracleConnectMode === 'sid') return 'ORCL'
    if (connection.oracleConnectMode === 'tns') {
      return '(DESCRIPTION=(ADDRESS=...)(CONNECT_DATA=...))'
    }
    return 'XEPDB1 / ORCLPDB'
  })

  const jdbcParamsPlaceholder = computed(() => {
    if (connection.dbType === 'oracle' && connection.oracleConnectMode === 'tns') {
      return t('tools.genExt.jdbcTnsHint')
    }
    if (connection.dbType === 'sqlserver') return 'encrypt=false;trustServerCertificate=true'
    if (connection.dbType === 'postgresql') return 'sslmode=disable'
    return 'useSSL=false&serverTimezone=Asia/Shanghai'
  })

  const genScope = reactive<ExternalGenScope>({
    genBackend: true,
    genFrontend: true,
    genSql: true
  })

  const selectedTableNames = computed(() => selectedTables.value.map((t) => t.tableName))

  async function loadTemplateOptions() {
    try {
      const res = await fetchExternalTemplates()
      templateOptions.categories = res.categories ?? []
      templateOptions.webTypes = res.webTypes ?? []
    } catch {
      templateOptions.categories = [
        { value: 'crud', label: t('tools.gen.tplCrud') },
        { value: 'tree', label: t('tools.gen.tplTree') },
        { value: 'sub', label: t('tools.genExt.tplSub') }
      ]
      templateOptions.webTypes = [
        { value: 'art-design-pro', label: 'Vue3 Art Design Pro' },
        { value: 'element-plus', label: 'Vue3 Element Plus' },
        { value: 'element-ui', label: 'Vue2 Element UI' }
      ]
    }
  }

  async function handleConnect(): Promise<boolean> {
    await connFormRef.value?.validate()
    stepLoading.value = true
    try {
      const res = await fetchExternalConnect(connection)
      sessionId.value = res.sessionId
      sessionStorage.setItem(SESSION_KEY, res.sessionId)
      dbInfo.value = `${res.database} · ${dbTypeShortName(connection.dbType)} ${res.dbVersion}`
      saveLastConnection(connection)
      sessionExpireWarned.value = false
      startSessionTimer()
      await refreshSessionTemplateDir()
      restoreLastWriteBackup()
      ElMessage.success(t('tools.genExt.connectSuccess', { minutes: res.expireMinutes }))
      return true
    } finally {
      stepLoading.value = false
    }
  }

  function stopSessionTimer() {
    if (sessionTimer) {
      clearInterval(sessionTimer)
      sessionTimer = undefined
    }
    sessionRemainingSeconds.value = 0
  }

  async function refreshSessionStatus() {
    if (!sessionId.value) return
    try {
      const status = await fetchExternalSessionStatus(sessionId.value)
      sessionRemainingSeconds.value = status.remainingSeconds ?? 0
      if (status.effectiveTemplateDir !== undefined) {
        effectiveTemplateDir.value = status.effectiveTemplateDir || ''
      }
      if (status.templateDir !== undefined) {
        sessionTemplateDir.value = status.templateDir || ''
      }
      if (!dbInfo.value && status.database) {
        dbInfo.value = `${status.database}${status.dbVersion ? ` · MySQL ${status.dbVersion}` : ''}`
      }
      if (sessionRemainingSeconds.value <= 0) {
        ElMessage.warning(t('tools.genExt.sessionExpired'))
        await handleDisconnect()
      } else if (sessionRemainingSeconds.value <= 300 && !sessionExpireWarned.value) {
        sessionExpireWarned.value = true
        ElMessage.warning(t('tools.genExt.sessionExpiring', { time: sessionRemainingText.value }))
      }
    } catch {
      stopSessionTimer()
    }
  }

  function startSessionTimer() {
    stopSessionTimer()
    refreshSessionStatus()
    sessionTimer = setInterval(refreshSessionStatus, 30_000)
  }

  function loadConnPresetsFromStorage() {
    connPresets.value = loadConnectionPresets()
    const last = loadLastConnection()
    if (last) {
      Object.assign(connection, { ...connection, ...last, password: connection.password })
    }
  }

  function applyConnPreset(name: string) {
    if (!name) return
    const preset = connPresets.value.find((p) => p.name === name)
    if (!preset) return
    Object.assign(connection, preset.connection)
    if (preset.savePassword && preset.password) {
      connection.password = preset.password
      saveConnPassword.value = true
    } else {
      connection.password = ''
      saveConnPassword.value = false
    }
  }

  function saveConnPreset() {
    const name = newConnPresetName.value.trim() || selectedConnPreset.value
    if (!name) {
      ElMessage.warning(t('tools.genExt.presetNameRequired'))
      return
    }
    const entry: ConnectionPreset = {
      name,
      connection: toPresetConnection(connection),
      savePassword: saveConnPassword.value,
      password: saveConnPassword.value ? connection.password : undefined
    }
    const idx = connPresets.value.findIndex((p) => p.name === name)
    if (idx >= 0) {
      connPresets.value[idx] = entry
    } else {
      connPresets.value.push(entry)
    }
    persistConnectionPresets(connPresets.value)
    selectedConnPreset.value = name
    newConnPresetName.value = ''
    ElMessage.success(t('tools.genExt.connPresetSaved'))
  }

  function deleteConnPreset() {
    const name = selectedConnPreset.value
    if (!name) return
    connPresets.value = connPresets.value.filter((p) => p.name !== name)
    persistConnectionPresets(connPresets.value)
    selectedConnPreset.value = ''
    ElMessage.success(t('tools.genExt.connPresetDeleted'))
  }

  async function handleDisconnect() {
    if (!sessionId.value) return
    stopSessionTimer()
    try {
      await fetchExternalDisconnect(sessionId.value)
    } catch {
      /* ignore */
    }
    sessionId.value = ''
    sessionStorage.removeItem(SESSION_KEY)
    lastWriteBackup.value = null
    clearLastWriteBackup()
    dbInfo.value = ''
    currentStep.value = 0
    ElMessage.info(t('tools.genExt.disconnected'))
  }

  async function loadTables() {
    if (!sessionId.value) return
    tableLoading.value = true
    try {
      const page = await fetchExternalTables({
        sessionId: sessionId.value,
        pageNum: tablePagination.current,
        pageSize: tablePagination.size,
        tableName: tableSearch.tableName || undefined,
        tableComment: tableSearch.tableComment || undefined
      })
      tableData.value = page.rows ?? []
      tablePagination.total = page.total ?? 0
    } finally {
      tableLoading.value = false
    }
  }

  function resetTableSearch() {
    tableSearch.tableName = ''
    tableSearch.tableComment = ''
    tablePagination.current = 1
    loadTables()
  }

  function onTableSelectionChange(rows: ExternalTableItem[]) {
    selectedTables.value = rows
  }

  function onTableSizeChange(size: number) {
    tablePagination.size = size
    tablePagination.current = 1
    loadTables()
  }

  function onTablePageChange(page: number) {
    tablePagination.current = page
    loadTables()
  }

  function guessBusinessName(tableName: string): string {
    const idx = tableName.lastIndexOf('_')
    return idx >= 0 ? tableName.slice(idx + 1) : tableName
  }

  function guessClassName(tableName: string): string {
    return tableName
      .split('_')
      .map((s) => s.charAt(0).toUpperCase() + s.slice(1).toLowerCase())
      .join('')
  }

  function guessTreeFields(name: string, cols: GenTableColumnItem[]) {
    const meta = tableMeta[name]
    if (!meta || genConfig.tplCategory !== 'tree') return
    if (meta.treeCode && meta.treeParentCode && meta.treeName) return

    const names = cols.map((c) => c.columnName)
    const parentCol =
      names.find((n) => /^(parent_id|pid|parentId)$/i.test(n)) ||
      names.find((n) => n.includes('parent') && n.endsWith('_id'))
    const pkCol = cols.find((c) => c.isPk === '1')?.columnName
    const nameCol =
      names.find((n) => /_name$/.test(n) && !n.includes('user')) ||
      names.find((n) => n === 'name' || n === 'title')

    if (!meta.treeParentCode && parentCol) meta.treeParentCode = parentCol
    if (!meta.treeCode && pkCol) meta.treeCode = pkCol
    if (!meta.treeName && nameCol) meta.treeName = nameCol
  }

  async function loadStep2Resources() {
    const [dictRes, menuRes] = await Promise.all([
      fetchGetDictTypeSelectList(),
      fetchGetParentMenu()
    ])
    const dictData = (dictRes as any)?.data ?? dictRes
    dictOptions.value = Array.isArray(dictData) ? dictData : []
    const menuData = (menuRes as any)?.data ?? menuRes
    parentMenus.value = Array.isArray(menuData) ? menuData : []
  }

  function validateStep2Config(): boolean {
    if (genConfig.tplCategory === 'tree') {
      for (const name of selectedTableNames.value) {
        const meta = tableMeta[name]
        if (!meta?.treeCode || !meta?.treeParentCode || !meta?.treeName) {
          ElMessage.warning(t('tools.genExt.treeConfigRequired', { name }))
          activeTableTab.value = name
          return false
        }
      }
    }
    if (genConfig.tplCategory === 'sub') {
      for (const name of selectedTableNames.value) {
        const meta = tableMeta[name]
        if (!meta?.subTableName || !meta?.subTableFkName) {
          ElMessage.warning(t('tools.genExt.subTableConfigRequired', { name }))
          activeTableTab.value = name
          return false
        }
      }
    }
    return true
  }

  async function loadColumnsForSelected() {
    if (!sessionId.value) return
    for (const row of selectedTables.value) {
      const name = row.tableName
      const cols = await fetchExternalColumns(sessionId.value, name)
      tableColumnsMap[name] = cols
      if (!tableMeta[name]) {
        tableMeta[name] = {
          className: guessClassName(name),
          businessName: guessBusinessName(name),
          functionName: row.tableComment || name,
          tableComment: row.tableComment || name
        }
      }
      guessTreeFields(name, cols)
    }
    activeTableTab.value = selectedTableNames.value[0] ?? ''
    previewTableName.value = selectedTableNames.value[0] ?? ''
  }

  async function saveDrafts() {
    if (!sessionId.value) return
    for (const name of selectedTableNames.value) {
      const meta = tableMeta[name]
      await fetchExternalSaveDraft({
        sessionId: sessionId.value,
        tableName: name,
        tableComment: meta?.tableComment,
        className: meta?.className,
        businessName: meta?.businessName,
        functionName: meta?.functionName,
        treeCode: meta?.treeCode,
        treeParentCode: meta?.treeParentCode,
        treeName: meta?.treeName,
        parentMenuId: meta?.parentMenuId,
        subTableName: meta?.subTableName,
        subTableFkName: meta?.subTableFkName,
        vuePagePath: meta?.vuePagePath,
        apiPath: meta?.apiPath,
        columns: tableColumnsMap[name]
      })
    }
  }

  async function nextStep() {
    if (currentStep.value === 0) {
      const ok = await handleConnect()
      if (ok) {
        currentStep.value = 1
        await loadTables()
      }
      return
    }
    if (currentStep.value === 1) {
      if (selectedTables.value.length === 0) {
        ElMessage.warning(t('tools.genExt.selectAtLeastOneTable'))
        return
      }
      stepLoading.value = true
      try {
        await loadColumnsForSelected()
        await loadStep2Resources()
        currentStep.value = 2
      } finally {
        stepLoading.value = false
      }
      return
    }
    if (currentStep.value === 2) {
      if (!genConfig.tplCategory || !genConfig.tplWebType) {
        ElMessage.warning(t('tools.genExt.selectTplType'))
        return
      }
      if (!validateStep2Config()) return
      stepLoading.value = true
      try {
        await saveDrafts()
        await fetchExternalSaveGenConfig({
          sessionId: sessionId.value,
          tableNames: selectedTableNames.value,
          tplCategory: genConfig.tplCategory,
          tplWebType: genConfig.tplWebType,
          author: genConfig.author,
          pathProfile: pathProfile.value
        })
        if (!pathProfile.value.entityPackage) {
          const base = pathProfile.value.basePackage || 'com.star.pivot.system'
          pathProfile.value.basePackage = base
          pathProfile.value.entityPackage = `${base}.domain.entity`
          pathProfile.value.dtoPackage = `${base}.domain.dto`
          pathProfile.value.voPackage = `${base}.domain.bo`
          pathProfile.value.boPackage = `${base}.domain.bo`
          pathProfile.value.mapperPackage = `${base}.mapper`
          pathProfile.value.servicePackage = `${base}.service`
          pathProfile.value.serviceImplPackage = `${base}.service.impl`
          pathProfile.value.controllerPackage = `${base}.controller`
          const mod = base.split('.').pop() || 'app'
          pathProfile.value.mapperXmlPath = `main/resources/mapper/${mod}`
          pathProfile.value.apiPath = `star-pivot-ui/src/api/${mod}`
          const biz = tableMeta[selectedTableNames.value[0]]?.businessName || 'demo'
          pathProfile.value.vuePagePath = `star-pivot-ui/src/views/${mod}/${biz}`
        }
        currentStep.value = 3
      } finally {
        stepLoading.value = false
      }
      return
    }
    if (currentStep.value === 3) {
      if (!pathProfile.value.basePackage?.trim()) {
        ElMessage.warning(t('tools.genExt.fillBasePackage'))
        return
      }
      stepLoading.value = true
      try {
        await fetchExternalSavePathProfile(sessionId.value, pathProfile.value)
        currentStep.value = 4
        previewTableName.value = selectedTableNames.value[0] ?? ''
      } finally {
        stepLoading.value = false
      }
    }
  }

  function prevStep() {
    if (currentStep.value > 0) currentStep.value -= 1
  }

  function validateGenScope(): boolean {
    if (!genScope.genBackend && !genScope.genFrontend && !genScope.genSql) {
      ElMessage.warning(t('tools.genExt.selectGenScope'))
      return false
    }
    return true
  }

  async function ensureOutputRootForWrite(): Promise<boolean> {
    if (outputRoot.value.trim() || configuredDefaultOutputRoot.value.trim()) {
      return true
    }
    try {
      await ElMessageBox.confirm(
        t('tools.genExt.outputRootConfirm'),
        t('tools.genExt.outputRoot'),
        {
          type: 'warning',
          confirmButtonText: t('tools.genExt.continue'),
          cancelButtonText: t('common.cancel')
        }
      )
      return true
    } catch {
      return false
    }
  }

  function restoreLastWriteBackup() {
    if (!sessionId.value) {
      lastWriteBackup.value = null
      return
    }
    lastWriteBackup.value = loadLastWriteBackup(sessionId.value)
  }

  function openPreview() {
    if (!previewTableName.value) {
      ElMessage.warning(t('tools.genExt.selectPreviewTable'))
      return
    }
    if (!validateGenScope()) return
    previewVisible.value = true
  }

  async function handleDownload() {
    if (!sessionId.value || selectedTableNames.value.length === 0) return
    if (!validateGenScope()) return
    downloading.value = true
    try {
      const blob = await fetchExternalDownload(sessionId.value, selectedTableNames.value, genScope)
      FileSaver.saveAs(blob, `codegen_external_${Date.now()}.zip`)
      ElMessage.success(t('tools.genExt.downloadSuccess'))
    } finally {
      downloading.value = false
    }
  }

  function onDbTypeChange(type: string) {
    if (type === 'postgresql') {
      connection.port = 5432
      if (!connection.schema) connection.schema = 'public'
      if (!connection.params) connection.params = ''
    } else if (type === 'oracle') {
      connection.port = 1521
      connection.oracleConnectMode = connection.oracleConnectMode || 'service'
      if (!connection.schema) connection.schema = connection.username?.toUpperCase() || ''
      if (!connection.params) connection.params = ''
    } else if (type === 'sqlserver') {
      connection.port = 1433
      if (!connection.schema) connection.schema = 'dbo'
      if (!connection.params) connection.params = 'encrypt=false;trustServerCertificate=true'
    } else {
      connection.port = 3306
      if (!connection.params) {
        connection.params = 'useSSL=false&serverTimezone=Asia/Shanghai'
      }
    }
  }

  async function syncPathProfileToServer() {
    if (!sessionId.value || !pathProfile.value.basePackage?.trim()) return
    await fetchExternalSavePathProfile(sessionId.value, pathProfile.value)
  }

  async function openWriteDiff() {
    if (!sessionId.value || selectedTableNames.value.length === 0) return
    if (!validateGenScope()) return
    if (!(await ensureOutputRootForWrite())) return
    writeDiffLoading.value = true
    try {
      await syncPathProfileToServer()
      writeDiffVisible.value = true
    } finally {
      writeDiffLoading.value = false
    }
  }

  async function handleSaveTemplateDir() {
    if (!sessionId.value) return
    await fetchExternalSaveTemplateDir(sessionId.value, sessionTemplateDir.value.trim())
    await refreshSessionTemplateDir()
    ElMessage.success(t('tools.genExt.templateDirSaved'))
  }

  async function refreshSessionTemplateDir() {
    if (!sessionId.value) return
    const status = await fetchExternalSessionStatus(sessionId.value)
    effectiveTemplateDir.value = status.effectiveTemplateDir || ''
    sessionTemplateDir.value = status.templateDir || ''
  }

  function queryOutputRoots(queryString: string, cb: (items: { value: string }[]) => void) {
    const q = queryString.trim().toLowerCase()
    const list = recentOutputRoots.value
      .filter((p) => !q || p.toLowerCase().includes(q))
      .map((p) => ({ value: p }))
    cb(list)
  }

  function onOutputRootPicked(path: string) {
    outputRoot.value = path
  }

  function applyWriteResult(res: ExternalWriteResult) {
    rememberOutputRoot(res.outputRoot)
    recentOutputRoots.value = loadRecentOutputRoots()
    if (res.backupId && (res.backedUpCount ?? 0) > 0 && sessionId.value) {
      lastWriteBackup.value = { backupId: res.backupId, outputRoot: res.outputRoot }
      saveLastWriteBackup({
        sessionId: sessionId.value,
        backupId: res.backupId,
        outputRoot: res.outputRoot
      })
    }
    const backupTip =
      res.backedUpCount && res.backedUpCount > 0
        ? t('tools.genExt.writeBackupTip', { count: res.backedUpCount })
        : ''
    ElMessage.success(
      t('tools.genExt.writeSuccess', {
        count: res.fileCount,
        root: res.outputRoot,
        backup: backupTip
      })
    )
  }

  async function handleRollbackWrite() {
    if (!sessionId.value || !lastWriteBackup.value) return
    await ElMessageBox.confirm(t('tools.genExt.rollbackConfirm'), t('tools.genExt.rollbackWrite'), {
      type: 'warning'
    })
    rollingBack.value = true
    try {
      const res = await fetchExternalWriteRollback(
        sessionId.value,
        lastWriteBackup.value.backupId,
        lastWriteBackup.value.outputRoot
      )
      ElMessage.success(t('tools.genExt.rollbackSuccess', { count: res.fileCount }))
      lastWriteBackup.value = null
      clearLastWriteBackup()
    } finally {
      rollingBack.value = false
    }
  }

  async function handleConfirmWriteFromDiff(selectedPaths: string[]) {
    if (!sessionId.value || selectedTableNames.value.length === 0) return
    if (!selectedPaths.length) return
    writeDiffDialogRef.value?.setWriting(true)
    try {
      await syncPathProfileToServer()
      const res = await fetchExternalWriteToDisk(
        sessionId.value,
        selectedTableNames.value,
        outputRoot.value.trim() || undefined,
        genScope,
        { selectedPaths, backupBeforeWrite: backupBeforeWrite.value }
      )
      applyWriteResult(res)
      writeDiffVisible.value = false
    } finally {
      writeDiffDialogRef.value?.setWriting(false)
    }
  }

  async function handleWriteToDisk() {
    if (!sessionId.value || selectedTableNames.value.length === 0) return
    if (!validateGenScope()) return
    if (!(await ensureOutputRootForWrite())) return
    writing.value = true
    try {
      await syncPathProfileToServer()
      const res = await fetchExternalWriteToDisk(
        sessionId.value,
        selectedTableNames.value,
        outputRoot.value.trim() || undefined,
        genScope,
        { onlyChanged: writeOnlyChanged.value, backupBeforeWrite: backupBeforeWrite.value }
      )
      applyWriteResult(res)
    } finally {
      writing.value = false
    }
  }

  async function handleImportGenTable() {
    if (!sessionId.value || selectedTableNames.value.length === 0) return
    importing.value = true
    try {
      const res = await fetchExternalImportGenTable(
        sessionId.value,
        selectedTableNames.value,
        importOverwrite.value
      )
      const parts: string[] = []
      if (res.imported?.length)
        parts.push(t('tools.genExt.importAdded', { count: res.imported.length }))
      if (res.updated?.length)
        parts.push(t('tools.genExt.importUpdated', { count: res.updated.length }))
      if (res.skipped?.length)
        parts.push(t('tools.genExt.importSkipped', { count: res.skipped.length }))
      ElMessage.success(parts.length ? parts.join('，') : t('tools.genExt.importDone'))
    } finally {
      importing.value = false
    }
  }

  watch(
    () => genConfig.tplCategory,
    (cat) => {
      if (cat !== 'tree') return
      for (const name of selectedTableNames.value) {
        guessTreeFields(name, tableColumnsMap[name] || [])
      }
    }
  )

  onMounted(() => {
    loadTemplateOptions()
    loadConnPresetsFromStorage()
    recentOutputRoots.value = loadRecentOutputRoots()
    if (recentOutputRoots.value.length && !outputRoot.value) {
      outputRoot.value = recentOutputRoots.value[0]
    }
    fetchExternalCapabilities()
      .then((cap) => {
        writeToDiskEnabled.value = cap.writeToDiskEnabled !== false
        if (cap.backupBeforeWrite !== undefined) {
          backupBeforeWrite.value = cap.backupBeforeWrite
        }
        configuredDefaultOutputRoot.value = cap.defaultOutputRoot || ''
        if (cap.defaultOutputRoot && !outputRoot.value) {
          outputRoot.value = cap.defaultOutputRoot
        }
      })
      .catch(() => {})
    restoreLastWriteBackup()
    if (sessionId.value) {
      ElMessageBox.confirm(t('tools.genExt.sessionRestoreConfirm'), t('common.tips'), {
        confirmButtonText: t('tools.genExt.continue'),
        cancelButtonText: t('tools.genExt.reconnect'),
        type: 'info'
      })
        .then(() => {
          currentStep.value = 1
          startSessionTimer()
          loadTables()
        })
        .catch(() => {
          handleDisconnect()
        })
    }
  })

  onBeforeUnmount(() => {
    stopSessionTimer()
  })
</script>

<style scoped lang="scss">
  .gen-external-page {
    padding: 12px;

    .step-card {
      min-height: calc(100vh - 120px);
    }

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .title {
        font-size: 16px;
        font-weight: 600;
      }
    }

    .step-panel {
      min-height: 360px;
      padding: 8px 0 24px;
    }

    .conn-form {
      max-width: 960px;
      margin: 0 auto;
    }

    .conn-preset-bar {
      max-width: 960px;
      margin: 0 auto;
    }

    .session-ttl {
      opacity: 0.85;
    }

    .step-footer {
      display: flex;
      gap: 12px;
      justify-content: center;
      padding-top: 16px;
      border-top: 1px solid var(--el-border-color-lighter);
    }

    .action-btn-row {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      align-items: center;
    }

    .w-full {
      width: 100%;
    }
  }

  @media (width <= 768px) {
    .gen-external-page {
      :deep(.el-steps) {
        overflow-x: auto;
        padding-bottom: 8px;
      }

      :deep(.el-step__title) {
        font-size: 12px;
      }

      .conn-form,
      .conn-preset-bar,
      .tpl-form {
        max-width: 100%;
      }

      .conn-preset-bar,
      .conn-form,
      .tpl-form {
        :deep(.el-form-item) {
          margin-bottom: 14px;
        }
      }

      .step-footer {
        flex-wrap: wrap;
      }

      :deep(.el-form--inline .el-form-item) {
        display: block;
        margin-right: 0;
        width: 100%;
      }
    }
  }
</style>
