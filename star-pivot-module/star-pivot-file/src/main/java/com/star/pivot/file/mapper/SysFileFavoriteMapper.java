package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.file.domain.entity.SysFileFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysFileFavoriteMapper extends BaseMapper<SysFileFavorite> {

    List<Long> selectFileIdsByUserAndFileIds(@Param("userId") Long userId, @Param("fileIds") Collection<Long> fileIds);
}
