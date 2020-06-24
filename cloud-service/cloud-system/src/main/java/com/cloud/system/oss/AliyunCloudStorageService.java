package com.cloud.system.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.cloud.common.enums.StatusEnums;
import com.cloud.common.exception.SkeletonException;
import com.cloud.common.exception.file.OssException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 阿里云存储
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSS client;

    public AliyunCloudStorageService(CloudStorageConfig config) {
        this.config = config;
        // 初始化
        init();
    }

    private void init() {
        client = new OSSClientBuilder().build(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    /**
     *
     * @param inputStream 字节流
     * @param path        文件路径，包含文件名
     * @return 返回http地址
     */
    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息");
        }
        return config.getAliyunDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getAliyunPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix));
    }

    /**
     * 功能暂无
     * @param fileName
     * @param os
     */
    @Override
    public void downLoad(String fileName, OutputStream os) {
        throw new SkeletonException(StatusEnums.METHOD_NOT_REALIZE);
    }

    /**
     * 功能暂无
     * @param fileName
     */
    @Override
    public void deleteFile(String fileName) {
        throw new SkeletonException(StatusEnums.METHOD_NOT_REALIZE);
    }
}
