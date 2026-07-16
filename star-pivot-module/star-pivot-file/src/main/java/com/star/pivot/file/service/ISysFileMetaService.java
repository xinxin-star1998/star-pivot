package com.star.pivot.file.service;

import com.star.pivot.file.domain.dto.SysFileTagBindDTO;
import com.star.pivot.file.domain.dto.SysFileTagDTO;
import com.star.pivot.file.domain.vo.SysFileTagVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.framework.domain.DataScope;

import java.util.List;
import java.util.Map;

public interface ISysFileMetaService {

    /** 切换收藏，返回收藏后状态 */
    boolean toggleFavorite(Long fileId, DataScope dataScope);

    void touchRecent(Long fileId, DataScope dataScope);

    List<SysFileTagVo> listMyTags();

    SysFileTagVo createTag(SysFileTagDTO dto);

    SysFileTagVo updateTag(SysFileTagDTO dto);

    void deleteTag(Long tagId);

    void bindTags(SysFileTagBindDTO dto, DataScope dataScope);

    void unbindTags(SysFileTagBindDTO dto, DataScope dataScope);

    /** 填充 favorited / tags */
    void enrichList(List<SysFileVo> rows);

    Map<String, Object> favoriteStatus(Long fileId);
}
