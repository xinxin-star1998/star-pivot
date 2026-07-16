package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.file.domain.dto.SysFileAuditQueryDTO;
import com.star.pivot.file.domain.entity.SysFileAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysFileAuditMapper extends BaseMapper<SysFileAudit> {

    IPage<SysFileAudit> selectAuditPage(Page<SysFileAudit> page, @Param("query") SysFileAuditQueryDTO query);
}
