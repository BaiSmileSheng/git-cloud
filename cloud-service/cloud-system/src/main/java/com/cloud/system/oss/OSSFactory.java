package com.cloud.system.oss;

import com.alibaba.fastjson.JSON;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.system.service.ISysConfigService;

/**
 * 文件上传Factory
 */
public final class OSSFactory {
    private static ISysConfigService sysConfigService;

    static {
        OSSFactory.sysConfigService =  ApplicationContextUtil.getBean(ISysConfigService.class);
    }

    /**
     * 默认查询数据库中sys_config：config_key为sys.oss.cloudStorage的数据
     * 根据config_value中type值确认云服务：1、华为2、阿里
     * @return
     */
    public static CloudStorageService build() {
        String jsonconfig = sysConfigService.selectConfigByKey(CloudConstant.CLOUD_STORAGE_CONFIG_KEY);
        // 获取云存储配置信息
        CloudStorageConfig config = JSON.parseObject(jsonconfig, CloudStorageConfig.class);
        if (config.getType() == CloudConstant.CloudService.HUAWEIYUN.getValue()) {
            return new HuawCloudStorageService(config);
        } else if (config.getType() == CloudConstant.CloudService.ALIYUN.getValue()) {
            return new AliyunCloudStorageService(config);
        }
        return null;
    }
}
