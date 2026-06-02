package com.star.pivot.generator.domain.dto.external;

import com.star.pivot.framework.domain.PageReqBo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExternalTableQueryDTO extends PageReqBo {

    @NotBlank(message = "sessionId 不能为空")
    private String sessionId;

    private String tableName;

    private String tableComment;
}
