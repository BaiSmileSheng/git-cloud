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
@ConfigurationProperties(prefix = "huc")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HucProperties {



    private String apiUrl;//HUC 请求域名
    private String appKey;//HUC 下发的 appKey
    private String secret;//HUC 下发的 secret
    private Boolean isCheck; //是否HUC检测


}
