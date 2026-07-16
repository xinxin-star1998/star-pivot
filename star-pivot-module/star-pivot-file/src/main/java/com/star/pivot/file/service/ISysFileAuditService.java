package com.star.pivot.file.service;

import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.domain.dto.SysFileAuditQueryDTO;
import com.star.pivot.file.domain.vo.SysFileAuditVo;
import com.star.pivot.framework.domain.PageResponse;

public interface ISysFileAuditService {

    void record(FileAuditAction action, Long fileId, String fileName, String detail);

    void record(FileAuditAction action, Long fileId, String fileName, String detail, String operIp);

    PageResponse<SysFileAuditVo> pageList(SysFileAuditQueryDTO queryDTO);
}
