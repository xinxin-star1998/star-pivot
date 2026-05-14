package com.star.pivot.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    IPage<Product> selectPageList(Page<Product> page, ProductReqBo productReqBo);
}
