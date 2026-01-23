<!-- 个人中心页面 -->
<template>
  <div class="w-full h-full p-0 bg-transparent border-none shadow-none">
    <div v-loading="loading" class="relative flex-b mt-2.5 max-md:block max-md:mt-1">
      <div class="w-112 mr-5 max-md:w-full max-md:mr-0">
        <div class="art-card-sm relative p-9 pb-6 overflow-hidden text-center">
          <img class="absolute top-0 left-0 w-full h-50 object-cover" :src="bgImage" />
          <img
            class="relative z-10 w-20 h-20 mt-30 mx-auto object-cover border-2 rounded-full transition-colors"
            :class="isDark ? 'border-g-600' : 'border-white'"
            :src="userDetail.avatar || defaultAvatar"
            :alt="userDetail.userName || ''"
          />
          <h2
            class="mt-5 text-xl font-normal transition-colors"
            :class="isDark ? 'text-g-100' : 'text-g-900'"
          >
            {{ userDetail.userName || userInfo.user?.username }}
          </h2>
          <p class="mt-5 text-sm transition-colors" :class="isDark ? 'text-g-400' : 'text-g-600'">
            {{ userDetail.remark || '专注于用户体验跟视觉设计' }}
          </p>

          <div class="w-75 mx-auto mt-7.5 text-left">
            <div class="mt-2.5 transition-colors" v-if="userDetail.email">
              <ArtSvgIcon
                icon="ri:mail-line"
                class="transition-colors"
                :class="isDark ? 'text-g-500' : 'text-g-700'"
              />
              <span
                class="ml-2 text-sm transition-colors"
                :class="isDark ? 'text-g-300' : 'text-g-700'"
              >
                {{ userDetail.email }}
              </span>
            </div>
            <div class="mt-2.5 transition-colors" v-if="userDetail.nickName">
              <ArtSvgIcon
                icon="ri:user-3-line"
                class="transition-colors"
                :class="isDark ? 'text-g-500' : 'text-g-700'"
              />
              <span
                class="ml-2 text-sm transition-colors"
                :class="isDark ? 'text-g-300' : 'text-g-700'"
              >
                {{ userDetail.nickName }}
              </span>
            </div>
            <div class="mt-2.5 transition-colors" v-if="userDetail.phonenumber">
              <ArtSvgIcon
                icon="ri:phone-line"
                class="transition-colors"
                :class="isDark ? 'text-g-500' : 'text-g-700'"
              />
              <span
                class="ml-2 text-sm transition-colors"
                :class="isDark ? 'text-g-300' : 'text-g-700'"
              >
                {{ userDetail.phonenumber }}
              </span>
            </div>
            <div class="mt-2.5 transition-colors" v-if="userDetail.roleName">
              <ArtSvgIcon
                icon="ri:dribbble-fill"
                class="transition-colors"
                :class="isDark ? 'text-g-500' : 'text-g-700'"
              />
              <span
                class="ml-2 text-sm transition-colors"
                :class="isDark ? 'text-g-300' : 'text-g-700'"
              >
                {{ userDetail.roleName }}
              </span>
            </div>
          </div>

          <div class="mt-10" v-if="userDetail.userRoles && userDetail.userRoles.length > 0">
            <h3
              class="text-sm font-medium transition-colors"
              :class="isDark ? 'text-g-200' : 'text-g-800'"
            >
              角色
            </h3>
            <div class="flex flex-wrap justify-center mt-3.5">
              <div
                v-for="role in userDetail.userRoles"
                :key="role"
                class="py-1 px-1.5 mr-2.5 mb-2.5 text-xs border rounded transition-colors"
                :class="
                  isDark ? 'border-g-500 text-g-300 bg-g-800/50' : 'border-g-300 text-g-700 bg-g-50'
                "
              >
                {{ role }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="flex-1 overflow-hidden max-md:w-full max-md:mt-3.5">
        <div class="art-card-sm">
          <h1
            class="p-4 text-xl font-normal border-b transition-colors"
            :class="isDark ? 'border-g-500 text-g-100' : 'border-g-300 text-g-900'"
          >
            基本设置
          </h1>

          <ElForm
            :model="form"
            class="box-border p-5 [&>.el-row_.el-form-item]:w-[calc(50%-10px)] [&>.el-row_.el-input]:w-full [&>.el-row_.el-select]:w-full"
            ref="ruleFormRef"
            :rules="rules"
            label-width="86px"
            label-position="top"
          >
            <ElRow>
              <ElFormItem label="用户名" prop="userName">
                <ElInput v-model="form.userName" :disabled="true" />
              </ElFormItem>
              <ElFormItem label="性别" prop="sex" class="ml-5">
                <ElSelect v-model="form.sex" placeholder="请选择性别" :disabled="!isEdit">
                  <ElOption
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElRow>

            <ElRow>
              <ElFormItem label="昵称" prop="nickName">
                <ElInput v-model="form.nickName" :disabled="!isEdit" />
              </ElFormItem>
              <ElFormItem label="邮箱" prop="email" class="ml-5">
                <ElInput v-model="form.email" :disabled="!isEdit" />
              </ElFormItem>
            </ElRow>

            <ElRow>
              <ElFormItem label="手机" prop="phonenumber">
                <ElInput v-model="form.phonenumber" :disabled="!isEdit" />
              </ElFormItem>
              <ElFormItem label="头像" prop="avatar" class="ml-5">
                <art-avatar-upload
                  v-model="form.avatar"
                  :userId="String(userDetail.userId || (userInfo as any)?.userId || '')"
                  :auto-upload="isEdit"
                  :show-actions="isEdit"
                  :size="120"
                />
              </ElFormItem>
            </ElRow>

            <ElFormItem label="个人介绍" prop="remark" class="h-32">
              <ElInput type="textarea" :rows="4" v-model="form.remark" :disabled="!isEdit" />
            </ElFormItem>

            <div class="flex-c justify-end [&_.el-button]:w-27.5!">
              <ElButton v-if="!isEdit" type="primary" class="w-22.5" v-ripple @click="edit">
                编辑
              </ElButton>
              <template v-else>
                <ElButton class="w-22.5 mr-2.5" v-ripple @click="cancelEdit">取消</ElButton>
                <ElButton
                  type="primary"
                  class="w-22.5"
                  v-ripple
                  @click="saveUserInfo"
                  :loading="saving"
                >
                  保存
                </ElButton>
              </template>
            </div>
          </ElForm>
        </div>

        <div class="art-card-sm my-5">
          <h1
            class="p-4 text-xl font-normal border-b transition-colors"
            :class="isDark ? 'border-g-500 text-g-100' : 'border-g-300 text-g-900'"
          >
            更改密码
          </h1>

          <ElForm
            :model="pwdForm"
            class="box-border p-5"
            label-width="86px"
            label-position="top"
            ref="pwdFormRef"
            :rules="pwdRules"
          >
            <ElFormItem label="新密码" prop="newPassword">
              <ElInput
                v-model="pwdForm.newPassword"
                type="password"
                :disabled="!isEditPwd"
                show-password
                placeholder="请输入新密码"
              />
            </ElFormItem>

            <ElFormItem label="确认新密码" prop="confirmPassword">
              <ElInput
                v-model="pwdForm.confirmPassword"
                type="password"
                :disabled="!isEditPwd"
                show-password
                placeholder="请再次输入新密码"
              />
            </ElFormItem>

            <div class="flex-c justify-end [&_.el-button]:w-27.5!">
              <ElButton v-if="!isEditPwd" type="primary" class="w-22.5" v-ripple @click="editPwd">
                编辑
              </ElButton>
              <template v-else>
                <ElButton class="w-22.5 mr-2.5" v-ripple @click="cancelEditPwd">取消</ElButton>
                <ElButton
                  type="primary"
                  class="w-22.5"
                  v-ripple
                  @click="savePassword"
                  :loading="savingPwd"
                >
                  保存
                </ElButton>
              </template>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '@/store/modules/user'
  import { fetchGetUserById, fetchUpdateUser, fetchResetUserPassword } from '@/api/user/user'
  import { fetchGetUserInfo } from '@/api/auth'
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import defaultAvatarImg from '@imgs/user/avatar.webp'
  import bgImageImg from '@imgs/user/bg.webp'
  import { useSettingStore } from '@/store/modules/setting'
  import ArtAvatarUpload from '@/components/core/media/art-avatar-upload/index.vue'

  defineOptions({ name: 'UserCenter' })

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 默认头像和背景图片
  const defaultAvatar = defaultAvatarImg
  const bgImage = bgImageImg

  const userStore = useUserStore()
  const userInfo = computed(() => userStore.getUserInfo)

  const isEdit = ref(false)
  const isEditPwd = ref(false)
  const loading = ref(false)
  const saving = ref(false)
  const savingPwd = ref(false)
  const date = ref('')
  const ruleFormRef = ref<FormInstance>()
  const pwdFormRef = ref<FormInstance>()

  /**
   * 用户详情数据
   */
  const userDetail = ref<Partial<Api.SystemManage.UserListItem>>({})

  /**
   * 用户信息表单
   */
  const form = reactive({
    userId: 0,
    userName: '',
    nickName: '',
    email: '',
    phonenumber: '',
    avatar: '',
    sex: '0',
    remark: ''
  })

  /**
   * 保存的表单数据（用于取消编辑时恢复）
   */
  const savedFormData = ref({ ...form })

  /**
   * 密码修改表单
   */
  const pwdForm = reactive({
    newPassword: '',
    confirmPassword: ''
  })

  /**
   * 保存的密码表单数据（用于取消编辑时恢复）
   */
  const savedPwdFormData = ref({ ...pwdForm })

  /**
   * 表单验证规则
   */
  const rules = reactive<FormRules>({
    nickName: [{ min: 0, max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' }],
    email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
    phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
    sex: [{ required: true, message: '请选择性别', trigger: 'blur' }]
  })

  /**
   * 密码表单验证规则
   */
  const pwdRules = reactive<FormRules>({
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 5, max: 20, message: '密码长度必须在5到20个字符之间', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入新密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value !== pwdForm.newPassword) {
            callback(new Error('两次输入的密码不一致'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  })

  /**
   * 性别选项
   */
  const options = [
    { value: '0', label: '男' },
    { value: '1', label: '女' },
    { value: '2', label: '未知' }
  ]

  /**
   * 获取用户详情
   */
  const getUserDetail = async () => {
    let currentUserId: number | undefined = userInfo.value?.userId

    // 打印当前用户信息，用于调试
    console.log('当前用户信息:', userInfo.value)
    console.log('用户信息类型:', typeof userInfo.value)
    console.log('用户信息键:', userInfo.value ? Object.keys(userInfo.value) : 'null')
    console.log('userId 值:', currentUserId)
    console.log('userId 类型:', typeof currentUserId)

    // 如果用户信息中没有 userId，尝试重新获取用户信息
    if (!currentUserId) {
      loading.value = true
      try {
        console.log('用户信息中没有 userId，尝试重新获取用户信息...')
        const responseData = await fetchGetUserInfo()
        console.log('重新获取的完整响应数据:', responseData)
        console.log('响应数据类型:', typeof responseData)
        console.log('响应数据键:', responseData ? Object.keys(responseData) : 'null')

        // 后端返回的数据结构是 { user: {...}, roles: [...], permissions: [...] }
        // 用户信息在 user 字段中
        const userInfoData = (responseData as any)?.user || responseData
        console.log('提取的用户信息:', userInfoData)
        console.log('用户信息中的 userId:', userInfoData?.userId)

        if (userInfoData?.userId) {
          // 构造符合 Api.Auth.UserInfo 格式的数据
          const permissions = (responseData as any)?.permissions || []
          const roles = (responseData as any)?.roles || []
          const formattedUserInfo: Api.Auth.UserInfo = {
            userId: userInfoData.userId,
            userName: userInfoData.userName || '',
            nickName: userInfoData.nickName,
            email: userInfoData.email || '',
            avatar: userInfoData.avatar,
            buttons: permissions.map((p: any) => p.perms || ''),
            roles: roles.map((r: any) => r.roleKey || r.roleName || '')
          }
          // 更新 store 中的用户信息
          userStore.setUserInfo(formattedUserInfo)
          // 直接使用获取到的 userId，不依赖computed更新
          currentUserId = userInfoData.userId
          console.log('用户信息已更新，userId:', currentUserId)
        } else {
          ElMessage.warning('未获取到用户ID，请重新登录')
          console.error('重新获取的用户信息中没有 userId:', userInfoData)
          console.error('完整响应数据:', responseData)
          loading.value = false
          return
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败，请重新登录')
        loading.value = false
        return
      }
    }

    // 确保有 userId 后再获取详情
    if (!currentUserId) {
      ElMessage.warning('未获取到用户ID，请重新登录')
      console.error('最终检查：仍然没有 userId')
      loading.value = false
      return
    }

    loading.value = true
    try {
      console.log('使用 userId 获取用户详情:', currentUserId)
      const res = await fetchGetUserById(currentUserId)
      console.log('获取到的用户详情:', res)
      if (res) {
        userDetail.value = res
        // 填充表单数据
        Object.assign(form, {
          userId: res.userId || 0,
          userName: res.userName || '',
          nickName: res.nickName || '',
          email: res.email || '',
          phonenumber: res.phonenumber || '',
          avatar: res.avatar || '',
          sex: res.sex || '0',
          remark: res.remark || ''
        })
        // 保存当前表单数据
        savedFormData.value = { ...form }
      }
    } catch (error) {
      console.error('获取用户详情失败:', error)
      ElMessage.error('获取用户详情失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 保存用户信息
   */
  const saveUserInfo = async () => {
    if (!ruleFormRef.value) return

    await ruleFormRef.value.validate(async (valid) => {
      if (valid) {
        saving.value = true
        try {
          const updateData: Api.SystemManage.UserListItem = {
            ...form,
            userId: userDetail.value.userId!,
            status: userDetail.value.status || '0'
          }
          await fetchUpdateUser(updateData)
          ElMessage.success('保存成功')
          // 重新获取用户详情（包含最新头像）
          await getUserDetail()
          // 同步头像到 store，便于顶部下拉立即展示最新头像
          // 使用 userDetail.avatar（从服务器获取的最新值）而不是 form.avatar（可能还未同步）
          userStore.setUserInfo({
            avatar: userDetail.value.avatar || form.avatar,
            avatarUpdatedAt: Date.now()
          })
          isEdit.value = false
        } catch (error) {
          console.error('保存用户信息失败:', error)
          ElMessage.error('保存用户信息失败')
        } finally {
          saving.value = false
        }
      }
    })
  }

  /**
   * 取消编辑
   */
  const cancelEdit = () => {
    // 恢复保存的表单数据
    Object.assign(form, savedFormData.value)
    isEdit.value = false
    ruleFormRef.value?.clearValidate()
  }

  /**
   * 切换用户信息编辑状态
   */
  const edit = () => {
    isEdit.value = true
    // 保存当前表单数据
    savedFormData.value = { ...form }
  }

  /**
   * 切换密码编辑状态
   */
  const editPwd = () => {
    isEditPwd.value = true
    // 保存当前密码表单数据
    savedPwdFormData.value = { ...pwdForm }
  }

  /**
   * 取消密码编辑
   */
  const cancelEditPwd = () => {
    // 恢复保存的密码表单数据
    Object.assign(pwdForm, savedPwdFormData.value)
    isEditPwd.value = false
    pwdFormRef.value?.clearValidate()
  }

  /**
   * 保存密码
   */
  const savePassword = async () => {
    if (!pwdFormRef.value) return

    await pwdFormRef.value.validate(async (valid) => {
      if (valid) {
        if (!userDetail.value?.userId) {
          ElMessage.warning('未获取到用户ID')
          return
        }

        savingPwd.value = true
        try {
          await fetchResetUserPassword(userDetail.value.userId, pwdForm.newPassword)
          ElMessage.success('密码修改成功')
          // 清空密码表单
          Object.assign(pwdForm, {
            newPassword: '',
            confirmPassword: ''
          })
          savedPwdFormData.value = { ...pwdForm }
          isEditPwd.value = false
          pwdFormRef.value?.clearValidate()
        } catch (error) {
          console.error('修改密码失败:', error)
          ElMessage.error('修改密码失败')
        } finally {
          savingPwd.value = false
        }
      }
    })
  }

  /**
   * 根据当前时间获取问候语
   */
  const getDate = () => {
    const h = new Date().getHours()

    if (h >= 6 && h < 9) date.value = '早上好'
    else if (h >= 9 && h < 11) date.value = '上午好'
    else if (h >= 11 && h < 13) date.value = '中午好'
    else if (h >= 13 && h < 18) date.value = '下午好'
    else if (h >= 18 && h < 24) date.value = '晚上好'
    else date.value = '很晚了，早点睡'
  }

  onMounted(() => {
    getDate()
    getUserDetail()
  })
</script>
