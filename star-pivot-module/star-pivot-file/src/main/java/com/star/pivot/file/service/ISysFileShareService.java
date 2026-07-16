package com.star.pivot.file.service;

import com.star.pivot.file.domain.dto.SysFileShareCreateDTO;
import com.star.pivot.file.domain.vo.SysFileSharePublicVo;
import com.star.pivot.file.domain.vo.SysFileShareVo;
import com.star.pivot.framework.domain.DataScope;

import java.util.List;

public interface ISysFileShareService {

    SysFileShareVo create(SysFileShareCreateDTO dto, DataScope dataScope, String publicBaseUrl);

    List<SysFileShareVo> listByFile(Long fileId, DataScope dataScope, String publicBaseUrl);

    List<SysFileShareVo> listMine(String publicBaseUrl);

    void revoke(Long shareId);

    SysFileSharePublicVo meta(String shareCode);

    SysFileSharePublicVo unlock(String shareCode, String password);
}
