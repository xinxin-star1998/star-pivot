# ============================================
# StarPivot Docker 快速启动脚本 (PowerShell)
# 适用于 Windows 10/11 + PowerShell 5.1+
# ============================================

param(
    [ValidateSet("start", "stop", "restart", "logs", "build", "clean", "status", "dev", "help")]
    [string]$Command = "start",

    [string]$Service = "star-pivot"
)

$ErrorActionPreference = "Stop"

function Write-ColorOutput($ForegroundColor) {
    $fc = $host.UI.RawUI.ForegroundColor
    $host.UI.RawUI.ForegroundColor = $ForegroundColor
    if ($args) { Write-Output ($args -join ' ') }
    $host.UI.RawUI.ForegroundColor = $fc
}

function Show-Help {
    Write-Output ""
    Write-Output "用法: .\docker-start.ps1 [-Command <命令>] [-Service <服务名>]"
    Write-Output ""
    Write-Output "命令:"
    Write-Output "  start       启动所有服务（默认，生产模式）"
    Write-Output "  dev         启动开发环境（含热重载 + 调试端口）"
    Write-Output "  stop        停止所有服务"
    Write-Output "  restart     重启所有服务"
    Write-Output "  logs        查看日志（可通过 -Service 指定服务）"
    Write-Output "  build       重新构建镜像（无缓存）"
    Write-Output "  clean       清理所有容器和数据卷（需确认）"
    Write-Output "  status      查看服务状态"
    Write-Output "  help        显示帮助信息"
    Write-Output ""
    Write-Output "示例:"
    Write-Output "  .\docker-start.ps1 -Command start"
    Write-Output "  .\docker-start.ps1 -Command dev"
    Write-Output "  .\docker-start.ps1 -Command logs -Service mysql"
    Write-Output "  .\docker-start.ps1 -Command clean"
    Write-Output ""
}

# 检查 Docker 是否安装
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-ColorOutput Red "错误: Docker 未安装，请访问 https://www.docker.com/products/docker-desktop/ 下载安装"
    exit 1
}

# 检查 Docker 是否运行
try {
    $null = docker info 2>&1
    if ($LASTEXITCODE -ne 0) { throw }
} catch {
    Write-ColorOutput Red "错误: Docker 未运行，请先启动 Docker Desktop"
    exit 1
}

# 检查 .env 文件
$envFile = Join-Path $PSScriptRoot ".env"
$envExample = Join-Path $PSScriptRoot " .env.example"
if (-not (Test-Path $envFile)) {
    if (Test-Path $envExample) {
        Write-ColorOutput Yellow "警告: .env 文件不存在，从 .env.example 复制..."
        Copy-Item $envExample $envFile
        Write-ColorOutput Yellow "请编辑 .env 文件配置环境变量后重新运行"
    } else {
        Write-ColorOutput Red "错误: .env 和 .env.example 文件均不存在"
    }
    exit 1
}

# 切换到项目目录
Push-Location $PSScriptRoot

try {
    switch ($Command) {
        "start" {
            Write-ColorOutput Green "========================================"
            Write-ColorOutput Green "  StarPivot Docker 启动（生产模式）"
            Write-ColorOutput Green "========================================"
            docker compose up -d
            if ($LASTEXITCODE -eq 0) {
                Write-ColorOutput Green "服务启动成功！"
                Write-Output ""
                Write-Output "  应用地址:  http://localhost:8080"
                Write-Output "  API 文档:  http://localhost:8080/api/doc.html"
                Write-Output "  健康检查:  http://localhost:8080/api/actuator/health"
                Write-Output ""
                Write-Output "查看日志: .\docker-start.ps1 -Command logs"
            }
        }

        "dev" {
            Write-ColorOutput Green "========================================"
            Write-ColorOutput Green "  StarPivot Docker 启动（开发模式）"
            Write-ColorOutput Green "========================================"
            docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d
            if ($LASTEXITCODE -eq 0) {
                Write-ColorOutput Green "开发环境启动成功！"
                Write-Output ""
                Write-Output "  应用地址:   http://localhost:8080"
                Write-Output "  调试端口:   localhost:5005（IDE Remote Debug）"
                Write-Output "  API 文档:   http://localhost:8080/api/doc.html"
                Write-Output ""
                Write-Output "如需 Adminer 数据库管理工具:"
                Write-Output "  docker compose -f docker-compose.yml -f docker-compose.dev.yml --profile dev-tools up -d adminer"
                Write-Output "  访问: http://localhost:8081"
            }
        }

        "stop" {
            Write-ColorOutput Yellow "停止 StarPivot 服务..."
            docker compose down
            Write-ColorOutput Green "服务已停止"
        }

        "restart" {
            Write-ColorOutput Yellow "重启 StarPivot 服务..."
            docker compose restart
            Write-ColorOutput Green "服务已重启"
        }

        "logs" {
            Write-ColorOutput Green "查看 $Service 日志（Ctrl+C 退出）..."
            docker compose logs -f $Service
        }

        "build" {
            Write-ColorOutput Green "重新构建镜像（无缓存）..."
            docker compose build --no-cache
            Write-ColorOutput Green "构建完成"
        }

        "clean" {
            Write-ColorOutput Red "警告: 这将删除所有容器和数据卷（MySQL 数据、Redis 数据等）！"
            $confirm = Read-Host "确认继续？(y/N)"
            if ($confirm -match '^[yY]$') {
                docker compose down -v
                Write-ColorOutput Green "清理完成"
            } else {
                Write-Output "取消操作"
            }
        }

        "status" {
            docker compose ps
        }

        "help" {
            Show-Help
        }
    }
} finally {
    Pop-Location
}
