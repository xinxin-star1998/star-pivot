<!-- 登录页面 -->
<template>
  <div class="flex w-full h-screen">
    <LoginLeftView />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">{{ t('login.title') }}</h3>
          <p class="sub-title">{{ t('login.subTitle') }}</p>
          <ElForm
            ref="formRef"
            :model="formData"
            :rules="rules"
            :key="formKey"
            @keyup.enter="handleSubmit"
            style="margin-top: 25px"
          >
            <ElFormItem prop="username">
              <ElInput
                class="custom-height"
                :placeholder="t('login.placeholder.username')"
                v-model.trim="formData.username"
              />
            </ElFormItem>
            <ElFormItem prop="password">
              <ElInput
                class="custom-height"
                :placeholder="t('login.placeholder.password')"
                v-model.trim="formData.password"
                type="password"
                autocomplete="off"
                show-password
              />
            </ElFormItem>

            <!-- 验证码 -->
            <div class="relative pb-5 mt-6">
              <ElFormItem prop="captcha" :error="captchaError">
                <div class="flex items-center">
                  <ElInput
                    v-model="formData.captcha"
                    :placeholder="t('login.placeholder.captcha')"
                    class="mr-2 custom-height"
                    @keyup.enter="handleSubmit"
                  />
                  <div class="relative">
                    <img
                      v-if="captchaImage"
                      :src="captchaImage"
                      alt="验证码"
                      class="h-10 w-32 cursor-pointer rounded border border-gray-300"
                      @click="refreshCaptcha"
                      :class="{ 'opacity-50': loadingCaptcha }"
                    />
                    <div
                      v-else
                      class="h-10 w-32 cursor-pointer rounded border border-gray-300 flex items-center justify-center bg-gray-50"
                      @click="refreshCaptcha"
                    >
                      <span class="text-gray-400 text-sm">点击获取验证码</span>
                    </div>
                    <div
                      v-if="loadingCaptcha"
                      class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70"
                    >
                      <div
                        class="w-5 h-5 border-2 border-gray-300 border-t-blue-500 rounded-full animate-spin"
                      ></div>
                    </div>
                  </div>
                </div>
              </ElFormItem>
            </div>

            <div class="flex-cb mt-2 text-sm">
              <ElCheckbox v-model="formData.rememberPassword">{{
                t('login.rememberPwd')
              }}</ElCheckbox>
              <RouterLink class="text-theme" :to="{ name: 'ForgetPassword' }">{{
                t('login.forgetPwd')
              }}</RouterLink>
            </div>

            <div style="margin-top: 30px">
              <ElButton
                class="w-full custom-height"
                type="primary"
                @click="handleSubmit"
                :loading="loading"
                v-ripple
              >
                {{ t('login.btnText') }}
              </ElButton>
            </div>

            <div class="mt-5 text-sm text-gray-600">
              <span>{{ t('login.noAccount') }}</span>
              <RouterLink class="text-theme" :to="{ name: 'Register' }">{{
                t('login.register')
              }}</RouterLink>
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
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchLogin, fetchCaptcha, fetchVerifyCaptcha } from '@/api/auth'
  import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { useCommon } from '@/hooks'
  import { useRoute, useRouter } from 'vue-router'

  defineOptions({ name: 'Login' })

  const { t, locale } = useI18n()
  const formKey = ref(0)

  // 监听语言切换，重置表单
  watch(locale, () => {
    formKey.value++
  })

  const userStore = useUserStore()
  const router = useRouter()
  const route = useRoute()

  const systemName = AppConfig.systemInfo.name
  const formRef = ref<FormInstance>()

  const formData = reactive({
    username: '',
    password: '',
    // 默认不勾选记住密码，由用户主动选择
    rememberPassword: false,
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
    captcha: [{ required: true, message: t('login.placeholder.captcha'), trigger: 'blur' }]
  }))

  const loading = ref(false)

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

      // 先校验验证码，获取一次性 proof
      if (!formData.captchaToken) {
        captchaError.value = '请先获取验证码'
        return
      }

      // 立即设置 loading 状态，防止重复点击
      loading.value = true

      const verifyRes = await fetchVerifyCaptcha({
        captchaToken: formData.captchaToken,
        code: formData.captcha,
        scene: 'login'
      })

      const captchaProof = verifyRes.captchaProof

      // 登录请求
      const { username, password } = formData

      const response = await fetchLogin({
        username,
        password,
        captchaProof,
        rememberPassword: formData.rememberPassword
      })

      const { token, refreshToken, username: returnedUsername, nickname } = response

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
      userStore.setToken(token, refreshToken)
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
          refreshCaptcha()
        }
        // 处理账户锁定错误（423）
        else if (error.code === 423) {
          // 账户锁定错误，HTTP拦截器已经显示了错误消息，这里不需要额外处理
          // 但可以刷新验证码，让用户重新尝试
          refreshCaptcha()
        }
      } else {
        // 处理非 HttpError
        console.error('[Login] Unexpected error:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 获取验证码
  const refreshCaptcha = async () => {
    loadingCaptcha.value = true
    captchaError.value = ''

    try {
      const response = await fetchCaptcha()
      formData.captchaToken = response.captchaToken
      captchaImage.value = response.captchaImage
    } catch (error) {
      console.error('获取验证码失败:', error)
    } finally {
      loadingCaptcha.value = false
    }
  }

  // 从本地存储读取保存的登录信息（仅账号信息，不再保存密码）
  const loadSavedLoginInfo = () => {
    try {
      const savedInfo = localStorage.getItem('login-info')
      if (savedInfo) {
        const parsedInfo = JSON.parse(savedInfo)
        if (parsedInfo.username) {
          formData.username = parsedInfo.username
        }
        // 兼容老数据：如果曾经存过密码，这里直接忽略密码字段
        formData.rememberPassword =
          parsedInfo.rememberPassword !== undefined ? parsedInfo.rememberPassword : true
      }
    } catch (error) {
      console.error('Failed to load saved login info:', error)
    }
  }

  // 保存登录信息到本地存储（仅账号和勾选状态，不保存密码）
  const saveLoginInfo = () => {
    try {
      if (formData.rememberPassword) {
        const loginInfo = {
          username: formData.username,
          rememberPassword: formData.rememberPassword
        }
        localStorage.setItem('login-info', JSON.stringify(loginInfo))
      } else {
        // 如果用户取消记住密码，清除本地存储中的登录信息
        localStorage.removeItem('login-info')
      }
    } catch (error) {
      console.error('Failed to save login info:', error)
    }
  }

  // 组件挂载时获取验证码并加载保存的登录信息
  onMounted(() => {
    refreshCaptcha()
    loadSavedLoginInfo()
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
