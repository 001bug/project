package com.apihub.user.model.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserKeyPairVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accessKey;

    private String secreteKey;
}
