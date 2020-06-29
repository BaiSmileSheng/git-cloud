package com.cloud.system.feign;

import cn.hutool.core.lang.Dict;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteMaterialExtendInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * 物料扩展信息  Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-16
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMaterialExtendInfoFallbackFactory.class)
public interface RemoteMaterialExtendInfoService {

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    @PostMapping("materialExtendInfo/timeSycMaterialCode")
    R timeSycMaterialCode();

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
    @GetMapping("materialExtendInfo/selectMaterialCodeByLifeCycle")
    R selectMaterialCodeByLifeCycle(@RequestParam("lifeCycle") String lifeCycle);

    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @PostMapping("materialExtendInfo/selectInfoInMaterialCodes")
    R selectInfoInMaterialCodes(@RequestBody List<String> materialCodes);

    /**
     * Description:  根据多个成品专用号查询扩展信息
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @PostMapping("materialExtendInfo/selectByMaterialList")
    R selectByMaterialList(@RequestBody List<Dict> list);
    /**
     * Description:  根据物料号查询一条记录
     * Param: [materialCode]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/23
     */

    @PostMapping("materialExtendInfo/selectOneByMaterialCode")
    R selectOneByMaterialCode(@RequestParam("materialCode") String materialCode);
}
