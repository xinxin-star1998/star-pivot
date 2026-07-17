<template>
  <ElTable :data="columns" border size="small" :max-height="maxHeight" style="width: 100%">
    <ElTableColumn type="index" label="#" width="50" />
    <ElTableColumn
      prop="columnName"
      :label="t('tools.gen.fieldColumnName')"
      width="130"
      show-overflow-tooltip
    />
    <ElTableColumn :label="t('tools.gen.fieldComment')" min-width="120">
      <template #default="{ row }">
        <ElInput v-model="row.columnComment" size="small" />
      </template>
    </ElTableColumn>
    <ElTableColumn
      prop="columnType"
      :label="t('tools.gen.fieldType')"
      width="110"
      show-overflow-tooltip
    />
    <ElTableColumn :label="t('tools.gen.javaType')" width="120">
      <template #default="{ row }">
        <ElSelect v-model="row.javaType" size="small">
          <ElOption label="Long" value="Long" />
          <ElOption label="String" value="String" />
          <ElOption label="Integer" value="Integer" />
          <ElOption label="Double" value="Double" />
          <ElOption label="BigDecimal" value="BigDecimal" />
          <ElOption label="Date" value="Date" />
          <ElOption label="Boolean" value="Boolean" />
        </ElSelect>
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.javaField')" width="120">
      <template #default="{ row }">
        <ElInput v-model="row.javaField" size="small" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.isInsert')" width="52" align="center">
      <template #default="{ row }">
        <ElCheckbox v-model="row.isInsert" true-value="1" false-value="0" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.isEdit')" width="52" align="center">
      <template #default="{ row }">
        <ElCheckbox v-model="row.isEdit" true-value="1" false-value="0" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.isList')" width="52" align="center">
      <template #default="{ row }">
        <ElCheckbox v-model="row.isList" true-value="1" false-value="0" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.isQuery')" width="52" align="center">
      <template #default="{ row }">
        <ElCheckbox v-model="row.isQuery" true-value="1" false-value="0" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.queryType')" width="120">
      <template #default="{ row }">
        <ElSelect v-model="row.queryType" size="small" :disabled="row.isQuery !== '1'">
          <ElOption label="=" value="EQ" />
          <ElOption label="!=" value="NE" />
          <ElOption label=">" value="GT" />
          <ElOption label=">=" value="GTE" />
          <ElOption label="<" value="LT" />
          <ElOption label="<=" value="LTE" />
          <ElOption label="LIKE" value="LIKE" />
          <ElOption label="BETWEEN" value="BETWEEN" />
        </ElSelect>
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.isRequired')" width="52" align="center">
      <template #default="{ row }">
        <ElCheckbox v-model="row.isRequired" true-value="1" false-value="0" />
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.htmlType')" width="130">
      <template #default="{ row }">
        <ElSelect v-model="row.htmlType" size="small">
          <ElOption :label="t('tools.genExt.htmlInput')" value="input" />
          <ElOption :label="t('tools.genExt.htmlTextarea')" value="textarea" />
          <ElOption :label="t('tools.genExt.htmlSelect')" value="select" />
          <ElOption :label="t('tools.genExt.htmlRadio')" value="radio" />
          <ElOption :label="t('tools.genExt.htmlCheckbox')" value="checkbox" />
          <ElOption :label="t('tools.genExt.htmlDatetime')" value="datetime" />
          <ElOption :label="t('tools.genExt.htmlImageUpload')" value="imageUpload" />
          <ElOption :label="t('tools.genExt.htmlFileUpload')" value="fileUpload" />
          <ElOption :label="t('tools.genExt.htmlEditor')" value="editor" />
        </ElSelect>
      </template>
    </ElTableColumn>
    <ElTableColumn :label="t('tools.gen.dictType')" min-width="160">
      <template #default="{ row }">
        <ElSelect
          v-model="row.dictType"
          clearable
          filterable
          :placeholder="t('common.pleaseSelect')"
          size="small"
          :disabled="!['select', 'radio', 'checkbox'].includes(row.htmlType)"
        >
          <ElOption
            v-for="dict in dictOptions"
            :key="dict.dictType"
            :label="dict.dictName"
            :value="dict.dictType"
          />
        </ElSelect>
      </template>
    </ElTableColumn>
  </ElTable>
</template>

<script setup lang="ts">
  import { ElCheckbox, ElInput, ElOption, ElSelect, ElTable, ElTableColumn } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import type { GenTableColumnItem } from '@/api/generator/gen-external'
  import type { SysDictType } from '@/api/dict/type'

  defineProps<{
    columns: GenTableColumnItem[]
    dictOptions: SysDictType[]
    maxHeight?: number | string
  }>()

  const { t } = useI18n()
</script>
