package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.entity.PmsBrand;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PmsBrandMapper extends BaseMapper<PmsBrand> {

    IPage<PmsBrand> selectPageList(Page<PmsBrand> page, BrandReqBo brandReqBo);
}
