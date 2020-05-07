package com.cloud.auth.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther cs
 * @date 2020/5/7 17:26
 * @description
 */
@Component
@ConfigurationProperties(prefix = "uuc")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UucProperties {

    private String apiUrl;
    private String clientId;
    private String clientSecret;


}
