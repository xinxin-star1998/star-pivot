package com.star.pivot.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.file.domain.dto.SysFileQueryDTO;
import com.star.pivot.file.domain.dto.SysFileRecycleQueryDTO;
import com.star.pivot.file.domain.dto.SysFileUsageQueryDTO;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.vo.SysFileUsageStatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {

    IPage<SysFile> selectPageList(Page<SysFile> page, @Param("query") SysFileQueryDTO query);

    IPage<SysFile> selectRecyclePageList(Page<SysFile> page, @Param("query") SysFileRecycleQueryDTO query);

    long countActiveByFolderId(@Param("folderId") Long folderId);

    long countByObjectName(@Param("objectName") String objectName, @Param("excludeFileId") Long excludeFileId);

    SysFile selectByHashAndSize(@Param("fileHash") String fileHash, @Param("fileSize") Long fileSize);

    List<SysFile> selectRecycleByIds(@Param("ids") List<Long> ids);

    List<Long> selectAllRecycleIds(@Param("query") SysFileRecycleQueryDTO query, @Param("limit") int limit);

    int restoreByIds(@Param("ids") List<Long> ids, @Param("updateBy") String updateBy);

    int physicalDeleteByIds(@Param("ids") List<Long> ids);

    SysFileUsageStatVo selectUsageSummary(@Param("query") SysFileUsageQueryDTO query);

    List<SysFileUsageStatVo> selectUsageGroupByUser(@Param("query") SysFileUsageQueryDTO query);

    List<SysFileUsageStatVo> selectUsageGroupByDept(@Param("query") SysFileUsageQueryDTO query);
}
