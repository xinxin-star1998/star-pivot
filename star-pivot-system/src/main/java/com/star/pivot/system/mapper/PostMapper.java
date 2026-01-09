package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位Mapper接口
 *
 * @author stardust
 * @date 2024-01-01
 */
@Mapper
public interface PostMapper extends BaseMapper<SysPost> {
}

