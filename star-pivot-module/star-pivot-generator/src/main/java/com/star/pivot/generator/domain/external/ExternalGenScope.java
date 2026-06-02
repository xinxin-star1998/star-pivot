package com.star.pivot.generator.domain.external;

import com.star.pivot.framework.exception.BizException;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 代码生成范围（预览/下载）
 */
@Data
public class ExternalGenScope implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Java + Mapper XML */
    private boolean genBackend = true;

    /** API + Vue */
    private boolean genFrontend = true;

    /** 菜单 SQL */
    private boolean genSql = true;

    public boolean isAllEnabled() {
        return genBackend && genFrontend && genSql;
    }

    public void validate() {
        if (!genBackend && !genFrontend && !genSql) {
            throw new BizException("请至少选择一种生成范围");
        }
    }
}
