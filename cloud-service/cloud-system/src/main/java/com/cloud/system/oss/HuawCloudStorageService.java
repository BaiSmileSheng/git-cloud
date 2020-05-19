package com.cloud.system.oss;

import com.cloud.common.exception.file.OssException;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 华为云存储 OBS
 */
public class HuawCloudStorageService extends  CloudStorageService{
    private  ObsClient obsClient;


    /**
     * 初始化OBS配置文件
     * @param cloudStorageConfig
     */
    public HuawCloudStorageService(CloudStorageConfig cloudStorageConfig) {
        this.config = cloudStorageConfig;
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(cloudStorageConfig.getHcloudendPoint());
        config.setHttpsOnly(true);
        config.setPathStyle(true);
        obsClient = new ObsClient(cloudStorageConfig.getHcloudAk(), cloudStorageConfig.getHcloudSk(), config);
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    /**
     * 华为云 上传文件
     * @param inputStream 字节流
     * @param path        文件路径，包含文件名
     * @return 返回值为objectKey  即可用于从华为云获取文件
     */
    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            obsClient.putObject(config.getHcloudBucketName(), path, inputStream);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息");
        }finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }
        return path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getHcloudPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getHcloudPrefix(), suffix));
    }


    /**
     * 下载文件
     * @param objectKey  上传时返回值   即文件表里存的url
     * @param os
     */
    public void downLoad(String objectKey, OutputStream os) {
        InputStream input = null;
        try {
            ObsObject obsObject = obsClient.getObject(config.getHcloudBucketName(), objectKey);
            input = obsObject.getObjectContent();
            byte[] b = new byte[1024];
            int len;
            while ((len = input.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (Exception e) {
            throw new OssException("下载文件失败，请检查配置信息");
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     * @param objectKey  上传时返回值   即文件表里存的url
     */
    public void deleteFile(String objectKey) {
        try {
            obsClient.deleteObject(config.getHcloudBucketName(), objectKey);
        } catch (ObsException e) {
            throw new ObsException("上传文件错误");
        }  finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }

    }


}
