package com.cloud.settle.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther cs
 * @date 2020/5/7 17:26
 * @description SAP配置
 */
@Component
@ConfigurationProperties(prefix = "sap.client600")
@Data
public class SAPConnConfig {


    private String ashost;//sap IP 地址

    private String sysnr;//实例 00

    private String client;//客户端 202 800等

    private String user;//用户名

    private String passwd; //密码

    private String lang;//语言


}
