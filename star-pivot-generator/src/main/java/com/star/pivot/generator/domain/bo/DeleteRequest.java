package com.star.pivot.generator.domain.bo;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
/**
 * 删除请求对象（支持单删和批量删除）
 * 
 * @author xinxin
 * @date 2026-01-25
 */
@Data
public class DeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 表ID列表
     */
    private List<Long> tableIds;
}
