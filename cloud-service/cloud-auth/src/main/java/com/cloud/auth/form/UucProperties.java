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

    private String apiUrl;//HCC请求域名
    private String clientId;//UUC 下发的 client_id
    private String clientSecret;//UUC 下发的 client_secret
    private Boolean isCheck; //是否UUC检测


}
