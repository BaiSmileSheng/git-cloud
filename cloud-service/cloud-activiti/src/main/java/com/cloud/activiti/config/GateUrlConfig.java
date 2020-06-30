package com.cloud.activiti.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther cs
 * @date 2020/5/7 17:26
 * @description SAP配置
 */
@Component
@ConfigurationProperties(prefix = "gate")
@Data
public class GateUrlConfig {

    private String url;//gateway地址

}
