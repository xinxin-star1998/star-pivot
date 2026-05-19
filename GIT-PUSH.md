# StarPivot 提交与推送（Gitee / GitHub）

仓库根目录：`StarPivot`。远程约定：

| 远程     | 平台   | 地址 |
|----------|--------|------|
| `origin` | Gitee  | https://gitee.com/xin1998/StarPivot.git |
| `github` | GitHub | https://github.com/xinxin-star1998/star-pivot.git |

---

## 一、首次在本机配置（只需一次）

在 `StarPivot` 目录打开终端：

```powershell
cd E:\star-pivot\project0422\StarPivot

git remote set-url origin https://gitee.com/xin1998/StarPivot.git
git config --unset-all remote.origin.pushurl

git remote add github https://github.com/xinxin-star1998/star-pivot.git
# 若提示 github 已存在，改用：
# git remote set-url github https://github.com/xinxin-star1998/star-pivot.git

git remote -v
```

---

## 二、每次改完代码：提交 + 双平台推送

把 `master` 换成你当前分支名（如 `develop`）。

### 1. 查看改动

```powershell
cd E:\star-pivot\project0422\StarPivot
git status
```

### 2. 暂存并提交

提交说明须符合 [Conventional Commits](https://www.conventionalcommits.org/)（项目 commitlint 会校验），例如：

- `feat: 新增仓库管理`
- `fix: 修复地区树加载`
- `chore: 更新依赖`

```powershell
git add .
git commit -m "feat: 你的提交说明"
```

只提交部分文件时：

```powershell
git add 路径/到/文件
git commit -m "fix: 你的提交说明"
```

### 3. 推送到 Gitee

```powershell
git push origin master
```

### 4. 推送到 GitHub

```powershell
git push github master
```

### 5. 一条龙（复制整段）

```powershell
cd E:\star-pivot\project0422\StarPivot
git add .
git commit -m "feat: 你的提交说明"
git push origin master
git push github master
```

---

## 三、GitHub 提示 rejected / fetch first

说明 GitHub 上有你本地没有的提交（例如在网页合并过 PR）。先合并再推：

```powershell
cd E:\star-pivot\project0422\StarPivot
git fetch github master
git merge github/master
git commit -m "chore: sync with GitHub remote"
git push github master
```

有冲突时先解决冲突，再 `git add .` → `git commit -m "chore: resolve merge conflicts"` → `git push github master`。

---

## 四、只推一个平台

```powershell
# 仅 Gitee
git push origin master

# 仅 GitHub
git push github master
```

---

## 五、可选：脚本一键双推

已提供脚本，效果等同「先推 Gitee，再推 GitHub」：

```powershell
.\push-remotes.ps1
# 或双击 push-remotes.bat
```

详见同目录 `push-remotes.ps1`、`push-remotes.bat`。

---

## 六、常用检查

```powershell
git remote -v
git branch
git log -3 --oneline
```
