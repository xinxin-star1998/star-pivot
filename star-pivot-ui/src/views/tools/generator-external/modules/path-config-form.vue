<template>
  <ElForm ref="formRef" :model="model" label-width="160px" class="path-config-form">
    <ElRow :gutter="16">
      <ElCol :span="24">
        <ElFormItem :label="t('tools.genExt.modulePreset')">
          <ElSelect
            v-model="preset"
            :placeholder="t('tools.genExt.builtinPreset')"
            clearable
            style="width: 180px"
            @change="applyBuiltinPreset"
          >
            <ElOption :label="t('tools.genExt.presetSystem')" value="system" />
            <ElOption :label="t('tools.genExt.presetFile')" value="file" />
            <ElOption :label="t('tools.genExt.presetMonitor')" value="monitor" />
          </ElSelect>
          <ElSelect
            v-model="savedPresetKey"
            :placeholder="t('tools.genExt.myPreset')"
            clearable
            style="width: 180px"
            class="ml-2"
            @change="applySavedPreset"
          >
            <ElOption
              v-for="item in savedPresetList"
              :key="item.name"
              :label="item.name"
              :value="item.name"
            />
          </ElSelect>
          <ExternalActionBtn
            :what="t('tools.genExt.syncFromBaseWhat')"
            :usage="t('tools.genExt.syncFromBaseUsage')"
            link
            type="primary"
            class="ml-3"
            @click="syncFromBasePackage"
          >
            {{ t('tools.genExt.syncFromBase') }}
          </ExternalActionBtn>
        </ElFormItem>
      </ElCol>

      <ElCol :span="24">
        <ElFormItem :label="t('tools.genExt.saveAsPreset')">
          <ElInput
            v-model="newPresetName"
            :placeholder="t('tools.genExt.presetNamePlaceholder')"
            style="width: 220px"
            maxlength="32"
          />
          <ExternalActionBtn
            :what="t('tools.genExt.savePresetWhat')"
            :usage="t('tools.genExt.savePresetUsage')"
            type="primary"
            class="ml-2"
            @click="saveCurrentAsPreset"
          >
            {{ t('tools.genExt.savePreset') }}
          </ExternalActionBtn>
          <ExternalActionBtn
            v-if="savedPresetKey"
            :what="t('tools.genExt.deletePresetWhat')"
            :usage="t('tools.genExt.deletePresetUsage')"
            link
            type="danger"
            class="ml-2"
            @click="deleteSavedPreset"
          >
            {{ t('tools.genExt.deleteCurrentPreset') }}
          </ExternalActionBtn>
        </ElFormItem>
      </ElCol>

      <ElCol :span="12">
        <ElFormItem :label="t('tools.gen.packageName')" required>
          <ElInput
            v-model="model.basePackage"
            placeholder="com.star.pivot.system"
            @blur="onBasePackageBlur"
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.gen.author')">
          <ElInput v-model="author" :placeholder="t('tools.genExt.authorPlaceholder')" />
        </ElFormItem>
      </ElCol>

      <ElCol :span="24"><div class="section-title">{{ t('tools.genExt.javaPackagePath') }}</div></ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.genExt.entityPackage')">
          <ElInput
            v-model="model.entityPackage"
            placeholder="com.star.pivot.system.domain.entity"
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem label="DTO">
          <ElInput v-model="model.dtoPackage" placeholder="com.star.pivot.system.domain.dto" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.genExt.voPackage')">
          <ElInput v-model="model.voPackage" placeholder="com.star.pivot.system.domain.bo" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.genExt.boPackage')">
          <ElInput v-model="model.boPackage" placeholder="com.star.pivot.system.domain.bo" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem label="Mapper">
          <ElInput v-model="model.mapperPackage" placeholder="com.star.pivot.system.mapper" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem label="Service">
          <ElInput v-model="model.servicePackage" placeholder="com.star.pivot.system.service" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem label="ServiceImpl">
          <ElInput
            v-model="model.serviceImplPackage"
            placeholder="com.star.pivot.system.service.impl"
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem label="Controller">
          <ElInput
            v-model="model.controllerPackage"
            placeholder="com.star.pivot.system.controller"
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="24">
        <ElFormItem :label="t('tools.genExt.mapperXmlPath')">
          <ElInput v-model="model.mapperXmlPath" placeholder="main/resources/mapper/system" />
          <div class="field-hint">{{ t('tools.genExt.mapperXmlHint') }}</div>
        </ElFormItem>
      </ElCol>

      <ElCol :span="24"><div class="section-title">{{ t('tools.genExt.relativePathTitle') }}</div></ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.genExt.apiPath')">
          <ElInput v-model="model.apiPath" placeholder="star-pivot-ui/src/api/system" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="12">
        <ElFormItem :label="t('tools.genExt.vuePagePath')">
          <ElInput v-model="model.vuePagePath" placeholder="star-pivot-ui/src/views/system/post" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="24">
        <ElFormItem :label="t('tools.genExt.vueModulesPath')">
          <ElInput v-model="model.vueModulesPath" :placeholder="t('tools.genExt.vueModulesPlaceholder')" />
        </ElFormItem>
      </ElCol>
    </ElRow>
  </ElForm>
</template>

