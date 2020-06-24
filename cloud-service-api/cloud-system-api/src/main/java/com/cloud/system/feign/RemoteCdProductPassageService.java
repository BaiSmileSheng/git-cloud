package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.feign.factory.RemoteCdProductPassageFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Description:  成品在途明细
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/16
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdProductPassageFallbackFactory.class)
public interface RemoteCdProductPassageService {
    /**
     * Description:  查询成品在途
     * Param: [cdProductPassage]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @PostMapping("productPassage/queryOneByExample")
    R queryOneByExample(@RequestBody CdProductPassage cdProductPassage);
    /**
     * Description:  查询成品在途
     * Param: [cdProductPassage]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @PostMapping("productPassage/queryByList")
    R queryByList(@RequestBody List<CdProductPassage> list);
}
