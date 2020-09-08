package com.cloud.auth.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther cs
 * @date 2020/9/8 17:26
 * @description
 */
@Component
@ConfigurationProperties(prefix = "login")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginProperties {

    private Boolean isLogin;//是否允许登录
    private String errMsg;//错误信息

}
