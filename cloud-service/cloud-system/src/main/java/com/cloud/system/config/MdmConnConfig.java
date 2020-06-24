package com.cloud.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @Description: mdm 获取物料主数据配置信息
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/1
 */
@Component
@ConfigurationProperties(prefix = "mdm.material")
@Data
public class MdmConnConfig {
    private String serviceUrl;
    private String sysName;
    private String masterType;
    private String tableName;
}
