package com.star.pivot.file.service;

import com.star.pivot.file.domain.dto.SysFileQueryDTO;
import com.star.pivot.file.domain.dto.SysFileRecycleQueryDTO;
import com.star.pivot.file.domain.dto.SysFileUploadDTO;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.framework.domain.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ISysFileService {

    SysFileVo upload(MultipartFile file, Long folderId, SysFileUploadDTO uploadDTO);

    PageResponse<SysFileVo> pageList(SysFileQueryDTO queryDTO);

    SysFileVo getDetail(Long fileId);

    void logicDelete(List<Long> ids);

    void restore(List<Long> ids);

    PageResponse<SysFileVo> recyclePage(SysFileRecycleQueryDTO queryDTO);

    Map<String, String> previewUrl(Long fileId);

    void moveToFolder(List<Long> ids, Long targetFolderId);

    void rename(Long fileId, String fileName);
}
