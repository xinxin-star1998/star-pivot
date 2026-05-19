package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.bo.WmsWareInfoReqBo;
import com.star.pivot.mall.domain.entity.WmsWareInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WmsWareInfoMapper extends BaseMapper<WmsWareInfo> {
    IPage<WmsWareInfo> selectPageList(
            Page<WmsWareInfo> page, @Param("queryDTO") WmsWareInfoReqBo wmsWareInfoReqBo);
}
