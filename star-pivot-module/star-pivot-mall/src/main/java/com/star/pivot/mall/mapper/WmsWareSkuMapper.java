package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.dto.WmsWareSkuQueryDTO;
import com.star.pivot.mall.domain.entity.WmsWareSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存Mapper接口
 * 
 * @author admin
 * @since 2026-05-22
 */
@Mapper
public interface WmsWareSkuMapper extends BaseMapper<WmsWareSku>
{
    /**
     * 分页查询商品库存列表
     * 
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<WmsWareSku> selectPageList(Page<WmsWareSku> page, @Param("queryDTO" ) WmsWareSkuQueryDTO queryDTO);
}
