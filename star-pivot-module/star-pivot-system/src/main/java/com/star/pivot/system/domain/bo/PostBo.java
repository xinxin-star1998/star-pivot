package com.star.pivot.system.domain.bo;

import lombok.Data;

@Data
public class PostBo {
    /**
     * 岗位ID
     */
    private Long postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;
}
