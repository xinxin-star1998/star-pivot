package com.star.pivot.file.constant;

import lombok.Getter;

@Getter
public enum FileAuditAction {
    UPLOAD("上传"),
    DOWNLOAD("下载"),
    DELETE("移入回收站"),
    RESTORE("恢复"),
    PURGE("彻底删除"),
    MOVE("迁移"),
    RENAME("重命名"),
    SHARE("创建分享"),
    SHARE_REVOKE("取消分享"),
    VERSION_UPLOAD("上传新版本"),
    VERSION_RESTORE("恢复版本"),
    FAVORITE("收藏变更"),
    TAG("打标/摘标"),
    ZIP_DOWNLOAD("打包下载");

    private final String label;

    FileAuditAction(String label) {
        this.label = label;
    }
}
