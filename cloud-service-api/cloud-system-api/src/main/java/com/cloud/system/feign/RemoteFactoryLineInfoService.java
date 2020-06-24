package com.cloud.system.feign;

import cn.hutool.core.lang.Dict;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.feign.factory.RemoteFactoryLineInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 工厂线体关系 Feign服务层
 * @author cs
 * @date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteFactoryLineInfoFallbackFactory.class)
public interface RemoteFactoryLineInfoService {
    /**
     * 查询工厂线体关系
     * @param cdFactoryLineInfo
     * @return List<CdFactoryLineInfo>
     */
    @PostMapping("factoryLine/listByExample")
    R listByExample(@RequestBody CdFactoryLineInfo cdFactoryLineInfo);

    /**
     * 查询工厂线体关系
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    @PostMapping("factoryLine/selectLineCodeBySupplierCode")
    R selectLineCodeBySupplierCode(@RequestParam(value = "supplierCode") String supplierCode);

    /**
     * 根据线体查询信息
     * @param produceLineCode
     * @return 供应商编码
     */
    @PostMapping("factoryLine/selectInfoByCodeLineCode")
    CdFactoryLineInfo selectInfoByCodeLineCode(@RequestParam(value = "produceLineCode") String produceLineCode);
    /**
     * 定时任务获取工厂线体关系数据，并保存
     * @return R
     */
    @PostMapping("factoryLine/saveFactoryLineInfo")
    R saveFactoryLineInfo();
    /**
     * Description:  根据List<Map<String,String>>工厂、线体查询线体信息
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @PostMapping("factoryLine/selectListByMapList")
    R selectListByMapList(@RequestBody List<Dict> list);
}
