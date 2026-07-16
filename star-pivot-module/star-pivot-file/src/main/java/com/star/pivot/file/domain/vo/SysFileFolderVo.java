package com.star.pivot.file.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysFileFolderVo {

    private Long folderId;

    private String category;

    private String folderName;

    private Long parentId;

    private Integer orderNum;

    private String status;

    private Long fileCount;

    private List<SysFileFolderVo> children = new ArrayList<>();
}
