/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  // 在这里可以添加更多的环境变量
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}