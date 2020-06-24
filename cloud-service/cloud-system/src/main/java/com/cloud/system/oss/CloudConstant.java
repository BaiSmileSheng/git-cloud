package com.cloud.system.oss;

public class CloudConstant {
    /**
     * 云存储配置KEY
     */
    public final static String CLOUD_STORAGE_CONFIG_KEY = "sys.oss.cloudStorage";

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 华为云
         */
        HUAWEIYUN(1),
        /**
         * 阿里云
         */
        ALIYUN(2);
        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
