<!-- 登录页面 -->
<template>
  <div class="flex w-full h-screen" :class="isDark ? 'dark-bg' : 'light-bg'">
    <LoginLeftView />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap">
        <div class="form">
          <div class="form-header">
            <h3 class="title">{{ t('login.title') }}</h3>
            <p class="sub-title">{{ t('login.subTitle') }}</p>
          </div>

          <ElForm
            ref="formRef"
            :model="formData"
            :rules="rules"
            :key="formKey"
            @keyup.enter="handleSubmit"
            class="form-content"
          >
            <ElFormItem prop="username">
              <ElInput
                class="custom-height"
                :placeholder="t('login.placeholder.username')"
                v-model.trim="formData.username"
                clearable
              >
                <template #prefix>
                  <ArtSvgIcon
                    icon="ri:user-line"
                    class="text-lg transition-colors"
                    :class="isDark ? 'text-g-500' : 'text-g-400'"
                  />
                </template>
              </ElInput>
            </ElFormItem>

            <ElFormItem prop="password">
              <ElInput
                class="custom-height"
                :placeholder="t('login.placeholder.password')"
                v-model.trim="formData.password"
                type="password"
                show-password
              >
                <template #prefix>
                  <ArtSvgIcon
                    icon="ri:lock-line"
                    class="text-lg transition-colors"
                    :class="isDark ? 'text-g-500' : 'text-g-400'"
                  />
                </template>
              </ElInput>
            </ElFormItem>

            <ElFormItem
              v-if="captchaEnabled && captchaType === 'image'"
              prop="captcha"
              :error="captchaError"
              class="captcha-item"
            >
              <div class="captcha-container">
                <ElInput
                  v-model="formData.captcha"
                  :placeholder="t('login.placeholder.captcha')"
                  class="captcha-input custom-height"
                  @keyup.enter="handleSubmit"
                  clearable
                >
                  <template #prefix>
                    <ArtSvgIcon
                      icon="ri:shield-check-line"
                      class="text-lg transition-colors"
                      :class="isDark ? 'text-g-500' : 'text-g-400'"
                    />
                  </template>
                </ElInput>
                <div class="captcha-image-wrapper">
                  <img
                    v-if="captchaImage"
                    :src="captchaImage"
                    alt="验证码"
                    class="captcha-image"
                    @click="refreshCaptcha"
                    :class="{ 'opacity-50': loadingCaptcha }"
                  />
                  <div v-else class="captcha-placeholder" @click="refreshCaptcha">
                    <ArtSvgIcon icon="ri:refresh-line" class="text-lg" />
                    <span>获取验证码</span>
                  </div>
                  <div v-if="loadingCaptcha" class="captcha-loading">
                    <div class="loading-spinner"></div>
                  </div>
                </div>
              </div>
            </ElFormItem>

            <ElFormItem
              v-if="captchaEnabled && captchaType === 'slider'"
              :error="captchaError"
              class="captcha-item"
            >
              <LoginCaptchaSlider
                ref="behaviorCaptchaRef"
                @verified="onBehaviorVerified"
                @failed="onBehaviorFailed"
                @refreshed="onBehaviorRefreshed"
              />
            </ElFormItem>

            <ElFormItem
              v-if="captchaEnabled && captchaType === 'drag'"
              :error="captchaError"
              class="captcha-item"
            >
              <LoginCaptchaDrag
                ref="behaviorCaptchaRef"
                @verified="onBehaviorVerified"
                @failed="onBehaviorFailed"
                @refreshed="onBehaviorRefreshed"
              />
            </ElFormItem>

            <ElFormItem
              v-if="captchaEnabled && captchaType === 'click'"
              :error="captchaError"
              class="captcha-item"
            >
              <LoginCaptchaClick
                ref="behaviorCaptchaRef"
                @verified="onBehaviorVerified"
                @failed="onBehaviorFailed"
                @refreshed="onBehaviorRefreshed"
              />
            </ElFormItem>

            <div class="form-options">
              <ElCheckbox v-model="formData.rememberPassword" class="remember-checkbox">
                {{ t('login.rememberPwd') }}
              </ElCheckbox>
              <RouterLink class="forget-link" :to="{ name: 'ForgetPassword' }">
                {{ t('login.forgetPwd') }}
              </RouterLink>
            </div>

            <ElButton
              class="submit-btn custom-height"
              type="primary"
              @click="handleSubmit"
              :loading="loading"
              v-ripple
            >
              {{ t('login.btnText') }}
            </ElButton>

            <div v-if="registerEnabled" class="form-footer">
              <span class="footer-text">{{ t('login.noAccount') }}</span>
              <RouterLink class="register-link" :to="{ name: 'Register' }">
                {{ t('login.register') }}
              </RouterLink>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { useUserStore } from '@/store/modules/user'
  import { useSettingStore } from '@/store/modules/setting'
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchCaptcha, fetchLogin, fetchVerifyCaptcha } from '@/api/auth'
  import { getLoginConfig } from '@/utils/auth/login-config'
  import LoginCaptchaSlider from './modules/login-captcha-slider.vue'
  import LoginCaptchaDrag from './modules/login-captcha-drag.vue'
  import LoginCaptchaClick from './modules/login-captcha-click.vue'
  import { ElMessageBox, ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { useCommon } from '@/hooks'
  import { useRoute, useRouter } from 'vue-router'

  defineOptions({ name: 'Login' })

  const { t, locale } = useI18n()
  const formKey = ref(0)
  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)

  watch(locale, () => {
    formKey.value++
  })

  const userStore = useUserStore()
  const router = useRouter()
  const route = useRoute()

  const systemName = AppConfig.systemInfo.name
  const formRef = ref<FormInstance>()
  const LOGIN_INFO_STORAGE_KEY = 'login-info'

  const formData = reactive({
    username: '',
    password: '',
    // 默认勾选记住密码
    rememberPassword: true,
    /** 当前验证码 token，由服务端生成 */
    captchaToken: '',
    captcha: ''
  })

  const captchaImage = ref('')
  const loadingCaptcha = ref(false)
  const captchaError = ref('')

  const rules = computed<FormRules>(() => ({
    username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }],
    ...(captchaEnabled.value && captchaType.value === 'image'
      ? {
          captcha: [{ required: true, message: t('login.placeholder.captcha'), trigger: 'blur' }]
        }
      : {})
  }))

  const loading = ref(false)
  const registerEnabled = ref(false)
  /** 等登录配置拉取完成后再展示，避免默认 image 闪一下「获取验证码」 */
  const captchaEnabled = ref(false)
  const captchaType = ref('')
  const behaviorCaptchaRef = ref<{ refresh: () => void }>()
  /** 行为验证码（滑块/拖动条/点选）通过后缓存的一次性 proof */
  const behaviorCaptchaProof = ref('')

  const isBehaviorCaptcha = computed(() => ['slider', 'drag', 'click'].includes(captchaType.value))

  const onBehaviorVerified = (proof: string) => {
    behaviorCaptchaProof.value = proof
    captchaError.value = ''
  }

  const onBehaviorFailed = () => {
    behaviorCaptchaProof.value = ''
    captchaError.value = '验证失败，请重试'
  }

  const onBehaviorRefreshed = () => {
    behaviorCaptchaProof.value = ''
    captchaError.value = ''
  }

  // 登录
  const handleSubmit = async () => {
    if (!formRef.value) return

    // 防止重复提交：如果正在加载中，直接返回
    if (loading.value) {
      return
    }

    try {
      // 表单验证
      const valid = await formRef.value.validate()
      if (!valid) return

      captchaError.value = ''

      let captchaProof: string | undefined

      if (captchaEnabled.value) {
        if (isBehaviorCaptcha.value) {
          if (!behaviorCaptchaProof.value) {
            captchaError.value = '请先完成安全验证'
            return
          }
          loading.value = true
          captchaProof = behaviorCaptchaProof.value
        } else {
          if (!formData.captchaToken) {
            captchaError.value = '请先获取验证码'
            return
          }

          loading.value = true

          const verifyRes = await fetchVerifyCaptcha({
            captchaToken: formData.captchaToken,
            code: formData.captcha,
            scene: 'login'
          })

          captchaProof = verifyRes.captchaProof
        }
      } else {
        loading.value = true
      }

      const { username, password } = formData

      const response = await fetchLogin({
        username,
        password,
        captchaProof,
        rememberPassword: formData.rememberPassword
      })

      const {
        token,
        refreshToken,
        username: returnedUsername,
        nickname,
        deviceSessionId,
        needChangePassword,
        passwordModifyReason
      } = response

      // 验证token
      if (!token) {
        console.error('[Login] Login failed - no token received')
        ElNotification({
          title: t('common.error') || '错误',
          type: 'error',
          duration: 2500,
          zIndex: 10000,
          message: '登录失败：未获取到 token'
        })
        return
      }

      // 存储 token、refreshToken 和登录状态
      userStore.setToken(token, refreshToken, deviceSessionId)
      userStore.setLoginStatus(true)

      // 设置用户信息
      userStore.setUserInfo({
        user: {
          userId: 0,
          username: returnedUsername,
          nickName: nickname,
          avatar: '',
          email: '',
          phoneNumber: '',
          sex: 0,
          status: '',
          createTime: ''
        }
      })

      // 登录成功处理
      showLoginSuccessNotice()

      // 保存登录信息到本地存储
      saveLoginInfo()

      if (needChangePassword) {
        await promptChangePassword(passwordModifyReason)
      }

      // 获取 redirect 参数，如果存在且不是登录页或根路径则跳转到指定页面，否则跳转到首页
      const redirect = route.query.redirect as string
      if (redirect && redirect !== '/' && !redirect.includes('login')) {
        router.push(redirect)
      } else {
        // 跳转到首页，首页路径从菜单中获取
        const { homePath } = useCommon()
        router.push(homePath.value || '/')
      }
    } catch (error) {
      // 处理 HttpError
      if (error instanceof HttpError) {
        // 处理验证码错误
        if (error.code === 401 && error.message.includes('验证码')) {
          captchaError.value = error.message
        }
      } else {
        console.error('[Login] Unexpected error:', error)
      }
      if (captchaEnabled.value) {
        refreshCaptcha()
      }
    } finally {
      loading.value = false
    }
  }

  // 获取验证码
  const refreshCaptcha = async () => {
    captchaError.value = ''
    behaviorCaptchaProof.value = ''

    if (isBehaviorCaptcha.value) {
      behaviorCaptchaRef.value?.refresh()
      return
    }

    loadingCaptcha.value = true
    try {
      const response = await fetchCaptcha()
      formData.captchaToken = response.captchaToken
      captchaImage.value = response.captchaImage || ''
    } catch (error) {
      console.error('获取验证码失败:', error)
    } finally {
      loadingCaptcha.value = false
    }
  }

  // 从本地存储读取保存的登录信息（账号+密码）
  const loadSavedLoginInfo = () => {
    try {
      const savedInfo = localStorage.getItem(LOGIN_INFO_STORAGE_KEY)
      if (savedInfo) {
        const parsedInfo = JSON.parse(savedInfo) as {
          username?: string
          password?: string
          rememberPassword?: boolean
        }
        if (parsedInfo.username) {
          formData.username = parsedInfo.username
        }
        if (parsedInfo.password) {
          formData.password = parsedInfo.password
        }
        formData.rememberPassword = parsedInfo.rememberPassword === true
      }
    } catch (error) {
      console.error('Failed to load saved login info:', error)
    }
  }

  // 保存登录信息到本地存储（账号+密码+勾选状态）
  const saveLoginInfo = () => {
    try {
      if (formData.rememberPassword) {
        const loginInfo = {
          username: formData.username,
          password: formData.password,
          rememberPassword: formData.rememberPassword
        }
        localStorage.setItem(LOGIN_INFO_STORAGE_KEY, JSON.stringify(loginInfo))
      } else {
        // 如果用户取消记住密码，清除本地存储中的登录信息
        localStorage.removeItem(LOGIN_INFO_STORAGE_KEY)
      }
    } catch (error) {
      console.error('Failed to save login info:', error)
    }
  }

  const promptChangePassword = async (reason?: string) => {
    const message =
      reason === 'PASSWORD_EXPIRED'
        ? '您的密码已过期，请尽快修改密码。'
        : '您正在使用初始密码登录，请尽快修改密码。'

    try {
      await ElMessageBox.confirm(message, '安全提示', {
        confirmButtonText: '去修改',
        cancelButtonText: '稍后',
        type: 'warning'
      })
      await router.push({ path: '/system/user-center', query: { tab: 'password' } })
    } catch {
      // 用户选择稍后
    }
  }

  // 组件挂载时加载登录配置并获取验证码
  onMounted(async () => {
    loadSavedLoginInfo()
    const loginConfig = await getLoginConfig()
    captchaEnabled.value = loginConfig.captchaEnabled
    registerEnabled.value = loginConfig.registerEnabled
    captchaType.value = ['slider', 'drag', 'click'].includes(loginConfig.captchaType || '')
      ? (loginConfig.captchaType as string)
      : 'image'
    if (captchaEnabled.value && captchaType.value === 'image') {
      refreshCaptcha()
    }
  })

  // 登录成功提示
  const showLoginSuccessNotice = () => {
    setTimeout(() => {
      ElNotification({
        title: t('login.success.title'),
        type: 'success',
        duration: 2500,
        zIndex: 10000,
        message: `${t('login.success.message')}, ${systemName}!`
      })
    }, 1000)
  }
</script>

<style scoped>
  @import './style.css';
</style>

<style lang="scss" scoped>
  :deep(.el-select__wrapper) {
    height: 40px !important;
  }
</style>
