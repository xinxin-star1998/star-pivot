@echo off
chcp 65001 >nul
echo ========================================
echo   StarPivot 前后端一键打包（用于 ECS 部署）
echo ========================================
echo.

echo [1/2] 正在打包后端...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 后端打包失败
    pause
    exit /b 1
)
echo 后端打包完成: star-pivot-controller\target\star-pivot-controller-0.0.1-SNAPSHOT.jar
echo.

echo [2/2] 正在打包前端...
cd star-pivot-ui
call pnpm install
call pnpm run build
cd ..
if %errorlevel% neq 0 (
    echo 前端打包失败
    pause
    exit /b 1
)
echo 前端打包完成: star-pivot-ui\dist\
echo.

echo ========================================
echo   全部打包完成
echo   请将以下内容上传到 ECS：
echo   - star-pivot-controller\target\star-pivot-controller-0.0.1-SNAPSHOT.jar
echo   - star-pivot-ui\dist\ 整个目录
echo   详细步骤见 doc\阿里云ECS部署说明.md
echo ========================================
pause
