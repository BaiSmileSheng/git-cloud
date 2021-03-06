package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.feign.factory.RemoteDictDataFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 数据BOM Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-09
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictDataFallbackFactory.class)
public interface RemoteDictDataService {
    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @GetMapping("dict/data/label")
    String getLabel(@RequestParam("dictType")String dictType, @RequestParam("dictValue") String dictValue);

    /**
     * 根据字典类型查询字典数据信息
     * 从redis中取值，过期时间一周
     * @param dictType 字典类型
     * @return 参数键值
     */
    @GetMapping("dict/data/type")
    List<SysDictData> getType(@RequestParam("dictType") String dictType);
}
