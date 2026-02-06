/**
 * 前端运行时配置（部署后可直接修改此文件，无需重新打包）
 * 部署到 ECS 后，将 VITE_API_URL 改为实际后端地址，例如：
 *   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = 'http://你的服务器IP:8080';
 * 若前端与后端同域（通过 Nginx 反向代理 /api），留空即可：
 *   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = '';
 */
window.__APP_RUNTIME_CONFIG__ = window.__APP_RUNTIME_CONFIG__ || {};
window.__APP_RUNTIME_CONFIG__.VITE_API_URL = 'http://101.201.181.191:8080';
