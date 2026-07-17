import App from './App.vue'
import { initStore } from './store' // Store
import { initRouter } from './router' // Router
import language, { loadLanguageOptions, switchAppLanguage } from './locales' // 国际化
import '@styles/core/tailwind.css' // tailwind
import '@styles/index.scss' // 样式
import '@utils/sys/console.ts' // 控制台输出内容
import { setupGlobDirectives } from './directives'
import { setupErrorHandle } from './utils/sys/error-handle'
import { setupOfflineIconify } from './utils/ui/iconify-offline'
import { setupPerformanceMonitor } from './utils/performance'
import { createApp } from 'vue'
import { useUserStore } from './store/modules/user'

void setupOfflineIconify()

setupPerformanceMonitor({
  enableConsoleLog: import.meta.env.DEV,
  enableReport: import.meta.env.VITE_PERF_REPORT === 'true',
  reportUrl: (import.meta.env.VITE_PERF_REPORT_URL as string) || '',
  onlyAbnormal: import.meta.env.VITE_PERF_ONLY_ABNORMAL === 'true'
})

document.addEventListener(
  'touchstart',
  function () {},
  { passive: false }
)

const app = createApp(App)
initStore(app)
initRouter(app)
setupGlobDirectives(app)
setupErrorHandle(app)

app.use(language)
app.mount('#app')

// Pinia 持久化恢复后，强制对齐 vue-i18n locale 与远程 UI 包
const userStore = useUserStore()
userStore.setLanguage(userStore.language)
void loadLanguageOptions()
void switchAppLanguage(userStore.language)