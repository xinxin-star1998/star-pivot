package com.star.pivot.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.ServiceException;
import com.star.pivot.generator.domain.bo.GenTableVO;
import com.star.pivot.generator.domain.dto.GenTableQueryDTO;
import com.star.pivot.generator.domain.entity.GenTable;
import com.star.pivot.generator.domain.entity.GenTableColumn;
import com.star.pivot.generator.mapper.GenTableColumnMapper;
import com.star.pivot.generator.mapper.GenTableMapper;
import com.star.pivot.generator.service.GenTableColumnService;
import com.star.pivot.generator.service.GenTableService;
import com.star.pivot.generator.utils.GenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码生成表服务实现类
 *
 * @author xinxin
 * @since 2025-01-17
 */
@Service
public class GenTableServiceColumnImpl extends ServiceImpl<GenTableColumnMapper, GenTableColumn> implements GenTableColumnService {
    @Autowired
    private GenTableColumnMapper genTableColumnMapper;
    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId)
    {
        return genTableColumnMapper.selectGenTableColumnListByTableId(tableId);
    }
}
