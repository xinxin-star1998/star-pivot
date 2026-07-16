package com.star.pivot.file.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysFileHashCheckVo {

    /** 是否可秒传 */
    private boolean instant;

    /** 秒传成功后的文件信息 */
    private SysFileVo file;
}
