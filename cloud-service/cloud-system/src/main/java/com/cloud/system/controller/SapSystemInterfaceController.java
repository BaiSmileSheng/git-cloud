package com.cloud.system.controller;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.service.SystemFromSap600InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: system - sap接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/5
 */
@RestController
@RequestMapping("sapSystem")
@Api(tags = "system模块对SAP系统接口")
public class SapSystemInterfaceController {
    @Autowired
    private SystemFromSap600InterfaceService systemFromSap600InterfaceService;
    /**
     * @Description: 获取uph数据
     * @Param:  factorys,materials
     * @return:
     * @Author: ltq
     * @Date: 2020/6/2
     */
    @GetMapping("queryUph")
    @ApiOperation(value = "获取UPH数据 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factorys",value = "生产工厂编码",required = true),
            @ApiImplicitParam(name = "materials",value = "成品物料编码", required = true)})
    public R queryUph(@RequestBody List<String> factorys, List<String> materials) {
        return systemFromSap600InterfaceService.queryUphFromSap600(factorys,materials);
    }
    /**
     * @Description: 获取BOM清单数据
     * @Param: [factorys, materials]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @GetMapping("queryBomInfo")
    @ApiOperation(value = "获取BOM清单数据 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factorys",value = "生产工厂编码",required = true),
            @ApiImplicitParam(name = "materials",value = "成品物料编码", required = true)})
    public R queryBomInfo(@RequestBody List<String> factorys, List<String> materials) {
        return systemFromSap600InterfaceService.queryBomInfoFromSap600(factorys,materials);
    }

    /**
     * @Description: 获取SAP系统工厂线体关系数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/2
     */
    @GetMapping("queryFactoryLine")
    @ApiOperation(value = "获取SAP系统工厂线体关系数据 ", response = R.class)
    public R queryFactoryLine() {
        return systemFromSap600InterfaceService.queryFactoryLineFromSap600();
    }
    /**
     * @Description: 获取原材料库存
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @GetMapping("queryRawMaterialStock")
    @ApiOperation(value = "获取原材料库存 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factorys",value = "生产工厂编码",required = true),
            @ApiImplicitParam(name = "materials",value = "成品物料编码", required = true)})
    public R queryRawMaterialStock(@RequestBody List<String> factorys, List<String> materials) {
        return systemFromSap600InterfaceService.queryRawMaterialStockFromSap600(factorys,materials);
    }

}
