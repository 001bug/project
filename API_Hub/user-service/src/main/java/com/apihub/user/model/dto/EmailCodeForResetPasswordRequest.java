package com.apihub.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailCodeForResetPasswordRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String email;
	private Long userId;
}
