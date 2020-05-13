package com.cloud.system.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 华为云存储配置信息
 */
@Component
@ConfigurationProperties(prefix = "obs")
@Data
public class CloudStorageConfig{

    // 华为云EndPoint
    private String endPoint;

    // 华为云AccessKeyId
    private String ak;

    // 华为云AccessKeySecret
    private String sk;

    // 华为云BucketName
    private String bucketName;

}
