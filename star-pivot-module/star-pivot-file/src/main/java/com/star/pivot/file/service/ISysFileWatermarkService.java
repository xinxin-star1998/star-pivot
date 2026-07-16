package com.star.pivot.file.service;

import com.star.pivot.file.domain.vo.SysFileWatermarkVo;
import com.star.pivot.framework.domain.DataScope;
import jakarta.servlet.http.HttpServletResponse;

public interface ISysFileWatermarkService {

    /** 当前登录用户的水印配置（文案已解析） */
    SysFileWatermarkVo currentConfig();

    /** 指定显示名（分享访客等）的水印配置 */
    SysFileWatermarkVo resolveConfig(String displayName);

    /** 下载带水印的图片；非图片或关闭时回退原图 */
    void downloadWatermarked(Long fileId, DataScope dataScope, HttpServletResponse response);
}
