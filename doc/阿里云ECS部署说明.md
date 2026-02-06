# StarPivot 阿里云 ECS 部署说明

本文档说明如何将前后端项目打包并部署到阿里云 ECS。

---

## 一、环境要求

- **本地/CI**：JDK 17、Maven 3.6+、Node.js 20+、pnpm 8.8+
- **ECS 服务器**：JDK 17、MySQL、Redis、Nginx（可选，用于单独部署前端）

---

## 二、后端打包（Spring Boot）

### 2.1 在项目根目录执行 Maven 打包

```bash
# 进入项目根目录
cd f:\project1231\StarPivot

# 跳过测试打包（推荐）
mvn clean package -DskipTests

# 或执行测试后打包
mvn clean package
```

### 2.2 打包产物位置

- **可执行 JAR**：`star-pivot-controller/target/star-pivot-controller-0.0.1-SNAPSHOT.jar`
- 该 JAR 为 Spring Boot 可执行 fat jar，内含依赖，可直接用 `java -jar` 运行。

### 2.3 仅打包 controller 模块（可选）

```bash
mvn clean package -pl star-pivot-controller -am -DskipTests
```

---

## 三、前端打包（Vue + Vite）

### 3.1 生产环境 API 地址（推荐：部署后改 config.js，无需重打包）

项目已支持**运行时配置**：部署后只需在服务器上改一个文件即可切换 API 地址，无需重新打包。

**方式一（推荐）：部署后改 config.js**

1. 正常打包并上传 `star-pivot-ui/dist/` 到 ECS。
2. 在服务器上编辑 **`dist/config.js`**（与 `index.html` 同目录），例如：
   ```javascript
   // 后端单独端口时，写完整地址
   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = 'http://101.201.181.191:8080';

   // 若用 Nginx 反向代理 /api 到后端，留空即可（同源请求）
   window.__APP_RUNTIME_CONFIG__.VITE_API_URL = '';
   ```
3. 保存后刷新页面即可生效，**无需重新打包**。

**方式二：打包前改 .env.production**

若希望把地址写进打包结果，可在打包前编辑 `star-pivot-ui/.env.production`，设置 `VITE_API_URL` 为后端地址后再执行 `pnpm run build`。之后若换服务器，需重新改 `.env.production` 并重新打包。

### 3.2 安装依赖并打包

```bash
# 进入前端目录
cd star-pivot-ui

# 安装依赖（使用 pnpm，与 package.json 一致）
pnpm install

# 生产环境打包
pnpm run build
```

### 3.3 打包产物位置

- **静态资源目录**：`star-pivot-ui/dist/`
- 将该目录整体上传到 ECS，用 Nginx 或后端静态资源方式提供访问。

---

## 四、上传到阿里云 ECS

### 4.1 需要上传的文件

| 类型   | 路径                                              | 说明 |
|--------|---------------------------------------------------|------|
| 后端   | `star-pivot-controller/target/star-pivot-controller-0.0.1-SNAPSHOT.jar` | 可执行 JAR |
| 前端   | `star-pivot-ui/dist/` 整个目录                    | 前端静态资源 |

### 4.2 上传方式示例

- **SCP**（在 PowerShell 或 CMD 中可用 `scp`）：
  ```bash
  scp star-pivot-controller/target/star-pivot-controller-0.0.1-SNAPSHOT.jar root@你的ECS公网IP:/opt/star-pivot/
  scp -r star-pivot-ui/dist root@你的ECS公网IP:/opt/star-pivot/
  ```
- **SFTP / 宝塔 / 阿里云控制台文件管理**：将上述 JAR 和 `dist` 目录上传到同一目录，例如 `/opt/star-pivot/`。

---

## 五、ECS 上运行后端

### 5.1 生产环境依赖

- 已安装 **JDK 17**
- 已安装并启动 **MySQL**，并执行项目中的 SQL 初始化脚本（如 `sql/star-pivot.sql`）
- 已安装并启动 **Redis**
- 生产配置使用环境变量，见 `application-prod.yml`

### 5.2 配置环境变量

在 ECS 上运行前，需设置以下环境变量（与 `application-prod.yml` 一致）：

