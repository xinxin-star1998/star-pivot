@echo off
chcp 65001 >nul
title StarPivot 一键启动

set "ROOT=%~dp0"
set "ROOT=%ROOT:~0,-1%"
set "UI_DIR=%ROOT%\star-pivot-ui"

echo ========================================
echo   StarPivot 前后端一键启动
echo ========================================
echo.

:: 若前端未安装依赖，先自动安装
if not exist "%UI_DIR%\node_modules" (
    echo [0/2] 前端依赖未安装，正在执行 pnpm install...
    cd /d "%UI_DIR%"
    if exist "%UI_DIR%\pnpm-lock.yaml" (pnpm install) else (npm install)
    if errorlevel 1 (
        echo 前端依赖安装失败，请手动在 star-pivot-ui 目录执行 pnpm install 或 npm install
        pause
        exit /b 1
    )
    cd /d "%ROOT%"
    echo 前端依赖安装完成。
    echo.
)

:: 确保 BOM 已安装（首次构建或清理后必须，否则 -am 可能解析不到版本）
echo [1/2] 正在启动后端服务（先安装依赖 BOM）...
start "StarPivot-Backend" cmd /k "cd /d %ROOT% && mvn install -f star-pivot-dependencies\pom.xml -q && mvn spring-boot:run -pl star-pivot-controller -am"
timeout /t 3 /nobreak >nul

:: 在新窗口启动前端（路径不放在引号内，避免 start 解析错误）
echo [2/2] 正在启动前端服务...
start "StarPivot-Frontend" cmd /k "cd /d %UI_DIR% && (pnpm dev 2>nul || npm run dev)"

echo.
echo 前后端已在独立窗口中启动：
echo   - 后端窗口：Spring Boot 服务
echo   - 前端窗口：Vite 开发服务器
echo.
echo 请等待后端启动完成后再访问前端页面。
echo 关闭对应窗口即可停止该服务。
echo.
pause
