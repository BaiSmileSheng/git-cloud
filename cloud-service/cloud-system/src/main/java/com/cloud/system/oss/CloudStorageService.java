package com.cloud.system.oss;

import com.cloud.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 云存储(支持阿里云、华为云)
 */
public abstract class CloudStorageService {
    /**
     * 云存储配置信息
     */
    CloudStorageConfig config;

    public int getService() {
        return config.getType();
    }

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 文件路径
        String path = DateUtils.dateTime() + "/" + uuid;
        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 文件上传
     *
     * @param data   文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path        文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix      后缀
     * @return 返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix);

    /**
     * 文件下载
     * @param fileName
     * @param os
     */
    public abstract void downLoad(String fileName, OutputStream os);

    /**
     * 文件删除
     * @param fileName
     */
    public abstract void deleteFile(String fileName);


}
