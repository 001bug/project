package com.apihub.user.model.dto.aouth;

import lombok.Data;

@Data
public class GiteeTokenRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String scope;
    private String username;
    private String password;
}
