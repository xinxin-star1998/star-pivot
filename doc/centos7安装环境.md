

# centos7安装环境

## 1、下载安装jdk17

```
#进入该目录下
cd /usr/local
#下载
wget https://download.oracle.com/java/17/archive/jdk-17.0.12_linux-x64_bin.tar.gz

# 解压
tar -zxvf jdk-17.0.10_linux-x64_bin.tar.gz
# 重命名方便使用
mv jdk-17.0.10 jdk17
# 配置环境变量
echo 'export JAVA_HOME=/usr/local/jdk17' >> /etc/profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> /etc/profile
# 生效配置
source /etc/profile
# 验证
java -version
```

## 2、下载nginx

```
# 安装Nginx依赖
yum install -y gcc-c++ pcre pcre-devel zlib zlib-devel openssl openssl-devel

# 添加Nginx官方源
rpm -ivh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm

# 安装Nginx
yum install -y nginx

# 启动Nginx
systemctl start nginx

# 设置Nginx开机自启
systemctl enable nginx
Created symlink from /etc/systemd/system/multi-user.target.wants/nginx.service to /usr/lib/systemd/system/nginx.service.
# 验证Nginx是否启动成功（出现active(running)即成功）
systemctl status nginx
```

## 3、下载安装mysql

```
# 下载MySQL8.0源
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
rpm -ivh mysql80-community-release-el7-3.noarch.rpm

# 安装MySQL服务
yum install -y mysql-community-server --nogpgcheck

# 启动MySQL
systemctl start mysqld

# 设置MySQL开机自启
systemctl enable mysqld

# 查看MySQL初始密码（关键！）
grep 'temporary password' /var/log/mysqld.log

# 登录MySQL，替换[初始密码]为上一步查到的密码
mysql -uroot -p[初始密码]

# 进入MySQL后，修改root密码（自定义，如MySql@123456，需符合复杂度）
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Xin@19981223';
# 允许远程连接MySQL（方便本地Navicat连接）修改root允许任意IP访问
use mysql;
update user set host='%' where user='root';
flush privileges;
# 退出MySQL
exit;
```

## 4、下载安装redis

```
#1、安装编译依赖
yum install -y gcc gcc-c++ make wget tcl jemalloc-devel
/2、下载 Redis7.2 官方源码包（国内镜像，高速）
    # 进入/usr/local目录（统一安装目录）
    cd /usr/local
    # 下载Redis7.2.4（最新稳定版，可替换为7.2.x其他版本）
    wget https://download.redis.io/releases/redis-7.2.4.tar.gz
# 解压
tar -zxvf redis-7.2.4.tar.gz
# 进入目录
cd redis-7.2.4
# 编译（指定jemalloc内存分配，性能更优）
make MALLOC=jemalloc
# 安装（指定安装路径/usr/local/redis72）
make PREFIX=/usr/local/redis72 install

# 一键删除/usr/local/bin下所有redis开头的可执行文件（核心回退命令）
rm -rf /usr/local/bin/redis-*

# 先进入你的Redis源码根目录（替换成你实际的路径，比如cd /root/redis-7.2.4）
cd 你的redis源码目录路径
# 一键清理make编译的所有临时产物（保留源码本身，不影响重新编译）
make clean
4：复制配置文件并做基础配置（关键）
	# 复制官方配置文件到安装目录，方便管理
    cp redis.conf /usr/local/redis72/
    # 进入安装目录
    cd /usr/local/redis72/
    # 修改配置文件（后台运行+允许阿里云内网/本地连接，禁止外网直接访问）
    sed -i 's/daemonize no/daemonize yes/' redis.conf
    sed -i 's/bind 127.0.0.1 -::1/bind 0.0.0.0/' redis.conf  # 允许ECS内网IP访问
    sed -i 's/protected-mode yes/protected-mode no/' redis.conf  # 关闭保护模式（配合bind使用）
5：启动 Redis 并设置开机自启
	5.1制作 systemd 服务（推荐，开机自启，统一管理）
		# 创建redis72服务文件
			vi /usr/lib/systemd/system/redis72.service
# 重新加载服务配置
systemctl daemon-reload
# 启动Redis72
systemctl start redis72
# 设置开机自启
systemctl enable redis72
# 查看运行状态（显示active(running)即成功）
systemctl status redis72
```

## 5、部署后端 Java 项目（SpringBoot Jar 包为例，最常用）

```
# 创建项目根目录
mkdir -p /usr/local/project
# 进入后端目录
cd /usr/local/project
mkdir backend
cd backend


# 进入后端Jar包目录
cd /usr/local/project/backend
# 后台运行Jar包，指定端口（若配置文件已指定，可省略--server.port=8080），输出日志到nohup.out
nohup java -jar star-pivot-controller-0.0.1-SNAPSHOT.jar --server.port=8080 --spring.datasource.url=jdbc:mysql://101.201.181.191:3306/star_pivot_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=Xin@19981223 > nohup.out 2>&1 &

java -Djava.awt.headless=true -Dsun.font.fontmanager=sun.awt.font.NativeFontManager -jar star-pivot-controller-0.0.1-SNAPSHOT.jar


# 验证后端服务是否启动成功（查看8080端口是否被占用）
netstat -tlnp | grep 8080
# 若输出类似 java  1234  root  4u  IPv6  12345      0t0  TCP *:8080 (LISTEN)，说明启动成功
#杀死进程
kill  pid
# 查看日志（排查启动失败问题）
tail -f nohup.out
```

