<template>
  <ElForm :model="meta" label-width="120px" class="external-table-extra">
    <ElRow :gutter="16">
      <ElCol :span="8">
        <ElFormItem :label="t('tools.gen.className')">
          <ElInput v-model="meta.className" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.gen.businessName')">
          <ElInput v-model="meta.businessName" :placeholder="t('tools.genExt.businessNameHint')" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.gen.functionName')">
          <ElInput v-model="meta.functionName" :placeholder="t('tools.genExt.functionNameHint')" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.gen.tableComment')">
          <ElInput v-model="meta.tableComment" />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.gen.parentMenu')">
          <ElTreeSelect
            v-model="meta.parentMenuId"
            :data="menuTreeOptions"
            check-strictly
            :render-after-expand="false"
            clearable
            :placeholder="t('tools.genExt.parentMenuHint')"
            class="w-full"
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.genExt.vuePagePath')">
          <ElInput
            v-model="meta.vuePagePath"
            :placeholder="t('tools.genExt.useGlobalConfig')"
            clearable
          />
        </ElFormItem>
      </ElCol>
      <ElCol :span="8">
        <ElFormItem :label="t('tools.genExt.apiPath')">
          <ElInput
            v-model="meta.apiPath"
            :placeholder="t('tools.genExt.useGlobalConfig')"
            clearable
          />
        </ElFormItem>
      </ElCol>
    </ElRow>

    <template v-if="tplCategory === 'tree'">
      <ElDivider content-position="left">{{ t('tools.genExt.treeConfig') }}</ElDivider>
      <ElRow :gutter="16">
        <ElCol :span="8">
          <ElFormItem :label="t('tools.genExt.treeCode')">
            <ElSelect v-model="meta.treeCode" :placeholder="t('common.pleaseSelect')" clearable>
              <ElOption
                v-for="col in columns"
                :key="col.columnName"
                :label="`${col.columnName}：${col.columnComment || ''}`"
                :value="col.columnName"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem :label="t('tools.genExt.treeParentCode')">
            <ElSelect
              v-model="meta.treeParentCode"
              :placeholder="t('common.pleaseSelect')"
              clearable
            >
              <ElOption
                v-for="col in columns"
                :key="col.columnName"
                :label="`${col.columnName}：${col.columnComment || ''}`"
                :value="col.columnName"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem :label="t('tools.genExt.treeName')">
            <ElSelect v-model="meta.treeName" :placeholder="t('common.pleaseSelect')" clearable>
              <ElOption
                v-for="col in columns"
                :key="col.columnName"
                :label="`${col.columnName}：${col.columnComment || ''}`"
                :value="col.columnName"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
      </ElRow>
    </template>

    <template v-if="tplCategory === 'sub'">
      <ElDivider content-position="left">{{ t('tools.genExt.subTableConfig') }}</ElDivider>
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem :label="t('tools.genExt.subTable')">
            <ElSelect
              v-model="meta.subTableName"
              :placeholder="t('tools.genExt.subTablePlaceholder')"
              clearable
              @change="onSubTableChange"
            >
              <ElOption
                v-for="name in subTableCandidates"
                :key="name"
                :label="name"
                :value="name"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem :label="t('tools.genExt.subTableFk')">
            <ElSelect
              v-model="meta.subTableFkName"
              :placeholder="t('common.pleaseSelect')"
              clearable
            >
              <ElOption
                v-for="col in subColumns"
                :key="col.columnName"
                :label="`${col.columnName}：${col.columnComment || ''}`"
                :value="col.columnName"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
      </ElRow>
    </template>
  </ElForm>
</template>

<script setup lang="ts">
  import {
    ElCol,
    ElDivider,
    ElForm,
    ElFormItem,
    ElInput,
    ElOption,
    ElRow,
    ElSelect,
    ElTreeSelect
  } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import type { GenTableColumnItem } from '@/api/generator/gen-external'

  export interface ExternalTableMeta {
    className: string
    businessName: string
    functionName: string
    tableComment?: string
    parentMenuId?: number | string
    treeCode?: string
    treeParentCode?: string
    treeName?: string
    subTableName?: string
    subTableFkName?: string
    vuePagePath?: string
    apiPath?: string
  }

  interface MenuItem {
    menuId: number | string
    menuName: string
    children?: MenuItem[]
  }

  const props = defineProps<{
    meta: ExternalTableMeta
    tplCategory: string
    columns: GenTableColumnItem[]
    currentTableName: string
    selectedTableNames: string[]
    parentMenus: MenuItem[]
    allColumnsMap: Record<string, GenTableColumnItem[]>
  }>()

  const { t } = useI18n()

  const subColumns = ref<GenTableColumnItem[]>([])

  const subTableCandidates = computed(() =>
    props.selectedTableNames.filter((name) => name !== props.currentTableName)
  )

  const menuTreeOptions = computed(() => {
    const toTree = (node: MenuItem) => ({
      value: node.menuId,
      label: node.menuName,
      children: node.children?.map(toTree)
    })
    return props.parentMenus.map(toTree)
  })

  function onSubTableChange(name?: string) {
    props.meta.subTableFkName = ''
    subColumns.value = name ? props.allColumnsMap[name] || [] : []
  }

  watch(
    () => props.meta.subTableName,
    (name) => {
      subColumns.value = name ? props.allColumnsMap[name] || [] : []
    },
    { immediate: true }
  )
</script>

<style scoped lang="scss">
  .external-table-extra {
    .w-full {
      width: 100%;
    }
  }
</style>
