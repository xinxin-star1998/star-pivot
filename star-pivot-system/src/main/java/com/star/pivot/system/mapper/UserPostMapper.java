package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.UserPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户岗位关联Mapper接口
 *
 * @author stardust
 */
@Mapper
public interface UserPostMapper extends BaseMapper<UserPost> {
    /**
     * 批量插入用户岗位关联关系
     *
     * @param list 用户岗位关联集合
     * @return 影响行数
     */
    int insertBatchUserPosts(@Param("list") java.util.List<UserPost> list);
}

