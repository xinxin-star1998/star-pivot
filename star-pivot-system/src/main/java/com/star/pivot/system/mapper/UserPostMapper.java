package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.UserPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户岗位关联Mapper接口
 *
 * @author stardust
 */
@Mapper
public interface UserPostMapper extends BaseMapper<UserPost> {
}

