package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysFileFolderDTO {

    private Long folderId;

    @NotBlank(message = "业务分类不能为空")
    private String category;

    private String folderName;

    /** 父文件夹 ID，0 或空表示分类下根级 */
    private Long parentId;

    private Integer orderNum;

    private String status;

    private String remark;
}
