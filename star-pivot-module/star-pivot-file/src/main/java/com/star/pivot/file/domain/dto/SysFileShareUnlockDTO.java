package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysFileShareUnlockDTO {

    private String password;
}
