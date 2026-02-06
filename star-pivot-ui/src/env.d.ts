/// <reference types="vite/client" />

declare module 'nprogress'

declare module 'crypto-js'

declare module 'vue-img-cutter'

declare module 'file-saver'

declare module '@wangeditor/editor-for-vue' {
  import type { DefineComponent } from 'vue'
  export const Editor: DefineComponent<unknown, unknown, unknown>
  export const Toolbar: DefineComponent<unknown, unknown, unknown>
}

declare module 'qrcode.vue' {
  export type Level = 'L' | 'M' | 'Q' | 'H'
  export type RenderAs = 'canvas' | 'svg'
  export type GradientType = 'linear' | 'radial'
  export interface ImageSettings {
    src: string
    height: number
    width: number
    excavate: boolean
  }
  export interface QRCodeProps {
    value: string
    size?: number
    level?: Level
    background?: string
    foreground?: string
    renderAs?: RenderAs
  }
  const QrcodeVue: any
  export default QrcodeVue
}

// Element Plus 图标按需导入模块声明
// 某些情况下 @element-plus/icons-vue 的类型声明不会被 TS 正确识别，这里做一个兜底声明
declare module '@element-plus/icons-vue'

// dayjs 时间库模块声明
// 项目已安装 dayjs，但可能缺少类型声明或 TS 未正确推断，这里做兜底声明
declare module 'dayjs'

// 全局变量声明
declare const __APP_VERSION__: string // 版本号

// 运行时配置（部署后通过 public/config.js 注入，无需重新打包即可修改 API 地址）
interface AppRuntimeConfig {
  VITE_API_URL?: string
}
declare global {
  interface Window {
    __APP_RUNTIME_CONFIG__?: AppRuntimeConfig
  }
}

// Vue i18n 全局类型声明
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $t: (key: string) => string
  }
}

// 确保 Vue 类型可以从 'vue' 模块正确导入
// 这些类型在 Vue 3.5+ 中应该可以从 'vue' 导入，但 vue-tsc 可能需要额外的类型声明
declare module 'vue' {
  export type {
    App,
    Directive,
    DirectiveBinding,
    DefineComponent,
    ComponentPublicInstance
  } from 'vue'
}
