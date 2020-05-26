package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteOssFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


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
     * @param file
     * @return
     */
    @PostMapping("oss/upload")
    R uploadFile(@RequestParam("file") MultipartFile file);

    /**
     * 下载文件
     * @param url   文件表url
     * @param fileName   下载文件名
     */
    @PostMapping("oss/downLoad")
    void downLoad(@RequestParam("url")String url,@RequestParam("fileName")String fileName);

    /**
     * 删除文件上传
     * @param ids  文件表id  逗号分隔
     * @return
     */
    @PostMapping("oss/remove")
    R remove(String ids);

    /**
     * 根据订单编号查询文件上传列表
     * @param orderNo 订单编号
     * @return R 包含List<SysOss> 文件上传集合
     */
    @GetMapping("oss/listByOrderNo")
    public R listByOrderNo(String orderNo);

}
