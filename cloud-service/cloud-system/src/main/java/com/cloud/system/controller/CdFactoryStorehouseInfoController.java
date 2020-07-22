package com.cloud.system.controller;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdFactoryStorehouseInfoExportVo;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import com.cloud.system.util.EasyExcelUtilOSS;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 工厂库位  提供者
 *
 * @author cs
 * @date 2020-06-15
 */
@RestController
@RequestMapping("factoryStorehouse")
@Api(tags = "工厂库位  提供者")
public class CdFactoryStorehouseInfoController extends BaseController {

    @Autowired
    private ICdFactoryStorehouseInfoService cdFactoryStorehouseInfoService;

    /**
     * 查询工厂库位
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询工厂库位 ", response = CdFactoryStorehouseInfo.class)
    public CdFactoryStorehouseInfo get(Long id) {
        return cdFactoryStorehouseInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询工厂库位 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "工厂库位 查询分页", response = CdFactoryStorehouseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂编码", required =false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storehouseFrom", value = "发货库位", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storehouseTo", value = "接收库位", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        Example example = assemblyConditions(cdFactoryStorehouseInfo);
        startPage();
        List<CdFactoryStorehouseInfo> cdFactoryStorehouseInfoList = cdFactoryStorehouseInfoService.selectByExample(example);
        return getDataTable(cdFactoryStorehouseInfoList);
    }

    /**
     * 组装查询条件
     * @param cdFactoryStorehouseInfo
     * @return
     */
    private Example assemblyConditions(CdFactoryStorehouseInfo cdFactoryStorehouseInfo){
        Example example = new Example(CdFactoryStorehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdFactoryStorehouseInfo.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode",cdFactoryStorehouseInfo.getProductFactoryCode());
        }
        if(StringUtils.isNotBlank(cdFactoryStorehouseInfo.getCustomerCode())){
            criteria.andEqualTo("customerCode",cdFactoryStorehouseInfo.getCustomerCode());
        }
        if(StringUtils.isNotBlank(cdFactoryStorehouseInfo.getStorehouseFrom())){
            criteria.andEqualTo("storehouseFrom",cdFactoryStorehouseInfo.getStorehouseFrom());
        }
        if(StringUtils.isNotBlank(cdFactoryStorehouseInfo.getStorehouseTo())){
            criteria.andEqualTo("storehouseTo",cdFactoryStorehouseInfo.getStorehouseTo());
        }
        return example;
    }

    /**
     * 查询工厂库位 列表
     */
    @GetMapping("listFactoryStorehouseInfo")
    @ApiOperation(value = "查询工厂库位 列表", response = CdFactoryStorehouseInfo.class)
    public R listFactoryStorehouseInfo(String cdFactoryStorehouseInfoReq){
        CdFactoryStorehouseInfo cdFactoryStorehouseInfo = JSONObject.parseObject(cdFactoryStorehouseInfoReq,CdFactoryStorehouseInfo.class);
        Example example = assemblyConditions(cdFactoryStorehouseInfo);
        List<CdFactoryStorehouseInfo> cdFactoryStorehouseInfoList = cdFactoryStorehouseInfoService.selectByExample(example);
        return R.data(cdFactoryStorehouseInfoList);
    }
    /**
     * 导出模板
     * @return
     */
    @GetMapping("exportTemplate")
    @HasPermissions("system:factoryStorehouse:exportTemplate")
    @ApiOperation(value = "导出模板", response = CdFactoryStorehouseInfo.class)
    public R exportTemplate(){
        String fileName = "交货提前量模板.xlsx";
        return EasyExcelUtilOSS.writeExcel(Arrays.asList(),fileName,fileName,new CdFactoryStorehouseInfoExportVo());
    }

    /**
     * 导出
     * @return
     */
    @GetMapping("export")
    @HasPermissions("system:factoryStorehouse:export")
    @ApiOperation(value = "导出", response = CdFactoryStorehouseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂编码", required =false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storehouseFrom", value = "发货库位", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storehouseTo", value = "接收库位", required = false,paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdFactoryStorehouseInfo cdFactoryStorehouseInfo){
        String fileName = "交货提前量.xlsx";
        Example example = assemblyConditions(cdFactoryStorehouseInfo);
        List<CdFactoryStorehouseInfo> cdFactoryStorehouseInfoList = cdFactoryStorehouseInfoService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(cdFactoryStorehouseInfoList,fileName,fileName,new CdFactoryStorehouseInfo());
    }

    /**
     * 导入
     * @return
     */
    @PostMapping("importFactoryStorehouse")
    @HasPermissions("system:factoryStorehouse:importFactoryStorehouse")
    @ApiOperation(value = "导入", response = CdFactoryStorehouseInfo.class)
    public R importFactoryStorehouse(@RequestPart("file") MultipartFile file)throws IOException {
        SysUser sysUser = getUserInfo(SysUser.class);
        return cdFactoryStorehouseInfoService.importFactoryStorehouse(file,sysUser);
    }

    /**
     * 查询一个工厂库位
     */
    @GetMapping("findOneByExample")
    public R findOneByExample(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        Example example = new Example(CdFactoryStorehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(cdFactoryStorehouseInfo);
        CdFactoryStorehouseInfo cdFactoryStorehouse = cdFactoryStorehouseInfoService.findByExampleOne(example);
        return R.data(cdFactoryStorehouse);
    }

    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    @PostMapping("selectStorehouseToMap")
    public R selectStorehouseToMap(@RequestBody List<Dict> dicts){
        return cdFactoryStorehouseInfoService.selectStorehouseToMap(dicts);
    }

    /**
     * 新增保存工厂库位
     */
    @PostMapping("save")
    @OperLog(title = "新增保存工厂库位 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存工厂库位 ", response = R.class)
    public R addSave(@RequestBody CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        cdFactoryStorehouseInfoService.insertSelective(cdFactoryStorehouseInfo);
        return R.data(cdFactoryStorehouseInfo.getId());
    }

    /**
     * 修改保存工厂库位
     */
    @HasPermissions("system:factoryStorehouse:update")
    @PostMapping("update")
    @OperLog(title = "修改保存工厂库位 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存工厂库位 ", response = R.class)
    public R editSave(@RequestBody CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        return toAjax(cdFactoryStorehouseInfoService.updateByPrimaryKeySelective(cdFactoryStorehouseInfo));
    }

    /**
     * 删除工厂库位
     */
    @HasPermissions("system:factoryStorehouse:remove")
    @PostMapping("remove")
    @OperLog(title = "删除工厂库位 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除工厂库位 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdFactoryStorehouseInfoService.deleteByIds(ids));
    }

}
