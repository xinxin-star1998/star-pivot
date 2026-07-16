package com.star.pivot.file.service;

import com.star.pivot.file.domain.dto.*;
import com.star.pivot.file.domain.vo.SysFileHashCheckVo;
import com.star.pivot.file.domain.vo.SysFileMultipartInitVo;
import com.star.pivot.file.domain.vo.SysFileUsageSummaryVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ISysFileService {

    SysFileVo upload(MultipartFile file, Long folderId, SysFileUploadDTO uploadDTO);

    SysFileHashCheckVo checkHashAndInstantUpload(SysFileHashCheckDTO dto, SysFileUploadDTO owner);

    SysFileMultipartInitVo initMultipart(SysFileMultipartInitDTO dto, SysFileUploadDTO owner);

    Map<String, String> uploadMultipartPart(String uploadId, String objectName, int partNumber, MultipartFile chunk);

    SysFileVo completeMultipart(SysFileMultipartCompleteDTO dto, SysFileUploadDTO owner);

    void abortMultipart(String uploadId, String objectName);

    SysFileMultipartInitVo multipartStatus(String uploadId, String objectName);

    PageResponse<SysFileVo> pageList(SysFileQueryDTO queryDTO);

    SysFileVo getDetail(Long fileId, DataScope dataScope);

    void logicDelete(List<Long> ids, DataScope dataScope);

    void restore(List<Long> ids, DataScope dataScope);

    PageResponse<SysFileVo> recyclePage(SysFileRecycleQueryDTO queryDTO);

    void purge(List<Long> ids, DataScope dataScope);

    int clearRecycle(DataScope dataScope);

    Map<String, String> previewUrl(Long fileId, DataScope dataScope);

    void moveToFolder(List<Long> ids, Long targetFolderId, DataScope dataScope);

    void rename(Long fileId, String fileName, DataScope dataScope);

    /** 批量打包下载 ZIP */
    void downloadZip(List<Long> ids, DataScope dataScope, HttpServletResponse response);

    SysFileUsageSummaryVo usageStats(SysFileUsageQueryDTO queryDTO);
}
