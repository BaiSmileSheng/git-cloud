package com.cloud.common.obs;

import com.cloud.common.exception.BusinessException;
import com.cloud.common.exception.file.FileNameLengthLimitExceededException;
import com.cloud.common.exception.file.FileSizeLimitExceededException;
import com.cloud.common.utils.file.FileUploadUtils;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

/**
 * @auther cs
 * @date 2020/5/11 15:54
 * @description
 */
@Slf4j
public class FileObsUntils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    private static int counter = 0;

    private static final String endPoint = "https://oss.cosmoplat.com";

    private static final String ak = "D9850F77F89C0FC2D1F4";

    private static final String sk = "O1R/ZGoGS3dxCrHhTeROsRta/fQAAAFx+JwPwhwb";

    private static ObsClient obsClient;

    private static ObsConfiguration config;

    private static String bucketName = "my-obs-bucket-demo123";

    static {
        config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setHttpsOnly(true);
        config.setPathStyle(true);
    }

    /**
     * 上传文件到OBS
     * @param fileM
     */
    public static void upLoadFile(MultipartFile fileM) {
        try {
            //验证文件大小不能超过50M
            long size = fileM.getSize();
            if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
                throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
            }
            //验证文件名长度
            int fileNamelength = fileM.getOriginalFilename().length();
            if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
                throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
            }
            obsClient = new ObsClient(ak, sk, config);
            String fileName = fileM.getOriginalFilename();
            obsClient.putObject(bucketName, fileName, fileM.getInputStream());
        } catch (ObsException e) {
            log.error("上传文件错误", e);
            throw new ObsException("上传文件错误");
        } catch (IOException e) {
            log.error("上传文件错误", e);
            throw new BusinessException("上传文件错误");
        } finally {
            if (obsClient != null) {
                try {
                    /*
                     * Close obs client
                     */
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * 下载文件
     * @param fileName
     * @param os
     */
    public static void downLoadFile(String fileName, OutputStream os) {
        InputStream input = null;
        try {
            obsClient = new ObsClient(ak, sk, config);
            ObsObject obsObject = obsClient.getObject(bucketName, fileName);
            input = obsObject.getObjectContent();
            byte[] b = new byte[1024];
            int len;
            while ((len = input.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (IOException e) {
            log.error("下载文件错误", e);
            throw new BusinessException("上传文件错误");
        }  finally {
            if (obsClient != null) {
                try {
                    /*
                     * Close obs client
                     */
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
    public static void deleteFile(String fileName) {
        try {
            obsClient = new ObsClient(ak, sk, config);
            obsClient.deleteObject(bucketName, fileName);
        } catch (ObsException e) {
            log.error("删除文件错误", e);
            throw new ObsException("上传文件错误");
        }  finally {
            if (obsClient != null) {
                try {
                    /*
                     * Close obs client
                     */
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