## 6、部署前端静态项目（Vue/React/ 静态页面）

```
# 创建前端目录
mkdir -p /usr/local/project/frontend
cd /usr/local/project/frontend
```

将本地`dist.zip`拖拽到该目录，然后解压：

```
# 解压zip包（若为tar.gz包，用tar -zxvf dist.tar.gz）
unzip dist.zip
# 解压后会出现dist文件夹，里面是所有前端静态资源（index.html、css、js等）
```

### 6.2 配置 Nginx（核心：托管前端 + 反向代理解决跨域）

Nginx 的核心配置文件是`/etc/nginx/nginx.conf`，**不建议直接修改主配置**，而是修改**站点配置**（更规范，方便管理）。

```
# 进入Nginx站点配置目录
cd /etc/nginx/conf.d
# 新建项目的Nginx配置文件（自定义名称，如demo.conf）
vim demo.conf
```

```
# 前端访问端口（默认80，用户直接输入公网IP即可访问）
server {
    listen 80;
    # 服务器域名（若有域名，填域名，如www.demo.com；无域名填ECS公网IP）
    server_name 你的ECS公网IP;

    # 配置前端静态资源根目录（**修改为你解压后的dist文件夹路径**）
    root /usr/local/project/frontend/dist;
    # 前端入口文件
    index index.html index.htm;

    # 解决Vue/React路由刷新404问题
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 反向代理：将/api开头的请求转发到Java后端服务（**核心，解决跨域**）
    location /api/ {
        # 转发到后端服务地址（**修改为你的后端IP+端口**，本地部署填127.0.0.1）
        proxy_pass http://127.0.0.1:8080/;
        # 以下是反向代理的通用配置，保留即可
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60;
        proxy_read_timeout 600;
        proxy_send_timeout 600;
    }

    # 静态资源缓存（优化访问速度）
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg)$ {
        expires 7d;
        add_header Cache-Control "public, max-age=604800";
    }
}
```

```
# 检查Nginx配置语法（出现nginx: configuration file /etc/nginx/nginx.conf test is successful即成功）
nginx -t

# 重启Nginx
systemctl restart nginx

# 验证Nginx状态
systemctl status nginx
```

##  配置 Java 服务开机自启（避免服务器重启后服务停止）

```
# 创建服务文件
vim /etc/systemd/system/demo-backend.service

[Unit]
Description=Java Backend Service
After=network.target mysqld.service

[Service]
Type=simple
# 运行用户（推荐用普通用户，这里暂时用root）
User=root
# Jar包所在目录
WorkingDirectory=/usr/local/project/backend
# 启动命令（和你后台运行的命令一致）
ExecStart=/usr/local/jdk17/bin/java -jar star-pivot-controller-0.0.1-SNAPSHOT.jar --server.port=8080
# 服务异常时自动重启
Restart=on-failure
RestartSec=5s

[Install]
WantedBy=multi-user.target


# 重新加载systemd配置
systemctl daemon-reload
# 设置开机自启
systemctl enable demo-backend
# 启动服务（替代之前的nohup命令）
systemctl start demo-backend
# 查看服务状态
systemctl status demo-backend
```

## 第五步：进阶配置（生产环境必备，可选）

### 5.1 配置域名（替换公网 IP，更友好）

1. 购买域名：阿里云域名控制台（https://wanwang.aliyun.com/）购买域名（如`demo.com`）。
2. 域名解析：进入域名控制台 → 解析 → 添加 A 记录，主机记录填`www`/ 空，记录值填 ECS 公网 IP，解析线路默认。
3. 修改 Nginx 配置：将`server_name`从公网 IP 改为你的域名（如`www.demo.com`），重启 Nginx。
4. 域名备案：阿里云国内服务器必须备案，否则域名无法访问，备案入口在阿里云控制台【ICP 备案】，按流程操作即可（约 1-2 周）。

### 5.2 配置 HTTPS（加密访问，提升安全性）

阿里云提供**免费 SSL 证书**，步骤：

1. 阿里云 SSL 证书控制台 → 免费证书 → 申请证书，绑定你的域名。
2. 下载证书（选择 Nginx 版本），得到`xxx.pem`和`xxx.key`文件。
3. 在服务器创建证书目录：`mkdir -p /etc/nginx/ssl`，将证书文件上传到该目录。
4. 修改 Nginx 的`demo.conf`，添加 443 端口配置（HTTPS），参考：

```
# HTTPS配置
server {
    listen 443 ssl;
    server_name www.demo.com;
    # 证书文件路径
    ssl_certificate /etc/nginx/ssl/xxx.pem;
    ssl_certificate_key /etc/nginx/ssl/xxx.key;
    # SSL通用配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_prefer_server_ciphers on;

    # 前端静态资源配置（和80端口一致）
    root /usr/local/project/frontend/dist;
    index index.html index.htm;
    location / {
        try_files $uri $uri/ /index.html;
    }
    # 反向代理接口（和80端口一致）
    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
# 强制将HTTP（80端口）重定向到HTTPS（443端口）
server {
    listen 80;
    server_name www.demo.com;
    rewrite ^(.*)$ https://$host$1 permanent;
}
```

1. 检查 Nginx 配置并重启，此时访问`https://www.demo.com`即可加密访问。