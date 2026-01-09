package com.star.pivot.system.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class MenuParentVo {
    private Long parentId;
    private long menuId;
    private String menuName;
    private Long value;
    private String label;
    private List<MenuParentVo> children;
}
