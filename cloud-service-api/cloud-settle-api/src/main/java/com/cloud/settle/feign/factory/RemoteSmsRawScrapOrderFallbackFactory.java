package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.feign.RemoteSmsRawScrapOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSmsRawScrapOrderFallbackFactory implements FallbackFactory<RemoteSmsRawScrapOrderService> {


    @Override
    public RemoteSmsRawScrapOrderService create(Throwable throwable) {
        return new RemoteSmsRawScrapOrderService() {
            /**
             * 根据创建时间查询原材料报废申请
             * @param createTimeStart
             * @param endTimeStart
             * @return
             */
            @Override
            public R listByTime(String createTimeStart, String endTimeStart) {
                log.error("RemoteSmsRawScrapOrderService.listByTime错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 定时任务更新价格
             * @param
             * @return
             */
            @Override
            public R updateRawScrapJob() {
                log.error("RemoteSmsRawScrapOrderService.updateRawScrapJob错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 查询原材料报废申请
             */
            @Override
            public R get(Long id) {
                log.error("RemoteSmsRawScrapOrderService.get错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 审核通过传SAP系统261进行报废
             * @param
             * @return
             */
            @Override
            public R autidSuccessToSAP261(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
                log.error("RemoteSmsRawScrapOrderService.autidSuccessToSAP261错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 更新原材料报废
             * @param
             * @return
             */
            @Override
            public R updateAct(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
                log.error("RemoteSmsRawScrapOrderService.updateAct错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
        };
    }
}