<script setup lang="ts">
  import { ElForm, ElFormItem, ElInput, ElMessage, ElOption, ElSelect } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import ExternalActionBtn from './external-action-btn.vue'
  import type { GenPathProfile } from '@/api/generator/gen-external'

  const PRESET_STORAGE_KEY = 'gen_external_path_presets'
  const LAST_PROFILE_KEY = 'gen_external_last_path_profile'

  interface SavedPreset {
    name: string
    profile: GenPathProfile
  }

  const model = defineModel<GenPathProfile>({ required: true })
  const author = defineModel<string>('author', { default: '' })
  const { t } = useI18n()

  const preset = ref<string>()
  const savedPresetKey = ref<string>()
  const newPresetName = ref('')
  const savedPresetList = ref<SavedPreset[]>([])

  const PRESETS: Record<string, Partial<GenPathProfile>> = {
    system: {
      basePackage: 'com.star.pivot.system',
      mapperXmlPath: 'main/resources/mapper/system',
      apiPath: 'star-pivot-ui/src/api/system',
      vuePagePath: 'star-pivot-ui/src/views/system/demo'
    },
    file: {
      basePackage: 'com.star.pivot.file',
      mapperXmlPath: 'main/resources/mapper/file',
      apiPath: 'star-pivot-ui/src/api/file',
      vuePagePath: 'star-pivot-ui/src/views/file/demo'
    },
    monitor: {
      basePackage: 'com.star.pivot.monitor',
      mapperXmlPath: 'main/resources/mapper/monitor',
      apiPath: 'star-pivot-ui/src/api/monitor',
      vuePagePath: 'star-pivot-ui/src/views/monitor/demo'
    }
  }

  function loadSavedPresets() {
    try {
      const raw = localStorage.getItem(PRESET_STORAGE_KEY)
      savedPresetList.value = raw ? (JSON.parse(raw) as SavedPreset[]) : []
    } catch {
      savedPresetList.value = []
    }
  }

  function persistSavedPresets() {
    localStorage.setItem(PRESET_STORAGE_KEY, JSON.stringify(savedPresetList.value))
  }

  function moduleFromBase(base: string): string {
    const idx = base.lastIndexOf('.')
    return idx >= 0 ? base.slice(idx + 1) : base
  }

  function syncFromBasePackage() {
    const base = model.value.basePackage?.trim()
    if (!base) return
    const module = moduleFromBase(base)
    model.value.entityPackage = `${base}.domain.entity`
    model.value.dtoPackage = `${base}.domain.dto`
    model.value.voPackage = `${base}.domain.bo`
    model.value.boPackage = `${base}.domain.bo`
    model.value.mapperPackage = `${base}.mapper`
    model.value.servicePackage = `${base}.service`
    model.value.serviceImplPackage = `${base}.service.impl`
    model.value.controllerPackage = `${base}.controller`
    model.value.mapperXmlPath = `main/resources/mapper/${module}`
    model.value.apiPath = `star-pivot-ui/src/api/${module}`
    if (!model.value.vuePagePath) {
      model.value.vuePagePath = `star-pivot-ui/src/views/${module}/demo`
    }
  }

  function applyBuiltinPreset(key: string) {
    if (!key || !PRESETS[key]) return
    model.value = { ...model.value, ...PRESETS[key] }
    syncFromBasePackage()
    savedPresetKey.value = undefined
  }

  function applySavedPreset(name: string) {
    if (!name) return
    const item = savedPresetList.value.find((p) => p.name === name)
    if (!item) return
    model.value = { ...item.profile }
    preset.value = undefined
  }

  function saveCurrentAsPreset() {
    const name = newPresetName.value.trim()
    if (!name) {
      ElMessage.warning(t('tools.genExt.presetNameRequired'))
      return
    }
    if (!model.value.basePackage?.trim()) {
      ElMessage.warning(t('tools.genExt.basePackageRequired'))
      return
    }
    const idx = savedPresetList.value.findIndex((p) => p.name === name)
    const entry: SavedPreset = { name, profile: { ...model.value } }
    if (idx >= 0) {
      savedPresetList.value[idx] = entry
    } else {
      savedPresetList.value.push(entry)
    }
    persistSavedPresets()
    savedPresetKey.value = name
    newPresetName.value = ''
    ElMessage.success(t('tools.genExt.presetSavedLocal'))
  }

  function deleteSavedPreset() {
    const name = savedPresetKey.value
    if (!name) return
    savedPresetList.value = savedPresetList.value.filter((p) => p.name !== name)
    persistSavedPresets()
    savedPresetKey.value = undefined
    ElMessage.success(t('tools.genExt.presetDeleted'))
  }

  function onBasePackageBlur() {
    if (!model.value.entityPackage) {
      syncFromBasePackage()
    }
  }

  function restoreLastProfile() {
    try {
      const raw = localStorage.getItem(LAST_PROFILE_KEY)
      if (raw) {
        const last = JSON.parse(raw) as GenPathProfile
        if (last.basePackage) {
          model.value = { ...model.value, ...last }
        }
      }
    } catch {
      /* ignore */
    }
  }

  watch(
    model,
    (val) => {
      if (val.basePackage) {
        localStorage.setItem(LAST_PROFILE_KEY, JSON.stringify(val))
      }
    },
    { deep: true }
  )

  onMounted(() => {
    loadSavedPresets()
    restoreLastProfile()
  })

  defineExpose({ syncFromBasePackage })
</script>

<style scoped lang="scss">
  .path-config-form {
    .section-title {
      margin: 8px 0 16px;
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .field-hint {
      margin-top: 4px;
      font-size: 12px;
      line-height: 1.4;
      color: var(--el-text-color-secondary);
    }

    .ml-2 {
      margin-left: 8px;
    }

    .ml-3 {
      margin-left: 12px;
    }
  }
</style>
