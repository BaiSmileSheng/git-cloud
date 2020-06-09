package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.feign.factory.RemoteMaterialFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:物料数据
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/2
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMaterialFactory.class)
public interface RemoteMaterialService {
    @PostMapping("material/saveMaterialInit")
    R saveMaterialInfo();

    /**
     * 根据物料号查询物料信息
     * @param materialCode
     * @return
     */
    @GetMapping("material/getByMaterialCode")
    CdMaterialInfo getByMaterialCode(@RequestParam(value = "materialCode") String materialCode);
}
