@echo off
chcp 65001 >nul
title StarPivot 推送到 Gitee / GitHub

set "ROOT=%~dp0"
set "ROOT=%ROOT:~0,-1%"
cd /d "%ROOT%"

if not exist "%ROOT%\.git" (
    echo [错误] 请在 StarPivot 仓库根目录运行此脚本。
    pause
    exit /b 1
)

echo ========================================
echo   StarPivot 双平台推送 (Gitee + GitHub)
echo ========================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%ROOT%\push-remotes.ps1" %*
set "EXIT_CODE=%ERRORLEVEL%"

if %EXIT_CODE% neq 0 (
    echo.
    echo 推送未全部成功，退出码: %EXIT_CODE%
    pause
    exit /b %EXIT_CODE%
)

echo.
echo 推送完成。
pause
exit /b 0
