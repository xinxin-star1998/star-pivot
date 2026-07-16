package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.file.domain.entity.SysFileRecent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysFileRecentMapper extends BaseMapper<SysFileRecent> {

    int upsert(@Param("userId") Long userId, @Param("fileId") Long fileId);

    List<Long> selectOverflowRecentIds(@Param("userId") Long userId, @Param("keep") int keep);
}