```bash
# 数据库
export DB_URL="jdbc:mysql://localhost:3306/star_pivot?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
export DB_USERNAME="你的数据库用户名"
export DB_PASSWORD="你的数据库密码"

# Redis
export REDIS_HOST="127.0.0.1"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""
export REDIS_DATABASE="0"

# JWT（必须与生成 token 的密钥一致）
export JWT_SECRET="你的JWT密钥，建议足够长且随机"

# CORS 允许的前端访问来源（前端页面访问的域名或 IP）
export CORS_ALLOWED_ORIGINS="http://你的ECS公网IP"
# 若有域名：export CORS_ALLOWED_ORIGINS="https://www.yourdomain.com"

# 若使用 OSS，还需配置
# export OSS_ENDPOINT="..."
# export OSS_ACCESS_KEY_ID="..."
# export OSS_ACCESS_KEY_SECRET="..."
# export OSS_URL_PREFIX="..."
```

可将以上写入脚本，例如 `start.sh`，再用 `source start.sh` 后启动 JAR。

### 5.3 启动后端服务

```bash
cd /opt/star-pivot
java -jar star-pivot-controller-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

建议使用 **systemd** 或 **nohup** 做常驻与开机自启，例如：

```bash
nohup java -jar star-pivot-controller-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > app.log 2>&1 &
```

---

## 六、前端在 ECS 上的部署方式

### 方式 A：使用 Nginx 单独部署前端（推荐）

1. 安装 Nginx，将站点根目录指向 `dist` 目录，例如：
   ```nginx
   server {
       listen 80;
       server_name 你的域名或IP;
       root /opt/star-pivot/dist;
       index index.html;
       location / {
           try_files $uri $uri/ /index.html;
       }
       # 若前端 VITE_API_URL 设为 /api，则反向代理到后端
       location /api {
           proxy_pass http://127.0.0.1:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
2. 前端 `.env.production` 中设置 `VITE_API_URL = /api`，重新执行 `pnpm run build` 后上传 `dist`。

### 方式 B：后端内置静态资源（前后端同端口）

若希望前后端同一端口、不单独配 Nginx，可将打包后的前端放到 Spring Boot 的静态目录，由同一 JAR 提供页面和 API：

1. 将 `star-pivot-ui/dist/` 下的**全部内容**复制到：
   `star-pivot-controller/src/main/resources/static/`
2. 重新打包后端：
   ```bash
   mvn clean package -pl star-pivot-controller -am -DskipTests
   ```
3. 部署时只上传并运行这一个 JAR，访问 `http://ECS IP:8080` 即可同时得到页面和接口。  
此时前端 `.env.production` 建议使用相对路径，例如 `VITE_API_URL = /api`（需与后端接口前缀一致）。

---

## 七、一键打包脚本示例（Windows）

在项目根目录可新建 `build-for-deploy.bat`，便于本地一键打出前后端包：

```batch
@echo off
chcp 65001 >nul
echo 正在打包后端...
call mvn clean package -DskipTests
if %errorlevel% neq 0 ( echo 后端打包失败 & pause & exit /b 1 )
echo 后端打包完成: star-pivot-controller\target\star-pivot-controller-0.0.1-SNAPSHOT.jar

echo.
echo 正在打包前端...
cd star-pivot-ui
call pnpm install
call pnpm run build
cd ..
if %errorlevel% neq 0 ( echo 前端打包失败 & pause & exit /b 1 )
echo 前端打包完成: star-pivot-ui\dist\

echo.
echo 全部打包完成，可上传到 ECS。
pause
```

---

## 八、简要检查清单

- [ ] 后端：`mvn clean package -DskipTests` 成功，JAR 在 `star-pivot-controller/target/`
- [ ] 前端：已修改 `.env.production` 中的 `VITE_API_URL`，再执行 `pnpm run build`，生成 `star-pivot-ui/dist/`
- [ ] ECS：已安装 JDK 17、MySQL、Redis，并配置好环境变量
- [ ] 若用 Nginx：已配置 `root` 与 `location /api` 反向代理
- [ ] 安全组：已放行 80（前端）、8080（后端，若直接访问）

按上述步骤即可在阿里云 ECS 上完成前后端打包与部署。

---

## 九、常见问题

### 9.1 报错：`Unable to access jarfile demo-0.0.1-SNAPSHOT.jar`

- **原因**：本项目的后端 JAR 名称是 **`star-pivot-controller-0.0.1-SNAPSHOT.jar`**，不是 `demo-0.0.1-SNAPSHOT.jar`。
- **处理**：
  1. 检查 ECS 上的启动命令或脚本，把 `demo-0.0.1-SNAPSHOT.jar` 改成 `star-pivot-controller-0.0.1-SNAPSHOT.jar`。
  2. 确认该 JAR 已上传到当前目录（例如 `backend`），或使用**绝对路径**：  
     `java -jar /opt/star-pivot/star-pivot-controller-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`

### 9.2 报错：`star-pivot-controller-0.0.1-SNAPSHOT.jar 中没有主清单属性`

- **原因**：JAR 里没有 `Main-Class`，不是可执行包。常见情况：
  - 上传错了 JAR（例如传了其他模块的 jar，或旧/损坏的包）。
  - 没有在**项目根目录**用 Maven 打包，导致 Spring Boot 插件没有对 controller 模块做可执行 repackage。
- **处理**：
  1. 在**项目根目录** `StarPivot` 下执行：  
     `mvn clean package -DskipTests`
  2. 只上传并使用这一份 JAR：  
     `star-pivot-controller/target/star-pivot-controller-0.0.1-SNAPSHOT.jar`  
     不要使用 `star-pivot-common`、`star-pivot-system` 等子模块 target 下的 jar。
  3. 若仍报错，在 controller 的 `pom.xml` 中已显式配置了 `<mainClass>com.star.pivot.StarPivotApplication</mainClass>`，重新执行 `mvn clean package -DskipTests` 后再上传、运行。

### 9.3 访问 `/api/auth/captcha` 报错（验证码接口失败）

- **原因**：验证码接口依赖 **Redis** 存储验证码状态。部署到 ECS 后报错（如 500、提示“验证码生成失败，请检查Redis连接”）通常是 Redis 未就绪或配置不正确。
- **处理**：
  1. **确认 Redis 已安装并运行**（与文档第五章「生产环境依赖」一致）：
     ```bash
     # 在 ECS 上执行
     redis-cli ping
     ```
     应返回 `PONG`。若未安装：`yum install redis` 或 `apt install redis-server`，然后 `systemctl start redis`（或 `redis-server`）。
  2. **若使用 prod 环境**，必须设置 Redis 相关环境变量后再启动 JAR：
     ```bash
     export REDIS_HOST="127.0.0.1"   # 若 Redis 在本机
     export REDIS_PORT="6379"
     export REDIS_PASSWORD=""       # 无密码时设为空字符串
     export REDIS_DATABASE="0"
     java -jar star-pivot-controller-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
     ```
     若 Redis 设了密码，则 `export REDIS_PASSWORD="你的Redis密码"`。
  3. **检查安全组与防火墙**：若 Redis 在另一台机器，需放行 6379 端口；同机部署一般无需放行。
  4. **查看后端日志**：启动时或请求验证码时若出现 `Connection refused`、`io.lettuce.core.RedisConnectionException` 等，说明连接 Redis 失败，按上面步骤排查。

### 9.4 验证码接口报错：Fontconfig head is null / 服务器未安装字体

- **原因**：验证码需要绘制文字图片，依赖系统字体。阿里云 ECS 等 minimal Linux 默认未安装 **fontconfig** 和字体包，Java AWT 会报错：`Fontconfig head is null, check your fonts or fonts configuration`，导致 `/api/auth/captcha` 返回 500。
- **处理**：在 ECS 上安装 fontconfig 与至少一种字体后，重启后端服务即可。
  - **CentOS / RHEL / Aliyun Linux**：
    ```bash
    sudo yum install -y fontconfig dejavu-sans-fonts
    # 或
    sudo yum install -y fontconfig liberation-fonts
    ```
  - **Ubuntu / Debian**：
    ```bash
    sudo apt-get update
    sudo apt-get install -y fontconfig fonts-dejavu-core
    ```
  安装完成后执行 `fc-cache -fv`（可选），然后重启 Java 应用，再访问验证码接口。
