package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.feign.factory.RemoteOssFallbackFactory;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import feign.codec.Encoder;
import java.util.List;


/**
 * OBS文件管理
 *
 * @author cs
 * @date 2020-05-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE,configuration = RemoteOssService.MultipartSupportConfig.class,fallbackFactory = RemoteOssFallbackFactory.class)
public interface RemoteOssService {
    /**
     * 上传文件到华为云
     * @param file
     * @return
     */
    @RequestMapping(value = "oss/upload", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestParam(value = "orderNo",required = false) String orderNo);

    /**
     * 上传文件到华为云
     * @param file
     * @return
     */
    @RequestMapping(value = "oss/onlyForUpload", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R onlyForUpload(@RequestPart(value = "file") MultipartFile file);


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
    R remove(@RequestParam("ids") String ids);

    /**
     * 根据订单编号查询文件上传列表
     * @param orderNo 订单编号
     * @return R 包含List<SysOss> 文件上传集合
     */
    @GetMapping("oss/listByOrderNo")
    public R listByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 根据订单编号修改文件上传列表
     * @param orderNo 订单编号
     * @param files 文件数组
     * @return 成功或失败
     */
    @RequestMapping(value = "oss/updateListByOrderNo", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R updateListByOrderNo(@RequestParam("orderNo") String orderNo,@RequestPart("files") MultipartFile[] files);

    /**
     * 根据订单编号删除文件上传列表
     * @param orderNo 订单编号
     * @return 成功或失败
     */
    @PostMapping("oss/deleteListByOrderNo")
    public R deleteListByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 按订单号批量修改
     * @param sysOssList
     * @return
     */
    @PostMapping("oss/batchEditSaveById")
    public R batchEditSaveById(@RequestBody List<SysOss> sysOssList);

    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }

}
