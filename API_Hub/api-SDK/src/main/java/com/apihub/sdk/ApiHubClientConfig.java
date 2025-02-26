package com.apihub.sdk;


import com.apihub.sdk.client.ApiHubIdClient;
import com.apihub.sdk.client.ApiHubSelfApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("apihub.client")
@Data
@ComponentScan
public class ApiHubClientConfig {

    private String accessKey;

    private String secretKey;

    private String apiToken;

    @Bean
    public ApiHubIdClient apiHubIdClient() {
        return new ApiHubIdClient(accessKey, secretKey);
    }

    @Bean
    public ApiHubSelfApiClient apiHubSelfApiClient() {
        return new ApiHubSelfApiClient(apiToken);
    }
}