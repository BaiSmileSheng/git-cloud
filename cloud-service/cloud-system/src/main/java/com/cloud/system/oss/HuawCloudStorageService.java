package com.cloud.system.oss;

import com.cloud.common.exception.file.OssException;
import com.cloud.common.utils.DateUtils;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 华为云存储 OBS
 */
public class HuawCloudStorageService {
    private  ObsClient obsClient;

    private  CloudStorageConfig cloudStorageConfig;

    public HuawCloudStorageService(CloudStorageConfig cloudStorageConfig) {
        this.cloudStorageConfig = cloudStorageConfig;
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(cloudStorageConfig.getEndPoint());
        config.setHttpsOnly(true);
        config.setPathStyle(true);
        obsClient = new ObsClient(cloudStorageConfig.getAk(), cloudStorageConfig.getSk(), config);
    }

    /**
     * 上传文件到OBS
     * @param fileM
     */
    public String upload(MultipartFile fileM) {
        String fileName = fileM.getOriginalFilename();
        String objectKey = new String();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        try {
            if(fileName.contains(".")){
                objectKey=fileName.substring(0,fileName.lastIndexOf("."))+ DateUtils.dateTimeNow()+suffix;
            }else{
                objectKey=fileName+UUID.randomUUID()+suffix;
            }
            obsClient.putObject(cloudStorageConfig.getBucketName(), objectKey, fileM.getInputStream());
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
        return objectKey;
    }
    /**
     * 下载文件
     * @param fileName
     * @param os
     */
    public void downLoad(String fileName, OutputStream os) {
        InputStream input = null;
        try {
            ObsObject obsObject = obsClient.getObject(cloudStorageConfig.getBucketName(), fileName);
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
     * @param fileName
     */
    public void deleteFile(String fileName) {
        try {
            obsClient.deleteObject(cloudStorageConfig.getBucketName(), fileName);
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
