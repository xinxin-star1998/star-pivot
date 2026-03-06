#!/bin/sh
# Husky 安装脚本

echo "Installing Husky..."

# 检查是否在项目根目录
if [ ! -f "package.json" ]; then
  echo "Error: package.json not found. Please run this script from the project root."
  exit 1
fi

# 安装 Husky
npm install -D husky

# 初始化 Husky
npx husky install

# 创建 hooks 目录
mkdir -p .husky

# 设置 Git hooks 路径
git config core.hooksPath .husky

echo "✅ Husky installed successfully!"
echo ""
echo "Git hooks have been configured:"
echo "  - pre-commit: Run code quality checks before commit"
echo "  - pre-push: Run tests and checks before push"
echo ""
echo "To disable hooks temporarily, use:"
echo "  git commit --no-verify"
echo "  git push --no-verify"
