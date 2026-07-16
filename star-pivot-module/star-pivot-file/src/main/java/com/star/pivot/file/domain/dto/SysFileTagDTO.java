package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SysFileTagDTO {

    private Long tagId;

    @NotBlank(message = "标签名不能为空")
    @Size(max = 50, message = "标签名不能超过50个字符")
    private String tagName;

    @Size(max = 20, message = "颜色值过长")
    private String tagColor;

    @Size(max = 255, message = "备注过长")
    private String remark;
}
