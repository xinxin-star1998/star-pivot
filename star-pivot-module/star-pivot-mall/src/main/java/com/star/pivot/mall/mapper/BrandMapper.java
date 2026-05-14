package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.entity.Brand;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {
    IPage<Brand> selectPageList(Page<Brand> page, BrandReqBo brandReqBo);
}
