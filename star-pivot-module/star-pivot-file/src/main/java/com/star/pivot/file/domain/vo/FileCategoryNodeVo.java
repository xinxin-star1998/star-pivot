package com.star.pivot.file.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileCategoryNodeVo {

    private String category;

    private String categoryLabel;

    private Long defaultFolderId;

    private List<SysFileFolderVo> children;
}
