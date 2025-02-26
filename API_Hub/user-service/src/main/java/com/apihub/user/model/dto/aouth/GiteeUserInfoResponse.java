package com.apihub.user.model.dto.aouth;

import lombok.Data;

@Data
public class GiteeUserInfoResponse {

    private Long id;
    private String name;
    private String avatar_url;

}
