package com.cloud.system.feign;

import cn.hutool.core.lang.Dict;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteMaterialFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:物料数据
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/2
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMaterialFactory.class)
public interface RemoteMaterialService {
    /**
     * 定时任务：获取MDM系统物料主数据
     * 获取MDM系统物料信息数据并保存
     */
    @PostMapping("material/saveMaterialInit")
    R saveMaterialInfo();
    /**
     * 定时任务：获取UPH数据并更新
     * 频率：定时任务（获取MDM系统物料信息数据并保存）之后
     */
    @PostMapping("material/updateUphBySap")
    R updateUphBySap();

    /**
     * 根据物料号查询一条物料信息(多条取一条)
     * @param materialCode
     * @return
     */
    @GetMapping("material/getByMaterialCode")
    R getByMaterialCode(@RequestParam(value = "materialCode") String materialCode,@RequestParam("factoryCode") String factoryCode);

    /**
     * 根据物料号集合查询物料信息
     * @param materialCodes
     * @return
     */
    @PostMapping("material/selectInfoByInMaterialCodeAndMaterialType")
    R  selectInfoByInMaterialCodeAndMaterialType(@RequestBody List<String> materialCodes,@RequestParam(value = "materialType") String materialType);
    /**
     * Description: 根据成品专用号、生产工厂、物料类型查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @PostMapping("material/selectListByMaterialList")
    R selectListByMaterialList(@RequestBody List<Dict> list);
}
