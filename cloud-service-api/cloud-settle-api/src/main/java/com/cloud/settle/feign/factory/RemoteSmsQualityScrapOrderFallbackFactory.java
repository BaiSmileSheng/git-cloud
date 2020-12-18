package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.feign.RemoteSmsQualityScrapOrderService;
import com.cloud.settle.feign.RemoteSmsRawScrapOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Component
public class RemoteSmsQualityScrapOrderFallbackFactory implements FallbackFactory<RemoteSmsQualityScrapOrderService> {


    @Override
    public RemoteSmsQualityScrapOrderService create(Throwable throwable) {
        return new RemoteSmsQualityScrapOrderService() {
            /**
             * 查询原材料报废申请
             */
            @Override
            public R get(Long id) {
                log.error("RemoteSmsRawScrapOrderService.get错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 更新原材料报废
             * @param
             * @return
             */
            @Override
            public R updateAct(SmsQualityScrapOrder smsQualityScrapOrder, Integer result,String comment,String auditor) {
                log.error("RemoteSmsRawScrapOrderService.updateAct错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 定时更新质量部报废订单价格
             */
            @Override
            public R updatePriceJob() {
                log.error("RemoteSmsRawScrapOrderService.updatePriceJob错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

        };
    }
}
