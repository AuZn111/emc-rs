# 发布操作步骤（CurseForge / MC百科）

GitHub 仓库与 Release 可以直接由本机 `gh` 完成。CurseForge 和 MC百科需要账号登录，
当前环境没有它们的 API/登录凭据，因此需要你在浏览器里完成最后的上传/提交（材料已全部准备好）。

## 一、GitHub（已可自动完成）

仓库：https://github.com/AuZn111/emc-rs
Release：https://github.com/AuZn111/emc-rs/releases

发布物：

- `dist/emc_rs-3.5.0.jar`
- `docs/cover.png`
- `dist/emc_rs-3.5.0-src.zip`（干净源码，不含前置 jar）

## 二、CurseForge

1. 打开 https://www.curseforge.com/ 并登录。
2. 右上角头像 → **Create Project** → 选择 **Minecraft** → **Mods**。
3. 项目信息：
   - Name：`EMC × Refined Storage`
   - Summary：`AI-created blockless ProjectE × Refined Storage bridge for Forge 1.20.1. Auto EMC buy/sell, live EMC balance, blacklists.`
   - Description：粘贴 `docs/curseforge-description.md` 的内容（可按编辑器格式再排版）。
   - Category：`Storage`、`Automation`、`Technology`（按实际可选分类选择）。
   - License：MIT。
   - Game version：`1.20.1`；Mod loader：`Forge`。
   - Project avatar：上传 `docs/icon.png`；封面/截图：上传 `docs/cover.png`。
4. Relations：
   - `ProjectE` — Required dependency
   - `Refined Storage` — Required dependency
5. 上传文件：
   - File：`dist/emc_rs-3.5.0.jar`
   - Display name：`emc_rs-3.5.0`
   - Release type：`Release`
   - Game versions：`1.20.1`、`Forge`
   - Changelog：粘贴 `CHANGELOG.md` 中 3.5.0 的内容。
6. 在 Description/Summary 里保留 **AI-CREATED** 标注。
7. 提交审核/发布，发布后把 CurseForge 链接补到 README。

注意：**不要上传** `libs/` 里的 ProjectE / Refined Storage 前置 jar；只上传本模组自己的 jar。

## 三、MC百科

1. 打开 https://www.mcmod.cn/ 并登录。
2. 进入“模组”区，选择“申请收录/创建模组”（具体入口可能随站点改版）。
3. 按 `docs/mcmod-entry.md` 填写：
   - 中文名：等价交换 × 精致存储
   - 英文名：EMC × Refined Storage
   - ModID：`emc_rs`
   - 作者：EMC-RS Team（AI 创作）
   - 支持版本：1.20.1；运行环境：Forge
   - 前置：ProjectE、Refined Storage
   - 许可证：MIT
   - 开源/下载：GitHub 链接
4. 简介/正文：粘贴 `docs/mcmod-entry.md` 的“模组简介/使用流程/配置/重要说明”。
5. 上传封面 `docs/cover.png`；封面/图标 `docs/icon.png`。
6. 在简介开头保留 **AI 创作声明**。
7. 提交审核；通过后把 MC百科链接补到 README。

## 四、发布后建议

- 在 GitHub 仓库主页的 About 里补上 CurseForge / MC百科链接。
- 如果 CurseForge 项目审核要求截图，建议进游戏截一张“RS 终端 EMC 余额 + 数量”的图。
- 不要把任何第三方模组 jar 上传到 GitHub/CurseForge/MC百科。
