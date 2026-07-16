package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.file.domain.entity.SysFileTagRel;
import com.star.pivot.file.domain.vo.SysFileTagVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysFileTagRelMapper extends BaseMapper<SysFileTagRel> {

    List<SysFileTagVo> selectTagsByFileIds(@Param("userId") Long userId, @Param("fileIds") Collection<Long> fileIds);
}
