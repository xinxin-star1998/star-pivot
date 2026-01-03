<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="用户名" prop="username">
        <ElInput v-model="formData.userName" placeholder="请输入用户名" />
      </ElFormItem>
      <ElFormItem label="用户密码" prop="username">
        <ElInput v-model="formData.password" placeholder="请输入用户名" />
      </ElFormItem>
      <ElFormItem label="用户昵称" prop="nickName">
        <ElInput v-model="formData.nickName" placeholder="请输入用户昵称" />
      </ElFormItem>
      <ElFormItem label="邮箱" prop="email">
        <ElInput v-model="formData.email" placeholder="请输入邮箱" />
      </ElFormItem>

      <ElFormItem label="手机号" prop="phone">
        <ElInput v-model="formData.phonenumber" placeholder="请输入手机号" />
      </ElFormItem>
      <ElFormItem label="性别" prop="gender">
        <ElRadioGroup v-model="formData.sex">
          <ElRadio label="0">男</ElRadio>
          <ElRadio label="1">女</ElRadio>
          <ElRadio label="2">未知</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio label="0">启用</ElRadio>
          <ElRadio label="1">禁用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="角色" prop="role">
        <ElSelect v-model="formData.roleIds" multiple>
          <ElOption
            v-for="role in roleList"
            :key="role.roleCode"
            :value="role.roleCode"
            :label="role.roleName"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="岗位" prop="post">
        <ElSelect v-model="formData.postIds" multiple>
          <ElOption
            v-for="post in postList"
            :key="post.postId"
            :value="post.postId"
            :label="post.postName"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="部门" prop="deptId">
        <ElTreeSelect
          v-model="formData.deptId"
          :data="deptTreeData"
          :props="deptTreeProps"
          placeholder="请选择部门"
          clearable
          check-strictly
          :render-after-expand="false"
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElTreeSelect } from 'element-plus'
  import { fetchGetRoleSelect } from '@/api/role/role'
  import { fetchGetPostSelect } from '@/api/post/post'
  import { fetchGetDeptTree, type SysDept } from '@/api/dept/dept'

  // 角色列表项类型（扩展 RoleListItem，添加 roleCode 字段）
  type RoleOption = Api.SystemManage.RoleListItem & { roleCode: string }

  interface Props {
    visible: boolean
    type: string
    userData?: Partial<Api.SystemManage.UserListItem>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 角色列表数据
  const roleList = ref<RoleOption[]>([])
  // 岗位列表数据
  const postList = ref<Api.post.PostBo[]>([])
  // 部门树数据
  const deptTreeData = ref<SysDept[]>([])
  // 部门树配置
  const deptTreeProps = {
    value: 'deptId',
    label: 'deptName',
    children: 'children'
  }
  // 对话框显示控制
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  // 表单实例
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive({
    deptId: null,
    userName: '',
    nickName: '',
    email: '',
    avatar: '',
    password: '',
    phonenumber: '',
    status: '0',
    sex: '0',
    remark: '',
    roleIds: [] as string[],
    postIds: [] as string[]
  })

  // 表单验证规则
  const rules: FormRules = {
    userName: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    phonenumber: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ],
    sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
    roleIds: [{ required: true, message: '请选择角色', trigger: 'blur' }]
  }

  /**
   * 初始化表单数据
   * 根据对话框类型（新增/编辑）填充表单
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.userData
    const row = props.userData

    Object.assign(formData, {
      deptId: isEdit && row ? row.deptId || null : null,
      userName: isEdit && row ? row.userName || '' : '',
      nickName: isEdit && row ? row.nickName || '' : '',
      email: isEdit && row ? row.email || '' : '',
      phonenumber: isEdit && row ? row.phonenumber || '' : '',
      sex: isEdit && row ? row.sex || '0' : '0',
      status: isEdit && row ? row.status || '0' : '0',
      password: '',
      roleIds:
        isEdit && row && Array.isArray(row.userRoles)
          ? row.userRoles.map((role: any) => role.roleId?.toString() || '')
          : [],
      postIds:
        isEdit && row && (row as any).postIds && Array.isArray((row as any).postIds)
          ? (row as any).postIds
          : []
    })
  }

  /**
   * 监听对话框状态变化
   * 当对话框打开时初始化表单数据并清除验证状态
   */
  watch(
    () => [props.visible, props.type, props.userData],
    ([visible]) => {
      if (visible) {
        initFormData()
        getRoleList() // 获取角色列表
        getPostList() // 获取岗位列表
        getDeptTree() // 获取部门树
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  /**
   * 提交表单
   * 验证通过后触发提交事件
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate((valid) => {
      if (valid) {
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
        dialogVisible.value = false
        emit('submit')
      }
    })
  }
  /**
   * 获取角色下拉数据
   */
  const getRoleList = async () => {
    try {
      const res = await fetchGetRoleSelect()
      if (Array.isArray(res) && res.length > 0) {
        // 将 roleKey 映射为 roleCode，以匹配模板中的使用
        roleList.value = res.map((role) => ({
          ...role,
          roleCode: role.roleId?.toString()
        }))
      } else {
        roleList.value = []
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
      roleList.value = []
      ElMessage.error('获取角色列表失败')
    }
  }
  /**
   * 获取岗位下拉数据
   */
  const getPostList = async () => {
    try {
      const res = await fetchGetPostSelect()
      if (Array.isArray(res) && res.length > 0) {
        postList.value = res
      } else {
        postList.value = []
      }
    } catch (error) {
      console.error('获取岗位列表失败:', error)
      postList.value = []
      ElMessage.error('获取岗位列表失败')
    }
  }
  /**
   * 获取部门树数据
   */
  const getDeptTree = async () => {
    try {
      const res = await fetchGetDeptTree()
      if (Array.isArray(res) && res.length > 0) {
        deptTreeData.value = res
      } else {
        deptTreeData.value = []
      }
    } catch (error) {
      console.error('获取部门树失败:', error)
      deptTreeData.value = []
      ElMessage.error('获取部门树失败')
    }
  }
</script>
