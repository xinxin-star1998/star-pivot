package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.mall.domain.dto.WmsWareSkuDTO;
import com.star.pivot.mall.domain.dto.WmsWareSkuQueryDTO;
import com.star.pivot.mall.domain.entity.WmsWareSku;
import com.star.pivot.mall.domain.vo.WmsWareSkuVO;
import com.star.pivot.mall.mapper.WmsWareSkuMapper;
import com.star.pivot.mall.service.IWmsWareSkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品库存Service业务层实现
 * 
 * @author admin
 * @since 2026-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuMapper, WmsWareSku> implements IWmsWareSkuService
{

    /**
     * 分页查询商品库存列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<WmsWareSkuVO> selectWmsWareSkuPage(WmsWareSkuQueryDTO queryDTO)
    {
        PageResponse<WmsWareSkuVO> pageResponse = new PageResponse<>();
        Page<WmsWareSku> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<WmsWareSku> wmsWareSkuPage = baseMapper.selectPageList(page, queryDTO);
        
        // 转换为VO
        java.util.List<WmsWareSkuVO> voList = wmsWareSkuPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
        
        pageResponse.setTotal(wmsWareSkuPage.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(wmsWareSkuPage.getCurrent());
        pageResponse.setPageSize(wmsWareSkuPage.getSize());
        pageResponse.setPageCount(wmsWareSkuPage.getPages());
        return pageResponse;
    }

    /**
     * 根据主键查询商品库存详细信息
     * 
     * @param id 商品库存主键
     * @return 商品库存信息
     */
    @Override
    public WmsWareSkuVO selectWmsWareSkuById(Long id)
    {
        WmsWareSku wmsWareSku = this.getById(id);
        if (wmsWareSku == null) {
            throw new BizException("商品库存不存在");
        }
        return convertToVO(wmsWareSku);
    }

    /**
     * 新增商品库存
     * 
     * @param wmsWareSkuDTO 商品库存信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertWmsWareSku(WmsWareSkuDTO wmsWareSkuDTO)
    {
        WmsWareSku wmsWareSku = new WmsWareSku();
        BeanUtils.copyProperties(wmsWareSkuDTO, wmsWareSku);
        return this.save(wmsWareSku);
    }

    /**
     * 修改商品库存
     * 
     * @param wmsWareSkuDTO 商品库存信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWmsWareSku(WmsWareSkuDTO wmsWareSkuDTO)
    {
        WmsWareSku wmsWareSku = this.getById(wmsWareSkuDTO.getId());
        if (wmsWareSku == null) {
            throw new BizException("商品库存不存在");
        }
        
        BeanUtils.copyProperties(wmsWareSkuDTO, wmsWareSku, "id");
        return this.updateById(wmsWareSku);
    }

    /**
     * 批量删除商品库存
     * 
     * @param ids 需要删除的商品库存主键数组
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteWmsWareSkuByIds(Long[] ids)
    {
        return this.removeByIds(java.util.Arrays.asList(ids));
    }

    /**
     * 转换为VO
     * 
     * @param wmsWareSku 实体对象
     * @return VO对象
     */
    private WmsWareSkuVO convertToVO(WmsWareSku wmsWareSku)
    {
        WmsWareSkuVO vo = new WmsWareSkuVO();
        BeanUtils.copyProperties(wmsWareSku, vo);
        return vo;
    }
}
