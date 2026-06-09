# StarPivot Docker 部署指南

> 本文档介绍如何使用 Docker / Docker Compose 在**开发环境**和**生产环境**部署 StarPivot 权限管理系统。

---

## 目录

- [前置条件](#前置条件)
- [快速开始](#快速开始)
- [环境配置 (.env)](#环境配置-env)
- [生产环境部署](#生产环境部署)
- [开发环境部署](#开发环境部署)
- [使用 Nginx 反向代理（可选）](#使用-nginx-反向代理可选)
- [常用运维命令](#常用运维命令)
- [故障排查](#故障排查)
- [升级与回滚](#升级与回滚)
- [安全建议](#安全建议)

---

## 前置条件

| 工具            | 最低版本   | 说明                              |
| --------------- | ---------- | --------------------------------- |
| Docker          | 20.10+     | [安装指南](https://docs.docker.com/get-docker/) |
| Docker Compose  | v2.0+      | Docker Desktop 已内置；Linux 需单独安装 |
| Git             | 2.x+       | 克隆代码仓库                      |
| Maven（可选）   | 3.8+       | 如需在宿主机预先构建 JAR          |
| JDK（可选）     | 17         | 与 Maven 配合使用                 |

验证安装：

```bash
docker --version
docker compose version   # 或 docker-compose --version
```

---

## 快速开始

```bash
# 1. 克隆项目
git clone https://gitee.com/xin1998/StarPivot.git
cd StarPivot

# 2. 复制环境变量文件并编辑
cp " .env.example" .env
# 编辑 .env，至少修改 MYSQL_ROOT_PASSWORD、JWT_SECRET

# 3. 准备数据库初始化 SQL
mkdir -p sql/init
cp sql/star-pivot.sql sql/init/01-star-pivot.sql
cp sql/workflow.sql    sql/init/02-workflow.sql
cp sql/mall_pms.sql    sql/init/03-mall_pms.sql

# 4. 一键启动（MySQL + Redis + 应用）
docker compose up -d

# 5. 查看日志
docker compose logs -f star-pivot
```

启动成功后访问：

| 地址                          | 说明               |
| ----------------------------- | ------------------ |
| `http://localhost:8080/api/doc.html` | API 文档（Knife4j） |
| `http://localhost:8080/api/actuator/health` | 健康检查端点      |

---

## 环境配置 (.env)

所有敏感配置均通过 `.env` 文件注入，**请勿将 .env 提交到版本库**。

### 数据库配置

| 变量                | 默认值           | 说明                   |
| ------------------- | ---------------- | ---------------------- |
| `MYSQL_ROOT_PASSWORD` | `root123456`   | MySQL root 密码        |
| `MYSQL_DATABASE`     | `star-pivot`   | 数据库名               |
| `MYSQL_USER`         | `starpivot`    | 应用专用用户           |
| `MYSQL_PASSWORD`     | `starpivot123` | 应用专用用户密码       |
| `MYSQL_PORT`         | `3306`         | 宿主机映射的 MySQL 端口 |

### Redis 配置

| 变量             | 默认值       | 说明                     |
| ---------------- | ------------ | ------------------------ |
| `REDIS_PASSWORD` | `redis123`   | Redis 密码               |
| `REDIS_PORT`     | `6379`       | 宿主机映射的 Redis 端口  |

### 应用配置

| 变量                    | 默认值                                              | 说明                         |
| ----------------------- | --------------------------------------------------- | ---------------------------- |
| `SPRING_PROFILES_ACTIVE`| `prod`                                              | Spring 激活 Profile          |
| `APP_PORT`              | `8080`                                              | 宿主机映射的应用端口         |
| `JWT_SECRET`            | `your-secret-key-change-in-production-min-32-bytes` | **必须修改**，至少 32 字节   |
| `JWT_EXPIRATION`        | `86400000`                                          | Token 过期时间（毫秒，默认24h）|
| `CORS_ALLOWED_ORIGINS`  | `http://localhost:5173`                             | 前端跨域白名单               |

### OSS 配置（可选）

| 变量                 | 默认值  | 说明                              |
| -------------------- | ------- | --------------------------------- |
| `OSS_ENABLED`        | `false` | 是否启用阿里云 OSS               |
| `OSS_ENDPOINT`       | -       | OSS 端点，如 `oss-cn-beijing.aliyuncs.com` |
| `OSS_ACCESS_KEY_ID`  | -       | AccessKey ID                     |
| `OSS_ACCESS_KEY_SECRET` | -    | AccessKey Secret                 |
| `OSS_BUCKET_NAME`    | `star-pivot` | Bucket 名称                  |

### Nginx 配置（可选）

| 变量              | 默认值 | 说明                |
| ----------------- | ------ | ------------------- |
| `NGINX_PORT`      | `80`   | HTTP 端口           |
| `NGINX_HTTPS_PORT`| `443`  | HTTPS 端口          |

---

## 生产环境部署

### 1. 准备数据库初始化文件

Docker Compose 会自动执行 `./sql/init/` 目录下的 `.sql` 文件（按文件名排序）：

```bash
mkdir -p sql/init
cp sql/star-pivot.sql sql/init/01-star-pivot.sql
cp sql/workflow.sql    sql/init/02-workflow.sql
cp sql/mall_pms.sql    sql/init/03-mall_pms.sql
# 如有补丁脚本也一并复制
cp sql/patch_workflow_menu.sql    sql/init/04-patch-workflow-menu.sql
cp sql/patch_external_gen_menu.sql sql/init/05-patch-external-gen-menu.sql
```

> **注意**：`star_pivot_dev.sql` 包含大量测试数据，生产环境**不要**导入该文件。

### 2. 安全配置 .env

```ini
# 生产环境 .env 示例
MYSQL_ROOT_PASSWORD=<强密码>
MYSQL_DATABASE=star-pivot
MYSQL_USER=starpivot
MYSQL_PASSWORD=<强密码>
REDIS_PASSWORD=<强密码>
JWT_SECRET=<随机生成至少32字节字符串>
JWT_EXPIRATION=7200000
CORS_ALLOWED_ORIGINS=https://your-domain.com
SPRING_PROFILES_ACTIVE=prod
OSS_ENABLED=false
```

生成安全的 JWT_SECRET：

```bash
openssl rand -base64 48
```

### 3. 构建并启动

```bash
# 构建镜像（多阶段：Maven 编译 → JRE 运行）
docker compose build

# 启动所有服务
docker compose up -d

# 查看启动状态
docker compose ps
```

### 4. 验证部署

```bash
# 等待应用启动（约 60-90 秒）
docker compose logs -f star-pivot

# 健康检查
curl http://localhost:8080/api/actuator/health
# 期望返回：{"status":"UP"}
```

### 5. 前端构建（配合 Nginx）

```bash
cd star-pivot-ui
pnpm install
pnpm run build
# 产物在 star-pivot-ui/dist/ 目录
```

然后启用 Nginx profile（见下节）。

---

## 开发环境部署

开发环境使用 `docker-compose.dev.yml` 覆盖生产配置，支持热重载和远程调试。

```bash
# 同时使用基础配置和开发覆盖配置
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 查看日志
docker compose -f docker-compose.yml -f docker-compose.dev.yml logs -f star-pivot
```

### 开发环境特性

| 特性           | 说明                                       |
| -------------- | ------------------------------------------ |
| 热重载         | 源码通过 volume 挂载到容器 `/app/src`     |
| 远程调试端口   | `5005`（IDE 连接 `localhost:5005`）        |
| 懒加载         | `application-dev.yml` 已开启 lazy-init    |
| Swagger 文档   | 默认开放                                    |
| Adminer（可选）| 使用 `--profile dev-tools` 启用数据库管理 UI |

### 启用 Adminer 数据库管理工具

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml --profile dev-tools up -d adminer
```

访问 `http://localhost:8081`，登录信息：
- 服务器：`mysql`
- 用户名：`starpivot`
- 密码：（.env 中配置的 MYSQL_PASSWORD）
- 数据库：`star-pivot`

---

## 使用 Nginx 反向代理（可选）

Nginx 用于统一前端静态资源和后端 API 入口，适合生产环境部署。

### 1. 修改 nginx-config.conf

将 `server_name` 和 SSL 证书路径替换为实际值：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    # 如不需要 HTTPS 可注释掉重定向，直接在此 server 块配置 location
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate     /path/to/fullchain.pem;
    ssl_certificate_key /path/to/privkey.pem;
    # ... 其余 SSL 配置保持不变

    location / {
        root /usr/share/nginx/html;  # 容器内路径，对应 volume 挂载
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://star-pivot:8080/api/;  # 容器名解析
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

> **注意**：docker-compose.yml 中 Nginx 容器的 proxy_pass 目标为 `star-pivot`（Docker 网络中的容器名），而非 `localhost`。

### 2. 启用 Nginx Profile

```bash
docker compose --profile with-nginx up -d
```

访问 `https://your-domain.com` 即可。

---

## 常用运维命令

### 使用 docker-start.sh（Linux/macOS）

```bash
# 启动
./docker-start.sh start

# 停止
./docker-start.sh stop

# 重启
./docker-start.sh restart

# 查看日志（默认应用，可指定服务名）
./docker-start.sh logs
./docker-start.sh logs mysql

# 查看状态
./docker-start.sh status

# 重新构建镜像
./docker-start.sh build

# 清理所有容器和数据卷（危险！）
./docker-start.sh clean
```

### 直接使用 Docker Compose

```bash
# 查看所有服务状态
docker compose ps

# 查看特定服务日志
docker compose logs -f star-pivot
docker compose logs -f mysql
docker compose logs -f redis

# 重启单个服务
docker compose restart star-pivot

# 进入 MySQL 容器
docker compose exec mysql mysql -u root -p

# 进入 Redis 容器
docker compose exec redis redis-cli -a <REDIS_PASSWORD>

# 备份 MySQL 数据
docker compose exec mysql mysqldump -u root -p<密码> star-pivot > backup_$(date +%Y%m%d).sql

# 恢复 MySQL 数据
docker compose exec -T mysql mysql -u root -p<密码> star-pivot < backup_20260601.sql
```

---

## 故障排查

### 应用启动失败

```bash
# 查看应用日志
docker compose logs star-pivot

# 常见原因：
# 1. MySQL 未就绪 → 检查 healthcheck: docker compose ps mysql
# 2. JWT_SECRET 太短 → 确保至少 32 字节
# 3. 端口被占用 → 修改 .env 中的 APP_PORT
```

### 数据库连接失败

```bash
# 确认 MySQL 容器运行正常
docker compose ps mysql

# 查看 MySQL 日志
docker compose logs mysql

# 测试连接
docker compose exec mysql mysql -u starpivot -p star-pivot -e "SELECT 1"
```

### Redis 连接失败

```bash
# 确认 Redis 容器运行
docker compose ps redis

# 测试连接
docker compose exec redis redis-cli -a redis123 ping
# 期望返回：PONG
```

### 健康检查失败

```bash
# 手动触发健康检查
curl -v http://localhost:8080/api/actuator/health

# 查看健康检查配置
docker inspect star-pivot-app --format='{{json .State.Health}}'
```

### 内存不足

```bash
# 调整 JVM 参数（在 docker-compose.yml 的 environment 中添加）
JAVA_OPTS: "-Xms256m -Xmx512m"
```

---

## 升级与回滚

### 升级步骤

```bash
# 1. 备份数据库
docker compose exec mysql mysqldump -u root -p<密码> star-pivot > backup_before_upgrade.sql

# 2. 拉取最新代码
git pull origin main

# 3. 重新构建并启动（仅重建变化的服务）
docker compose build star-pivot
docker compose up -d star-pivot

# 4. 验证
curl http://localhost:8080/api/actuator/health
```

### 回滚

```bash
# 1. 回滚代码
git checkout <previous-tag-or-commit>

# 2. 恢复数据库（如有 schema 变更）
docker compose exec -T mysql mysql -u root -p<密码> star-pivot < backup_before_upgrade.sql

# 3. 重新构建并启动
docker compose build star-pivot
docker compose up -d star-pivot
```

---

## 安全建议

### 必须执行

- [ ] **修改默认密码**：MYSQL_ROOT_PASSWORD、MYSQL_PASSWORD、REDIS_PASSWORD
- [ ] **修改 JWT_SECRET**：使用 `openssl rand -base64 48` 生成，至少 32 字节
- [ ] **关闭不需要的端口**：生产环境不要暴露 `3306`、`6379`、`5005`
- [ ] **配置 CORS 白名单**：`CORS_ALLOWED_ORIGINS` 设置为实际前端域名

### 建议执行

- [ ] **启用 HTTPS**：使用 Nginx + Let's Encrypt 证书
- [ ] **限制 Docker 网络**：MySQL 和 Redis 端口仅对 Docker 内部网络开放
- [ ] **定期备份**：设置 cron 定时执行 `mysqldump`
- [ ] **更新镜像**：定期 `docker compose pull` 更新基础镜像
- [ ] **关闭调试端口**：生产环境移除 `5005` 端口映射
- [ ] **关闭 Druid 监控**：生产 profile 已默认关闭

### 端口暴露建议

| 服务      | 开发环境    | 生产环境              |
| --------- | ----------- | --------------------- |
| 应用      | `8080:8080` | `8080:8080`（或仅内网）|
| MySQL     | `3306:3306` | 不暴露，使用 Docker 网络 |
| Redis     | `6379:6379` | 不暴露，使用 Docker 网络 |
| 调试端口  | `5005:5005` | 不暴露               |
| Nginx HTTP | -          | `80:80`              |
| Nginx HTTPS| -          | `443:443`            |
| Adminer   | `8081:8080` | 不暴露               |

---

## 架构说明

```
                    ┌─────────────────────────────────────────────────────┐
                    │                  Docker Network                      │
                    │                  star-pivot-network                  │
                    │                                                      │
  :80/:443          │    ┌──────────┐      ┌──────────────────────────┐   │
  ────────────────► │    │  Nginx   │─────►│  star-pivot (Spring Boot)│   │
  (可选, with-nginx)│    │  (Alpine)│      │  :8080                   │   │
                    │    └──────────┘      └────────────┬─────────────┘   │
                    │                                    │                 │
                    │                       ┌────────────┼────────────┐   │
                    │                       │            │            │   │
                    │                  ┌────▼────┐  ┌───▼─────┐      │   │
                    │                  │  MySQL  │  │  Redis  │      │   │
                    │                  │  :3306  │  │  :6379  │      │   │
                    │                  └─────────┘  └─────────┘      │   │
                    └─────────────────────────────────────────────────────┘
```

### 数据卷说明

| Volume 名称    | 用途                   | 清理影响               |
| -------------- | ---------------------- | ---------------------- |
| `mysql-data`   | MySQL 持久化数据       | **丢失所有数据库数据** |
| `redis-data`   | Redis AOF 持久化       | 丢失缓存数据           |
| `app-logs`     | 应用日志               | 丢失历史日志           |
| `app-data`     | 应用数据（文件上传等） | 丢失上传文件           |
| `nginx-logs`   | Nginx 访问/错误日志    | 丢失 Nginx 日志        |
