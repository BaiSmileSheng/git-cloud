package com.cloud.system.controller;

import cn.hutool.core.collection.CollUtil;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.mail.MailService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    private SystemFromSap601InterfaceService systemFromSap601InterfaceService;
    @Autowired
    private MailService mailService;
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
        return systemFromSap601InterfaceService.queryUphFromSap601(factorys,materials);
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
    public R queryBomInfo(@RequestParam String factorys, @RequestParam("materials") String materials) {
        List<String> factoryList = Arrays.asList(factorys.split(","));
        List<String> materialList = Arrays.asList(materials.split(","));
        return systemFromSap601InterfaceService.queryBomInfoFromSap601(factoryList,materialList, SapConstants.ABAP_AS_SAP601_SINGLE);
    }

    /**
     * 定时获取BOM清单数据
     * @return
     */
    @PostMapping("sycBomInfo")
    @ApiOperation(value = "定时获取BOM清单数据 ", response = R.class)
    public R sycBomInfo(){
        R r = new R();
        StringBuffer msg = new StringBuffer();
        try {
            r = systemFromSap601InterfaceService.sycBomInfo();
            msg.append(r.getStr("msg"));
        } catch (Exception e) {
            msg.append("bom获取失败："+e.getMessage());
            throw e;
        }
        return r;
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
        return systemFromSap601InterfaceService.queryFactoryLineFromSap601();
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
    public R queryRawMaterialStock(@RequestParam(value = "factorys",required = false) String factorys,
                                   @RequestParam(value = "materials",required = false) String materials,
                                   @RequestParam(value = "startNum",required = false) Integer startNum,
                                   @RequestParam(value = "endNum",required = false) Integer endNum) {
        List<String> factoryList = Arrays.asList(factorys.split(","));
        List<String> materialList = Arrays.asList(materials.split(","));
        return systemFromSap601InterfaceService.queryRawMaterialStockFromSap601(factoryList,materialList,startNum,endNum);
    }

    /**
     * 定时同步原材料库存
     * @return
     */
    @PostMapping("sycRawMaterialStock")
    @ApiOperation(value = "定时同步原材料库存 ", response = R.class)
    public R sycRawMaterialStock(){
        return systemFromSap601InterfaceService.sycRawMaterialStock();
    }

}
