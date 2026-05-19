package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.bo.SkuReqBo;
import com.star.pivot.mall.domain.entity.PmsSkuInfo;
import com.star.pivot.mall.domain.vo.SkuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PmsSkuInfoMapper extends BaseMapper<PmsSkuInfo> {

    IPage<SkuVo> selectPageList(Page<SkuVo> page, @Param("req") SkuReqBo reqBo);
}
