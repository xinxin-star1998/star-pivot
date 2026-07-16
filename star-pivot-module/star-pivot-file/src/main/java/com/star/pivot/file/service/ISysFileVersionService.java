package com.star.pivot.file.service;

import com.star.pivot.file.domain.vo.SysFileVersionVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.framework.domain.DataScope;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISysFileVersionService {

    List<SysFileVersionVo> listVersions(Long fileId, DataScope dataScope);

    SysFileVo uploadVersion(Long fileId, MultipartFile file, String remark, DataScope dataScope);

    SysFileVo restoreVersion(Long fileId, Long versionId, DataScope dataScope);

    void deleteVersion(Long fileId, Long versionId, DataScope dataScope);

    /** 彻底删除文件时清理历史版本与无引用 OSS */
    void purgeVersionsOfFile(Long fileId, String currentObjectName);
}
