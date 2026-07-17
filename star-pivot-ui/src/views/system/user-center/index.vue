<!-- 个人中心页面 -->
<template>
  <div class="w-full h-full p-0 bg-transparent border-none shadow-none">
    <div v-loading="loading" class="relative flex-b mt-2.5 max-md:block max-md:mt-1">
      <div class="w-112 mr-5 max-md:w-full max-md:mr-0">
        <div class="art-card-sm relative p-0 overflow-hidden">
          <div class="relative h-48 bg-gradient-to-r from-blue-500 to-purple-600">
            <img
              class="absolute top-0 left-0 w-full h-full object-cover opacity-80"
              :src="bgImage"
            />
            <div class="absolute inset-0 bg-gradient-to-b from-transparent to-black/30"></div>
          </div>
          <div class="relative px-6 pb-8 -mt-16 text-center">
            <div class="relative inline-block">
              <ArtAvatarUpload
                class="user-center-top-avatar transition-all duration-300 hover:scale-105"
                :model-value="topAvatarDisplayUrl"
                :user-id="form.userId"
                :size="112"
                :auto-upload="true"
                use-presigned-url
                @update:model-value="(val) => (topAvatarDisplayUrl = val)"
                @success="handleAvatarUploadSuccess"
                @error="handleAvatarUploadError"
              />
              <div
                class="absolute bottom-1 right-1 w-6 h-6 rounded-full border-2 transition-colors"
                :class="isDark ? 'bg-green-500 border-g-700' : 'bg-green-500 border-white'"
              ></div>
            </div>
            <h2
              class="mt-4 text-2xl font-semibold transition-colors"
              :class="isDark ? 'text-g-100' : 'text-g-900'"
            >
              {{ userDetail.userName || userInfo.user?.username }}
            </h2>
            <p class="mt-2 text-sm transition-colors" :class="isDark ? 'text-g-400' : 'text-g-600'">
              {{ userDetail.remark || t('system.userCenter.defaultRemark') }}
            </p>

            <div class="mt-6 space-y-3">
              <div
                class="flex items-center justify-center p-3 rounded-lg transition-colors"
                v-if="userDetail.email"
                :class="isDark ? 'bg-g-800/50 hover:bg-g-800/70' : 'bg-g-100 hover:bg-g-200'"
              >
                <ArtSvgIcon
                  icon="ri:mail-line"
                  class="text-lg transition-colors"
                  :class="isDark ? 'text-blue-400' : 'text-blue-600'"
                />
                <span
                  class="ml-3 text-sm font-medium transition-colors"
                  :class="isDark ? 'text-g-200' : 'text-g-700'"
                >
                  {{ userDetail.email }}
                </span>
              </div>
              <div
                class="flex items-center justify-center p-3 rounded-lg transition-colors"
                v-if="userDetail.nickName"
                :class="isDark ? 'bg-g-800/50 hover:bg-g-800/70' : 'bg-g-100 hover:bg-g-200'"
              >
                <ArtSvgIcon
                  icon="ri:user-3-line"
                  class="text-lg transition-colors"
                  :class="isDark ? 'text-purple-400' : 'text-purple-600'"
                />
                <span
                  class="ml-3 text-sm font-medium transition-colors"
                  :class="isDark ? 'text-g-200' : 'text-g-700'"
                >
                  {{ userDetail.nickName }}
                </span>
              </div>
              <div
                class="flex items-center justify-center p-3 rounded-lg transition-colors"
                v-if="userDetail.phonenumber"
                :class="isDark ? 'bg-g-800/50 hover:bg-g-800/70' : 'bg-g-100 hover:bg-g-200'"
              >
                <ArtSvgIcon
                  icon="ri:phone-line"
                  class="text-lg transition-colors"
                  :class="isDark ? 'text-green-400' : 'text-green-600'"
                />
                <span
                  class="ml-3 text-sm font-medium transition-colors"
                  :class="isDark ? 'text-g-200' : 'text-g-700'"
                >
                  {{ userDetail.phonenumber }}
                </span>
              </div>
              <div
                class="flex items-center justify-center p-3 rounded-lg transition-colors"
                v-if="userDetail.roleName"
                :class="isDark ? 'bg-g-800/50 hover:bg-g-800/70' : 'bg-g-100 hover:bg-g-200'"
              >
                <ArtSvgIcon
                  icon="ri:dribbble-fill"
                  class="text-lg transition-colors"
                  :class="isDark ? 'text-orange-400' : 'text-orange-600'"
                />
                <span
                  class="ml-3 text-sm font-medium transition-colors"
                  :class="isDark ? 'text-g-200' : 'text-g-700'"
                >
                  {{ userDetail.roleName }}
                </span>
              </div>
            </div>

            <div class="mt-6" v-if="userDetail.userRoles && userDetail.userRoles.length > 0">
              <h3
                class="mb-3 text-sm font-semibold transition-colors"
                :class="isDark ? 'text-g-200' : 'text-g-800'"
              >
                {{ t('system.user.role') }}
              </h3>
              <div class="flex flex-wrap justify-center gap-2">
                <div
                  v-for="role in userDetail.userRoles"
                  :key="role"
                  class="px-3 py-1.5 text-sm font-medium rounded-full transition-all duration-300 hover:scale-105"
                  :class="
                    isDark
                      ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-lg'
                      : 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-md'
                  "
                >
                  {{ role }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="flex-1 overflow-hidden max-md:w-full max-md:mt-3.5">
        <div class="art-card-sm">
          <div class="px-6 pt-4 pb-2">
            <h1
              class="text-xl font-semibold transition-colors"
              :class="isDark ? 'text-g-100' : 'text-g-900'"
            >
              {{ t('system.userCenter.basicSettings') }}
            </h1>
            <p class="mt-1 text-sm transition-colors" :class="isDark ? 'text-g-400' : 'text-g-500'">
              {{ t('system.userCenter.basicSettingsHint') }}
            </p>
          </div>

          <ElTabs v-model="activeTab" class="px-6 pb-6 user-center-tabs">
            <ElTabPane :label="t('system.userCenter.basicInfo')" name="basic">
              <ElForm
                :model="form"
                class="box-border pt-4 space-y-6"
                label-width="86px"
                label-position="top"
              >
                <ElRow class="gap-6">
                  <ElFormItem :label="t('system.user.userName')" class="flex-1">
                    <ElInput v-model="form.userName" class="transition-all duration-300">
                      <template #prefix>
                        <ArtSvgIcon
                          icon="ri:user-line"
                          class="transition-colors"
                          :class="isDark ? 'text-g-500' : 'text-g-400'"
                        />
                      </template>
                    </ElInput>
                  </ElFormItem>
                  <ElFormItem :label="t('system.userCenter.sex')" class="flex-1">
                    <ElSelect v-model="form.sex" class="w-full transition-all duration-300">
                      <ElOption
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </ElSelect>
                  </ElFormItem>
                </ElRow>

                <ElRow class="gap-6">
                  <ElFormItem :label="t('system.userCenter.nickName')" class="flex-1">
                    <ElInput v-model="form.nickName" class="transition-all duration-300">
                      <template #prefix>
                        <ArtSvgIcon
                          icon="ri:user-smile-line"
                          class="transition-colors"
                          :class="isDark ? 'text-g-500' : 'text-g-400'"
                        />
                      </template>
                    </ElInput>
                  </ElFormItem>
                  <ElFormItem :label="t('system.userCenter.email')" class="flex-1">
                    <ElInput v-model="form.email" class="transition-all duration-300">
                      <template #prefix>
                        <ArtSvgIcon
                          icon="ri:mail-line"
                          class="transition-colors"
                          :class="isDark ? 'text-g-500' : 'text-g-400'"
                        />
                      </template>
                    </ElInput>
                  </ElFormItem>
                </ElRow>

                <ElRow class="gap-6">
                  <ElFormItem :label="t('system.userCenter.phone')" class="flex-1">
                    <ElInput v-model="form.phonenumber" class="transition-all duration-300">
                      <template #prefix>
                        <ArtSvgIcon
                          icon="ri:phone-line"
                          class="transition-colors"
                          :class="isDark ? 'text-g-500' : 'text-g-400'"
                        />
                      </template>
                    </ElInput>
                  </ElFormItem>
                </ElRow>

                <ElFormItem :label="t('system.userCenter.intro')" class="h-32">
                  <ElInput
                    type="textarea"
                    :rows="4"
                    v-model="form.remark"
                    class="transition-all duration-300"
                  >
                    <template #prefix>
                      <ArtSvgIcon
                        icon="ri:file-text-line"
                        class="transition-colors"
                        :class="isDark ? 'text-g-500' : 'text-g-400'"
                      />
                    </template>
                  </ElInput>
                </ElFormItem>
                <ElFormItem>
                  <ElButton type="primary" @click="submitBasicProfile">{{
                    t('common.save')
                  }}</ElButton>
                  <ElButton plain @click="resetBasicProfile">{{ t('common.reset') }}</ElButton>
                </ElFormItem>
              </ElForm>
            </ElTabPane>

            <ElTabPane :label="t('system.userCenter.updatePwd')" name="password">
              <ElForm
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                class="pt-4"
                label-width="90px"
                label-position="left"
              >
                <ElFormItem :label="t('system.userCenter.oldPassword')" prop="oldPassword">
                  <ElInput
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    autocomplete="current-password"
                    :placeholder="t('system.userCenter.oldPwdPlaceholder')"
                  />
                </ElFormItem>
                <ElFormItem :label="t('system.userCenter.newPassword')" prop="newPassword">
                  <ElInput
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    autocomplete="new-password"
                    :placeholder="t('system.userCenter.newPwdPlaceholder')"
                  />
                </ElFormItem>
                <ElFormItem :label="t('system.userCenter.confirmPassword')" prop="confirmPassword">
                  <ElInput
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    autocomplete="new-password"
                    :placeholder="t('system.userCenter.confirmPwdPlaceholder')"
                  />
                </ElFormItem>
                <ElFormItem>
                  <ElButton type="primary" @click="submitPassword">{{ t('common.save') }}</ElButton>
                </ElFormItem>
              </ElForm>
            </ElTabPane>
            <ElTabPane :label="t('system.userCenter.sessionManagement')" name="session">
              <div class="session-management pt-4">
                <!-- 操作按钮 -->
                <div class="flex items-center gap-3 mb-4">
                  <el-button
                    type="danger"
                    :loading="logoutAllLoading"
                    :disabled="sessionList.length === 0"
                    @click="handleLogoutAll"
                  >
                    <el-icon><SwitchButton /></el-icon>
                    {{ t('system.userCenter.forceLogoutAll') }}
                  </el-button>
                  <el-button :loading="sessionLoading" @click="loadSessions">
                    <el-icon><Refresh /></el-icon>
                    {{ t('file.refresh') }}
                  </el-button>
                  <el-tag v-if="sessionList.length > 0" type="info" size="small" class="ml-2">
                    {{ t('system.userCenter.activeSessionCount', { count: sessionList.length }) }}
                  </el-tag>
                </div>

                <!-- 会话列表 -->
                <el-table
                  v-loading="sessionLoading"
                  :data="sessionList"
                  style="width: 100%"
                  :empty-text="t('system.userCenter.noActiveSession')"
                  :row-class-name="sessionRowClassName"
                >
                  <el-table-column
                    type="index"
                    :label="t('system.user.index')"
                    width="60"
                    align="center"
                  />
                  <el-table-column :label="t('system.userCenter.deviceInfo')" min-width="200">
                    <template #default="{ row }">
                      <div class="device-info">
                        <span class="font-medium">{{ getDeviceText(row) }}</span>
                        <div class="text-xs text-g-500">
                          <el-icon><Location /></el-icon>
                          {{ row.ipaddr || t('system.userCenter.unknownIp') }}
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="t('monitor.online.browser')"
                    prop="browser"
                    min-width="120"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    :label="t('monitor.online.os')"
                    prop="os"
                    min-width="100"
                    show-overflow-tooltip
                  />
                  <el-table-column :label="t('monitor.online.loginTime')" min-width="160">
                    <template #default="{ row }">
                      {{ formatDateTime(row.createdAt) }}
                    </template>
                  </el-table-column>
                  <el-table-column :label="t('system.userCenter.lastAccess')" min-width="160">
                    <template #default="{ row }">
                      {{ formatDateTime(row.lastAccessTime) }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="t('system.userCenter.sessionDuration')"
                    width="110"
                    align="center"
                  >
                    <template #default="{ row }">
                      <el-tag type="info" size="small">{{ row.sessionDuration || '-' }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="t('system.userCenter.currentSession')"
                    width="90"
                    align="center"
                  >
                    <template #default="{ row }">
                      <el-tag v-if="isCurrentSession(row)" type="success" size="small">{{
                        t('system.userCenter.current')
                      }}</el-tag>
                      <el-tag v-else type="info" size="small">{{
                        t('system.userCenter.other')
                      }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column
                    :label="t('common.operation')"
                    width="90"
                    fixed="right"
                    align="center"
                  >
                    <template #default="{ row }">
                      <el-button
                        v-if="!isCurrentSession(row)"
                        type="danger"
                        size="small"
                        link
                        :loading="row.logoutLoading"
                        @click="handleLogoutSession(row)"
                      >
                        {{ t('system.userCenter.logout') }}
                      </el-button>
                      <el-text v-else type="info" size="small">-</el-text>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </ElTabPane>
          </ElTabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '@/store/modules/user'
  import {
    fetchGetAvatarPresignedUrl,
    fetchGetUserById,
    fetchUpdateUser,
    fetchUpdateUserPassword
  } from '@/api/user/user'
  import {
    fetchGetUserInfo,
    fetchLogout,
    fetchUserSessions,
    forceLogoutAllSessions,
    forceLogoutSession
  } from '@/api/auth'
  import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
  import { Location, Refresh, SwitchButton } from '@element-plus/icons-vue'
  import ArtAvatarUpload from '@/components/core/media/art-avatar-upload/index.vue'
  import { handleMutationError } from '@/utils/http/mutation'
  import defaultAvatarImg from '@imgs/user/avatar.webp'
  import bgImageImg from '@imgs/user/bg.webp'
  import { useSettingStore } from '@/store/modules/setting'
  import { extractOssObjectPath, needsOssPresignedDisplay } from '@/utils/storage/oss-object-path'
  import dayjs from 'dayjs'
  import { useRoute } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  defineOptions({ name: 'UserCenter' })

  const route = useRoute()
  const { t } = useI18n()

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 默认头像和背景图片
  const defaultAvatar = defaultAvatarImg
  const bgImage = bgImageImg

  const userStore = useUserStore()
  const userInfo = computed(() => userStore.getUserInfo)

  const loading = ref(false)
  const activeTab = ref('basic')

  /**
   * 用户详情数据
   */
  const userDetail = ref<Partial<Api.SystemManage.UserListItem>>({})

  /**
   * 顶部卡片头像展示 URL：若存的是 OSS 私有桶永久地址则用 presigned URL 展示，避免 403
   */
  const topAvatarDisplayUrl = ref(defaultAvatarImg)

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
  const originalForm = reactive({
    userId: 0,
    userName: '',
    nickName: '',
    email: '',
    phonenumber: '',
    avatar: '',
    sex: '0',
    remark: ''
  })

  const passwordFormRef = ref<FormInstance>()
  const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  const passwordRules = computed<FormRules<typeof passwordForm>>(() => ({
    oldPassword: [
      { required: true, message: t('system.userCenter.oldPwdRequired'), trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: t('system.userCenter.newPwdRequired'), trigger: 'blur' },
      { min: 6, max: 20, message: t('system.userCenter.pwdLength'), trigger: 'blur' },
      {
        pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/,
        message: t('system.userCenter.pwdPattern'),
        trigger: 'blur'
      },
      {
        validator: (_rule, value, callback) => {
          if (!value) {
            callback()
            return
          }
          if (value === passwordForm.oldPassword) {
            callback(new Error(t('system.userCenter.pwdSameAsOld')))
            return
          }
          callback()
        },
        trigger: 'blur'
      }
    ],
    confirmPassword: [
      { required: true, message: t('system.userCenter.confirmPwdRequired'), trigger: 'blur' },
      {
        validator: (_rule, value, callback) => {
          if (!value) {
            callback(new Error(t('system.userCenter.confirmPwdRequired')))
            return
          }
          if (value !== passwordForm.newPassword) {
            callback(new Error(t('system.userCenter.pwdNotMatch')))
            return
          }
          callback()
        },
        trigger: 'blur'
      }
    ]
  }))

  /**
   * 性别选项
   */
  const options = computed(() => [
    { value: '0', label: t('system.userCenter.male') },
    { value: '1', label: t('system.userCenter.female') },
    { value: '2', label: t('system.userCenter.unknown') }
  ])

  // ==================== 会话管理 ====================
  const sessionLoading = ref(false)
  const logoutAllLoading = ref(false)
  const sessionList = ref<Array<Api.Auth.DeviceSession & { logoutLoading?: boolean }>>([])

  const loadSessions = async () => {
    sessionLoading.value = true
    try {
      const userId = userStore.getUserInfo?.user?.userId
      if (!userId) {
        ElMessage.warning(t('system.userCenter.notLoggedIn'))
        return
      }
      const res = await fetchUserSessions(userId)
      sessionList.value = (res || []).map((session) => ({
        ...session,
        logoutLoading: false
      }))
    } catch (error: any) {
      handleMutationError(error, t('system.userCenter.loadSessionFail'))
    } finally {
      sessionLoading.value = false
    }
  }

  const isCurrentSession = (session: Api.Auth.DeviceSession) => {
    return session.isCurrent === true
  }

  const sessionRowClassName = ({ row }: { row: Api.Auth.DeviceSession }) => {
    return isCurrentSession(row) ? 'current-session-row' : ''
  }

  const getDeviceText = (session: Api.Auth.DeviceSession) => {
    const parts: string[] = []
    if (session.browser) parts.push(session.browser)
    if (session.os) parts.push(session.os)
    return parts.length > 0 ? parts.join(' / ') : t('system.userCenter.unknownDevice')
  }

  const formatDateTime = (dateStr?: string) => {
    if (!dateStr) return '-'
    return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
  }

  const handleLogoutSession = async (
    session: Api.Auth.DeviceSession & { logoutLoading?: boolean }
  ) => {
    try {
      await ElMessageBox.confirm(
        t('system.userCenter.logoutSessionConfirm', {
          device: getDeviceText(session),
          ip: session.ipaddr || t('system.userCenter.unknown')
        }),
        t('common.tips'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'warning',
          dangerouslyUseHTMLString: true
        }
      )

      const index = sessionList.value.findIndex(
        (s) => s.deviceSessionId === session.deviceSessionId
      )
      if (index > -1) {
        sessionList.value[index].logoutLoading = true
      }

      const userId = userStore.getUserInfo?.user?.userId
      if (!userId) return

      await forceLogoutSession(userId, session.deviceSessionId)
      ElMessage.success(t('system.userCenter.logoutSessionSuccess'))

      if (index > -1) {
        sessionList.value.splice(index, 1)
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        handleMutationError(error, t('system.userCenter.logoutSessionFail'))
      }
    } finally {
      const idx = sessionList.value.findIndex((s) => s.deviceSessionId === session.deviceSessionId)
      if (idx > -1) {
        sessionList.value[idx].logoutLoading = false
      }
    }
  }

  const handleLogoutAll = async () => {
    try {
      await ElMessageBox.confirm(t('system.userCenter.logoutAllConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })

      logoutAllLoading.value = true
      const userId = userStore.getUserInfo?.user?.userId
      if (!userId) return

      await forceLogoutAllSessions(userId)
      ElMessage.success(t('system.userCenter.logoutAllSuccess'))

      userStore.logOut()
    } catch (error: any) {
      if (error !== 'cancel') {
        handleMutationError(error, t('system.userCenter.logoutAllFail'))
      }
    } finally {
      logoutAllLoading.value = false
    }
  }
  // ==================== 会话管理 END ====================

  /** 根据 userDetail.avatar 更新顶部头像展示 URL（私有桶时用 presigned 避免 403） */
  const updateTopAvatarDisplayUrl = async () => {
    const avatar = userDetail.value.avatar
    if (!avatar || avatar === '') {
      topAvatarDisplayUrl.value = defaultAvatar
      return
    }
    const path = extractOssObjectPath(avatar)
    if (path && needsOssPresignedDisplay(avatar)) {
      try {
        const res = (await fetchGetAvatarPresignedUrl(path)) as any
        const presigned = res?.presignedUrl ?? res?.data?.presignedUrl
        if (presigned) {
          topAvatarDisplayUrl.value = presigned
        } else {
          topAvatarDisplayUrl.value = defaultAvatar
        }
      } catch {
        topAvatarDisplayUrl.value = defaultAvatar
      }
    } else {
      topAvatarDisplayUrl.value = avatar
    }
  }

  /**
   * 获取用户详情
   */
  const getUserDetail = async () => {
    let currentUserId: number | undefined = userInfo.value?.user?.userId

    // 如果用户信息中没有 userId，尝试重新获取用户信息
    if (!currentUserId) {
      loading.value = true
      try {
        const responseData = await fetchGetUserInfo()

        // 后端返回的数据结构是 { user: {...}, roles: [...], permissions: [...] }
        // 用户信息在 user 字段中
        const userInfoData = (responseData as any)?.user || responseData

        if (userInfoData?.userId) {
          const permissions = (responseData as any)?.permissions || []
          const roles = (responseData as any)?.roles || []
          const formattedUserInfo = {
            userId: userInfoData.userId,
            userName: userInfoData.userName || '',
            nickName: userInfoData.nickName,
            email: userInfoData.email || '',
            avatar: userInfoData.avatar,
            buttons: permissions.map((p: any) => p.perms || ''),
            roles: roles.map((r: any) => r.roleKey || r.roleName || '')
          } as any
          userStore.setUserInfo(formattedUserInfo)
          currentUserId = userInfoData.userId
        } else {
          if (import.meta.env.DEV) {
            console.error('重新获取的用户信息中没有 userId:', userInfoData)
            console.error('完整响应数据:', responseData)
          }
          loading.value = false
          return
        }
      } catch (error) {
        if (import.meta.env.DEV) {
          console.error('获取用户信息失败:', error)
        }
        loading.value = false
        return
      }
    }

    if (!currentUserId) {
      if (import.meta.env.DEV) {
        console.error('最终检查：仍然没有 userId')
      }
      loading.value = false
      return
    }

    loading.value = true
    try {
      const res = await fetchGetUserById(currentUserId)
      if (res) {
        userDetail.value = res
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
        Object.assign(originalForm, form)
        await updateTopAvatarDisplayUrl()
      }
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取用户详情失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    const tab = route.query.tab
    if (tab === 'password' || tab === 'session' || tab === 'basic') {
      activeTab.value = tab
    }
    getUserDetail()
  })

  // 切换到会话管理标签页时自动加载会话列表
  watch(activeTab, (val) => {
    if (val === 'session') {
      loadSessions()
    }
  })

  const submitPassword = async () => {
    if (!passwordFormRef.value) return
    await passwordFormRef.value.validate(async (valid) => {
      if (!valid) return
      try {
        await fetchUpdateUserPassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success(t('system.userCenter.pwdSuccess'))
        fetchLogout()
          .catch(() => void 0)
          .finally(() => {
            resetPasswordForm()
            userStore.logOut()
          })
      } catch (error) {
        handleMutationError(error, t('system.userCenter.pwdFail'))
        if (import.meta.env.DEV) {
          console.error('修改密码失败:', error)
        }
      }
    })
  }

  const resetPasswordForm = () => {
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value?.resetFields()
  }

  const resetBasicProfile = () => {
    Object.assign(form, originalForm)
  }

  const submitBasicProfile = async () => {
    if (!form.userId) {
      ElMessage.error(t('system.userCenter.profileNotReady'))
      return
    }
    try {
      await fetchUpdateUser({
        userId: form.userId,
        userName: form.userName,
        nickName: form.nickName,
        email: form.email,
        phonenumber: form.phonenumber,
        avatar: form.avatar,
        sex: form.sex,
        remark: form.remark
      } as Api.SystemManage.UserListItem)
      Object.assign(originalForm, form)
      Object.assign(userDetail.value, form)
      userStore.setUserInfo({
        userName: form.userName,
        nickName: form.nickName,
        email: form.email,
        avatar: form.avatar
      } as any)
      ElMessage.success(t('system.userCenter.saveSuccess'))
    } catch (error) {
      handleMutationError(error, t('system.userCenter.profileFail'))
      if (import.meta.env.DEV) {
        console.error('保存基本资料失败:', error)
      }
    }
  }

  const handleAvatarUploadSuccess = async (avatarUrl: string) => {
    form.avatar = avatarUrl || ''
    userDetail.value.avatar = form.avatar
    await updateTopAvatarDisplayUrl()
    userStore.setUserInfo({
      avatar: form.avatar,
      avatarUpdatedAt: Date.now()
    } as any)
  }

  const handleAvatarUploadError = (error: any) => {
    handleMutationError(error, t('system.userCenter.avatarFail'))
  }
</script>

<style scoped lang="scss">
  :deep(.art-card-sm) {
    overflow: hidden;
    border: 1px solid var(--art-card-border);
    border-radius: 16px;
    box-shadow: var(--art-shadow-card);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--art-shadow-card-hover);
    }
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--art-shadow-sm);
    }
  }

  :deep(.el-select) {
    .el-select__wrapper {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: var(--art-shadow-sm);
      }
    }
  }

  :deep(.el-button) {
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  :deep(.user-center-top-avatar .avatar-preview) {
    border: 4px solid var(--default-box-color);
    box-shadow: 0 10px 20px rgb(0 0 0 / 20%);
  }

  .bg-gradient-to-r {
    background-size: 200% 200%;
    animation: gradientShift 8s ease infinite;
  }

  @keyframes gradientShift {
    0%,
    100% {
      background-position: 0% 50%;
    }

    50% {
      background-position: 100% 50%;
    }
  }

  .hover\:scale-105:hover {
    transform: scale(1.05);
  }

  .session-management {
    .device-info {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    :deep(.el-table) {
      overflow: hidden;
      border-radius: 8px;
    }

    :deep(.current-session-row) {
      background-color: var(--el-color-success-light-9) !important;

      td {
        background-color: transparent !important;
      }
    }
  }
</style>
