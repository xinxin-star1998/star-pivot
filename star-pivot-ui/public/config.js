/**
 * 前端运行时配置（部署后可直接修改此文件，无需重新打包）
 * 部署到 ECS 后，将 VITE_API_URL 改为实际后端地址，例如：
 *   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = 'http://你的服务器IP:8080/api/v1';
 * 若前端与后端同域（通过 Nginx 反向代理 /api/v1），使用 /api/v1 即可：
 *   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = '/api/v1';
 */
window.__APP_RUNTIME_CONFIG__ = window.__APP_RUNTIME_CONFIG__ || {}
// window.__APP_RUNTIME_CONFIG__.VITE_API_URL = 'http://101.201.181.191:8080/api/v1';
// 生产环境：使用带版本的路径，与后端 server.servlet.context-path=/api/v1 对应
window.__APP_RUNTIME_CONFIG__.VITE_API_URL = '/api/v1'
