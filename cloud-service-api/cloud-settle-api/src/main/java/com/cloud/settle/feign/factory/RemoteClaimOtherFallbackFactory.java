package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.feign.RemoteClaimOtherService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 延期索赔 提供者
 * @Author Lihongxia
 * @Date 2020-06-01
 */
@Slf4j
@Component
public class RemoteClaimOtherFallbackFactory implements FallbackFactory<RemoteClaimOtherService> {
    @Override
    public RemoteClaimOtherService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteClaimOtherService(){

            /**
             * 查询其他索赔
             * @param id 主键
             * @return 成功或失败
             */
            @Override
            public R get(Long id) {
                log.error("RemoteClaimOtherService.get error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 查询其他索赔(包含文件信息)
             * @param id 主键
             * @return 成功或失败 其他索赔(包含文件信息)
             */
            @Override
            public R selectById(Long id) {
                log.error("RemoteClaimOtherService.selectById error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 修改保存其他索赔
             * @param smsClaimOther 其他索赔信息
             * @return 成功或失败
             */
            @Override
            public R editSave(SmsClaimOther smsClaimOther) {
                log.error("RemoteClaimOtherService.editSave error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 索赔单供应商申诉(包含文件信息)
             * @param id 主键id
             * @param complaintDescription 申诉描述
             * @param files
             * @return 索赔单供应商申诉结果成功或失败
             */
            @Override
            public R supplierAppeal(Long id, String complaintDescription, MultipartFile[] files) {

                log.error("RemoteClaimOtherService.supplierAppeal error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 48H超时未确认发送邮件
             * @return 成功或失败
             */
            @Override
            public R overTimeSendMail() {
                log.error("RemoteClaimOtherService.overTimeSendMail error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 72H超时供应商自动确认
             * @return 成功或失败
             */
            @Override
            public R overTimeConfim() {
                log.error("RemoteClaimOtherService.overTimeConfim error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
