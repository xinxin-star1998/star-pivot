<!-- 个人中心页面 -->
<template>
  <div class="w-full h-full p-0 bg-transparent border-none shadow-none">
    <div v-loading="loading" class="relative flex-b mt-2.5 max-md:block max-md:mt-1">
      <div class="w-112 mr-5 max-md:w-full max-md:mr-0">
        <div class="art-card-sm relative p-0 overflow-hidden">
          <div class="relative h-48 bg-gradient-to-r from-blue-500 to-purple-600">
            <img class="absolute top-0 left-0 w-full h-full object-cover opacity-80" :src="bgImage" />
            <div class="absolute inset-0 bg-gradient-to-b from-transparent to-black/30"></div>
          </div>
          <div class="relative px-6 pb-8 -mt-16 text-center">
            <div class="relative inline-block">
              <img
                class="w-28 h-28 rounded-full border-4 shadow-lg transition-all duration-300 hover:scale-105"
                :class="isDark ? 'border-g-700' : 'border-white'"
                :src="topAvatarDisplayUrl"
                :alt="userDetail.userName || ''"
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
              {{ userDetail.remark || '专注于用户体验跟视觉设计' }}
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
                角色
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
          <div
            class="relative px-6 py-4 border-b transition-colors"
            :class="isDark ? 'border-g-500' : 'border-g-200'"
          >
            <h1
              class="text-xl font-semibold transition-colors"
              :class="isDark ? 'text-g-100' : 'text-g-900'"
            >
              基本设置
            </h1>
            <p class="mt-1 text-sm transition-colors" :class="isDark ? 'text-g-400' : 'text-g-500'">
              查看您的基本信息
            </p>
          </div>

          <ElForm
            :model="form"
            class="box-border p-6 space-y-6"
            label-width="86px"
            label-position="top"
          >
            <ElRow class="gap-6">
              <ElFormItem label="用户名" class="flex-1">
                <ElInput
                  v-model="form.userName"
                  :disabled="true"
                  class="transition-all duration-300"
                >
                  <template #prefix>
                    <ArtSvgIcon
                      icon="ri:user-line"
                      class="transition-colors"
                      :class="isDark ? 'text-g-500' : 'text-g-400'"
                    />
                  </template>
                </ElInput>
              </ElFormItem>
              <ElFormItem label="性别" class="flex-1">
                <ElSelect
                  v-model="form.sex"
                  :disabled="true"
                  class="w-full transition-all duration-300"
                >
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
              <ElFormItem label="昵称" class="flex-1">
                <ElInput
                  v-model="form.nickName"
                  :disabled="true"
                  class="transition-all duration-300"
                >
                  <template #prefix>
                    <ArtSvgIcon
                      icon="ri:user-smile-line"
                      class="transition-colors"
                      :class="isDark ? 'text-g-500' : 'text-g-400'"
                    />
                  </template>
                </ElInput>
              </ElFormItem>
              <ElFormItem label="邮箱" class="flex-1">
                <ElInput
                  v-model="form.email"
                  :disabled="true"
                  class="transition-all duration-300"
                >
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
              <ElFormItem label="手机" class="flex-1">
                <ElInput
                  v-model="form.phonenumber"
                  :disabled="true"
                  class="transition-all duration-300"
                >
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

            <ElFormItem label="个人介绍" class="h-32">
              <ElInput
                type="textarea"
                :rows="4"
                v-model="form.remark"
                :disabled="true"
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
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '@/store/modules/user'
  import {
    fetchGetUserById,
    fetchGetAvatarPresignedUrl
  } from '@/api/user/user'
  import { fetchGetUserInfo } from '@/api/auth'
  import defaultAvatarImg from '@imgs/user/avatar.webp'
  import bgImageImg from '@imgs/user/bg.webp'
  import { useSettingStore } from '@/store/modules/setting'
  import ArtAvatarUpload from '@/components/core/media/art-avatar-upload/index.vue'

  defineOptions({ name: 'UserCenter' })

  /** 从 OSS/MinIO 完整 URL 中提取对象路径，用于请求 presigned URL */
  function extractAvatarPathFromUrl(url: string): string | null {
    if (!url || !url.startsWith('http')) return null
    try {
      const u = new URL(url)
      let path = u.pathname.replace(/^\//, '')
      if (u.hostname.includes('.oss-') || u.hostname.includes('aliyuncs.com')) return path || null
      const parts = path.split('/')
      if (parts.length > 1) return parts.slice(1).join('/')
      return path || null
    } catch {
      return null
    }
  }

  // 主题状态
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  // 默认头像和背景图片
  const defaultAvatar = defaultAvatarImg
  const bgImage = bgImageImg

  const userStore = useUserStore()
  const userInfo = computed(() => userStore.getUserInfo)

  const loading = ref(false)

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

  /**
   * 性别选项
   */
  const options = [
    { value: '0', label: '男' },
    { value: '1', label: '女' },
    { value: '2', label: '未知' }
  ]

  /** 根据 userDetail.avatar 更新顶部头像展示 URL（私有桶时用 presigned 避免 403） */
  const updateTopAvatarDisplayUrl = async () => {
    const avatar = userDetail.value.avatar
    if (!avatar || avatar === '') {
      topAvatarDisplayUrl.value = defaultAvatar
      return
    }
    const path = extractAvatarPathFromUrl(avatar)
    const isOssUrl = avatar.includes('aliyuncs.com') || avatar.includes('.oss-')
    if (path && isOssUrl) {
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
    getUserDetail()
  })
</script>

<style scoped lang="scss">
  :deep(.art-card-sm) {
    border: 1px solid var(--art-card-border);
    border-radius: 16px;
    box-shadow: 0 4px 16px 0 rgb(0 0 0 / 8%);
    transition: all 0.3s ease;
    overflow: hidden;

    &:hover {
      box-shadow: 0 8px 24px 0 rgb(0 0 0 / 12%);
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
      box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
    }
  }

  :deep(.el-select) {
    .el-select__wrapper {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
      }
    }
  }

  :deep(.el-button) {
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
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
</style>
