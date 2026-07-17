<template>
  <ElDialog
    v-model="visible"
    :title="t('system.role.selectUser')"
    width="60%"
    align-center
    @close="handleClose"
  >
    <div class="search-bar">
      <ElForm :model="searchForm" inline>
        <ElFormItem :label="t('system.user.userName')">
          <ElInput
            v-model="searchForm.userName"
            :placeholder="t('system.user.userNamePlaceholder')"
            clearable
            style="width: 200px"
          />
        </ElFormItem>
        <ElFormItem :label="t('system.user.phone')">
          <ElInput
            v-model="searchForm.phonenumber"
            :placeholder="t('system.user.phonePlaceholder')"
            clearable
            style="width: 200px"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" :icon="Search" @click="handleSearch">
            {{ t('table.searchBar.search') }}
          </ElButton>
          <ElButton :icon="Refresh" @click="handleReset">{{ t('table.searchBar.reset') }}</ElButton>
        </ElFormItem>
      </ElForm>
    </div>

    <ElTable
      ref="tableRef"
      v-loading="loading"
      :data="tableData"
      @selection-change="handleSelectionChange"
      style="margin-top: 20px"
    >
      <ElTableColumn type="selection" width="55" />
      <ElTableColumn prop="userName" :label="t('system.user.userName')" width="120" />
      <ElTableColumn prop="nickName" :label="t('system.user.nickName')" width="120" />
      <ElTableColumn prop="email" :label="t('system.user.email')" width="180" />
      <ElTableColumn prop="phonenumber" :label="t('system.user.phone')" width="150" />
      <ElTableColumn prop="status" :label="t('common.status')" width="100">
        <template #default="{ row }">
          <ElTag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? t('common.normal') : t('common.disabled') }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="createTime" :label="t('common.createTime')" width="180" />
    </ElTable>
    <div class="pagination-wrapper">
      <ElPagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="handleClose">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleConfirm">{{ t('common.confirm') }}</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import { Refresh, Search } from '@element-plus/icons-vue'
  import { useI18n } from 'vue-i18n'
  import { fetchAssignUser, fetchGetUserListNotInByRoleId } from '@/api/role/role'
  import { handleMutationError } from '@/utils/http/mutation'

  interface Props {
    modelValue: boolean
    roleId?: string | number
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'close'): void
    (e: 'confirm', userIds: number[]): void
  }

  const props = withDefaults(defineProps<Props>(), {
    roleId: undefined
  })

  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const visible = computed({
    get: () => props.modelValue,
    set: (val: boolean) => emit('update:modelValue', val)
  })

  const tableRef = ref()
  const loading = ref(false)

  const searchForm = ref({
    roleId: props.roleId,
    userName: undefined as string | undefined,
    phonenumber: undefined as string | undefined,
    pageNum: 1,
    pageSize: 10
  })

  const tableData = ref<Api.SystemManage.UserListItem[]>([])
  const selectedUsers = ref<Api.SystemManage.UserListItem[]>([])

  const pagination = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0
  })

  const fetchUserList = async () => {
    if (props.roleId == null) {
      tableData.value = []
      return
    }
    try {
      loading.value = true
      searchForm.value.pageNum = pagination.value.pageNum
      searchForm.value.pageSize = pagination.value.pageSize
      const response = await fetchGetUserListNotInByRoleId({
        ...searchForm.value,
        roleId: props.roleId
      })
      tableData.value = (response as any)?.rows || ([] as Api.SystemManage.UserListItem[])
      if ((response as any)?.total !== undefined) {
        pagination.value.total = (response as any).total
      }
      if ((response as any)?.pageNum !== undefined) {
        pagination.value.pageNum = (response as any).pageNum
      }
      if ((response as any)?.pageSize !== undefined) {
        pagination.value.pageSize = (response as any).pageSize
      }
    } catch (error) {
      console.error('fetch user list failed:', error)
      handleMutationError(error, t('system.role.loadUserListFail'))
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    pagination.value.pageNum = 1
    fetchUserList()
  }

  const handleReset = () => {
    searchForm.value.userName = undefined
    searchForm.value.phonenumber = undefined
    pagination.value.pageNum = 1
    fetchUserList()
  }

  const handleSizeChange = (size: number) => {
    pagination.value.pageSize = size
    pagination.value.pageNum = 1
    fetchUserList()
  }

  const handleCurrentChange = (page: number) => {
    pagination.value.pageNum = page
    fetchUserList()
  }

  const handleSelectionChange = (selection: Api.SystemManage.UserListItem[]) => {
    selectedUsers.value = selection
  }

  const handleConfirm = async () => {
    if (selectedUsers.value.length === 0) {
      ElMessage.warning(t('system.role.selectUserRequired'))
      return
    }
    if (props.roleId == null) {
      ElMessage.warning(t('system.role.roleIdInvalid'))
      return
    }
    const userIds = selectedUsers.value.map((user: Api.SystemManage.UserListItem) => user.userId)
    const UserRoleDTO = {
      roleId: props.roleId,
      userIds: userIds
    }
    await fetchAssignUser(UserRoleDTO)
    ElMessage.success(t('system.role.assignSuccess'))
    emit('confirm', userIds)
    handleClose()
  }

  const handleClose = () => {
    visible.value = false
    emit('close')
    searchForm.value.userName = undefined
    searchForm.value.phonenumber = undefined
    pagination.value.pageNum = 1
    pagination.value.pageSize = 10
    selectedUsers.value = []
    tableData.value = []
    if (tableRef.value) {
      tableRef.value.clearSelection()
    }
  }

  watch(
    () => visible.value,
    (newVal: boolean) => {
      if (newVal) {
        fetchUserList()
      }
    },
    { immediate: true }
  )
</script>

<style scoped lang="scss">
  .search-bar {
    padding: 20px 0;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .dialog-footer {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
  }
</style>
