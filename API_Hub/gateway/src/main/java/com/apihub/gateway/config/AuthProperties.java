package com.apihub.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "apihub.auth")
@Component
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
}
