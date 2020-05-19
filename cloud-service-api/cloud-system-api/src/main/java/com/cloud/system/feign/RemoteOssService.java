package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.feign.factory.RemoteOssFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OBS文件管理
 *
 * @author cs
 * @date 2020-05-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteOssFallbackFactory.class)
public interface RemoteOssService {
    /**
     * 上传文件到华为云
     *
     * @param file
     * @return
     * @author cs
     */
    @GetMapping("oss/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file);


    @GetMapping("oss/downLoad")
    public void downLoad(String url,String fileName);


    @GetMapping("oss/remove")
    public String remove(String ids);

    /**
     * 根据订单编号查询文件上传列表
     * @param orderNo
     * @return
     */
    @GetMapping("oss/listByOrderNo")
    public R listByOrderNo(String orderNo);

}
