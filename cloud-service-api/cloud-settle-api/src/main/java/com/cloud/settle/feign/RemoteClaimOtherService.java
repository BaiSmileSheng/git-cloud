package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.feign.factory.RemoteClaimOtherFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 延期索赔 提供者
 * @Author Lihongxia
 * @Date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE,fallbackFactory = RemoteClaimOtherFallbackFactory.class)
public interface RemoteClaimOtherService {

    /**
     * 查询其他索赔
     * @param id 主键
     * @return 成功或失败
     */
    @GetMapping("claimOther/get")
    SmsClaimOther get(@RequestParam("id") Long id);

    /**
     * 查询其他索赔(包含文件信息)
     * @param id 主键
     * @return 成功或失败 其他索赔(包含文件信息)
     */
    @GetMapping("claimOther/selectById")
    R selectById(@RequestParam("id") Long id);

    /**
     * 修改保存其他索赔
     * @param smsClaimOther 其他索赔信息
     * @return 成功或失败
     */
    @PostMapping("claimOther/update")
    R editSave(@RequestBody SmsClaimOther smsClaimOther);

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param files
     * @return 索赔单供应商申诉结果成功或失败
     */
    @RequestMapping(value = "claimOther/supplierAppeal", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R supplierAppeal(@RequestParam("id") Long id,@RequestParam("complaintDescription")String complaintDescription,@RequestPart("files") MultipartFile[] files);

    /**
     * 48H超时未确认发送邮件
     * @return 成功或失败
     */
    @PostMapping("claimOther/overTimeSendMail")
    R overTimeSendMail();

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @PostMapping("claimOther/overTimeConfim")
    R overTimeConfim();

}
