package com.star.pivot.file.service;

import com.star.pivot.file.domain.dto.SysFileFolderDTO;
import com.star.pivot.file.domain.vo.FileCategoryNodeVo;

import java.util.List;

public interface ISysFileFolderService {

    List<FileCategoryNodeVo> listTree(String category);

    Long create(SysFileFolderDTO dto);

    void update(SysFileFolderDTO dto);

    void delete(Long folderId);
}
