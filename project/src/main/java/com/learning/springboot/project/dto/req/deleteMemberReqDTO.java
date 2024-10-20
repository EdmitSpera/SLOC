package com.learning.springboot.project.dto.req;

import lombok.Data;

@Data
public class deleteMemberReqDTO {
    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 用户id
     */
    private Long userId;
}
