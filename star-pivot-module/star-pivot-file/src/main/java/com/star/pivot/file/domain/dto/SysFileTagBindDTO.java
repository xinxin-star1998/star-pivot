package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SysFileTagBindDTO {

    @NotEmpty(message = "文件ID不能为空")
    private List<Long> fileIds;

    @NotEmpty(message = "标签ID不能为空")
    private List<Long> tagIds;
}
