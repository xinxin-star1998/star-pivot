package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.file.domain.entity.SysFileVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysFileVersionMapper extends BaseMapper<SysFileVersion> {

    Integer selectMaxVersionNo(@Param("fileId") Long fileId);

    long countByObjectName(@Param("objectName") String objectName, @Param("excludeVersionId") Long excludeVersionId);

    int deleteByFileId(@Param("fileId") Long fileId);
}
