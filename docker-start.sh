#!/bin/bash

# ============================================
# StarPivot Docker 快速启动脚本
# 适用于 Linux / macOS
# Windows 用户请使用 docker-start.ps1
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  StarPivot Docker 快速启动${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查 .env 文件是否存在
if [ ! -f .env ]; then
    echo -e "${YELLOW}警告: .env 文件不存在，从 .env.example 复制...${NC}"
    cp " .env.example" .env 2>/dev/null || cp ".env.example" .env 2>/dev/null || true
    echo -e "${YELLOW}请编辑 .env 文件配置环境变量后重新运行${NC}"
    exit 1
fi

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    exit 1
fi

# 判断使用 docker compose（V2）还是 docker-compose（V1）
if docker compose version &> /dev/null; then
    DC="docker compose"
elif command -v docker-compose &> /dev/null; then
    DC="docker-compose"
else
    echo -e "${RED}错误: Docker Compose 未安装${NC}"
    exit 1
fi

# 函数：显示帮助信息
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  start       启动所有服务（默认，生产模式）"
    echo "  dev         启动开发环境（含热重载 + 调试端口）"
    echo "  stop        停止所有服务"
    echo "  restart     重启所有服务"
    echo "  logs        查看日志（可指定服务名，如: $0 logs mysql）"
    echo "  build       重新构建镜像"
    echo "  clean       清理所有容器和数据卷"
    echo "  status      查看服务状态"
    echo "  help        显示帮助信息"
    echo ""
}

# 主逻辑
case "${1:-start}" in
    start)
        echo -e "${GREEN}启动 StarPivot 服务（生产模式）...${NC}"
        $DC up -d
        echo -e "${GREEN}服务启动成功！${NC}"
        echo -e "应用地址: http://localhost:8080"
        echo -e "API 文档: http://localhost:8080/api/doc.html"
        ;;
    dev)
        echo -e "${GREEN}启动 StarPivot 服务（开发模式）...${NC}"
        $DC -f docker-compose.yml -f docker-compose.dev.yml up -d
        echo -e "${GREEN}开发环境启动成功！${NC}"
        echo -e "应用地址:   http://localhost:8080"
        echo -e "调试端口:   localhost:5005（IDE Remote Debug）"
        echo -e "API 文档:   http://localhost:8080/api/doc.html"
        ;;
    stop)
        echo -e "${YELLOW}停止 StarPivot 服务...${NC}"
        $DC down
        echo -e "${GREEN}服务已停止${NC}"
        ;;
    restart)
        echo -e "${YELLOW}重启 StarPivot 服务...${NC}"
        $DC restart
        echo -e "${GREEN}服务已重启${NC}"
        ;;
    logs)
        $DC logs -f "${2:-star-pivot}"
        ;;
    build)
        echo -e "${GREEN}重新构建镜像...${NC}"
        $DC build --no-cache
        echo -e "${GREEN}构建完成${NC}"
        ;;
    clean)
        echo -e "${RED}警告: 这将删除所有容器和数据卷！${NC}"
        read -p "确认继续？(y/N): " confirm
        if [[ $confirm == [yY] ]]; then
            $DC down -v
            echo -e "${GREEN}清理完成${NC}"
        else
            echo "取消操作"
        fi
        ;;
    status)
        $DC ps
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知命令: $1${NC}"
        show_help
        exit 1
        ;;
esac
