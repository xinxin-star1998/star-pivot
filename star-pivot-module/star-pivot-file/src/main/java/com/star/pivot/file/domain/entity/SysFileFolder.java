package com.star.pivot.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.pivot.framework.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 文件中心文件夹 sys_file_folder。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file_folder")
public class SysFileFolder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "folder_id", type = IdType.AUTO)
    private Long folderId;

    private String category;

    private String folderName;

    private Long parentId;

    private Integer orderNum;

    private String status;

    /** 0 正常，2 已删除（与 sys_file 一致，手动维护，不走全局 @TableLogic） */
    private String delFlag;
}
