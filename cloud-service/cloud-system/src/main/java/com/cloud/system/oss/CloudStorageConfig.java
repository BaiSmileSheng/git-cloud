package com.cloud.system.oss;

import com.cloud.system.oss.valdator.AliyunGroup;
import com.cloud.system.oss.valdator.HuaweiGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 云存储配置信息
 */
@Data
public class CloudStorageConfig implements Serializable {
    //
    private static final long serialVersionUID = 9035033846176792944L;

    // 类型 1：华为 2：阿里云
    @Range(min = 1, max = 2, message = "类型错误")
    private Integer type;


    // 阿里云绑定的域名
    @NotBlank(message = "阿里云绑定的域名不能为空", groups = AliyunGroup.class)
    @URL(message = "阿里云绑定的域名格式不正确", groups = AliyunGroup.class)
    private String aliyunDomain;

    // 阿里云路径前缀
    @Pattern(regexp = "^[^(/|\\)](.*[^(/|\\)])?$", message = "阿里云路径前缀不能'/'或者'\'开头或者结尾", groups = AliyunGroup.class)
    private String aliyunPrefix;

    // 阿里云EndPoint
    @NotBlank(message = "阿里云EndPoint不能为空", groups = AliyunGroup.class)
    private String aliyunEndPoint;

    // 阿里云AccessKeyId
    @NotBlank(message = "阿里云AccessKeyId不能为空", groups = AliyunGroup.class)
    private String aliyunAccessKeyId;

    // 阿里云AccessKeySecret
    @NotBlank(message = "阿里云AccessKeySecret不能为空", groups = AliyunGroup.class)
    private String aliyunAccessKeySecret;

    // 阿里云BucketName
    @NotBlank(message = "阿里云BucketName不能为空", groups = AliyunGroup.class)
    private String aliyunBucketName;

    @NotBlank(message = "华为云EndPoint不能为空", groups = HuaweiGroup.class)
    private String hcloudendPoint;

    @NotBlank(message = "华为云AK不能为空", groups = HuaweiGroup.class)
    private String hcloudAk;

    @NotBlank(message = "华为云SK不能为空", groups = HuaweiGroup.class)
    private String hcloudSk;

    @NotBlank(message = "华为云BucketName不能为空", groups = HuaweiGroup.class)
    private String hcloudBucketName;

    private String hcloudPrefix;

}
