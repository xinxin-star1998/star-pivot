# 安全策略

## 支持的版本

| 版本   | 支持状态 |
|--------|----------|
| 最新 `main` / `master` 分支 | ✅ 支持 |
| `v1.0.0` 及后续 tag | ✅ 支持 |
| 更早版本 | ❌ 不再维护 |

## 报告漏洞

如果你发现了 StarPivot 的安全漏洞，**请勿在公开 Issue 中披露**。

请通过以下任一方式私下报告：

1. **Gitee 私信**：向仓库维护者发送私信  
   仓库：[https://gitee.com/xin1998/StarPivot](https://gitee.com/xin1998/StarPivot)
2. **GitHub Security Advisory**（如已启用）：  
   [https://github.com/xinxin-star1998/star-pivot/security/advisories/new](https://github.com/xinxin-star1998/star-pivot/security/advisories/new)
3. **邮件**：在 Gitee/GitHub Issue 中联系维护者获取安全报告邮箱

### 报告时请尽量包含

- 漏洞类型（如 SQL 注入、XSS、越权访问、认证绕过等）
- 受影响的模块或接口路径
- 复现步骤（PoC 或截图）
- 影响范围评估
- 你使用的 StarPivot 版本或 commit hash
- 运行环境（JDK、MySQL、Redis、操作系统）

## 响应流程

1. **确认**：维护者在 3 个工作日内确认收到报告
2. **评估**：评估严重等级与影响范围
3. **修复**：在私有分支修复并验证
4. **发布**：发布补丁版本或安全公告
5. **致谢**：经你同意后，在 CHANGELOG 或 Release Notes 中致谢

## 安全最佳实践（部署方）

- 生产环境修改默认账号密码（`admin` / `admin123`）
- 通过环境变量注入 JWT 密钥、数据库密码、Redis 密码
- 启用 HTTPS，配置合理的 CORS 白名单
- 定期更新依赖，关注 [Dependabot / 安全公告](https://github.com/xinxin-star1998/star-pivot/security)
- 演示环境（[starpivot.org.cn](https://www.starpivot.org.cn/)）请勿录入真实敏感数据

## 范围说明

以下通常**不在**本仓库安全报告范围内：

- 第三方服务（阿里云 OSS、MySQL、Redis）的配置不当
- 仅影响演示环境的已知默认凭据
- 社会工程学攻击
- 拒绝服务（DoS）攻击（除非存在明显的代码层缺陷）

如有疑问，仍欢迎先行联系维护者确认。
