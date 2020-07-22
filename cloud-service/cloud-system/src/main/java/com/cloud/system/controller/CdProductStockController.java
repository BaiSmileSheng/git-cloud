package com.cloud.system.controller;

import cn.hutool.core.lang.Dict;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.SheetExcelData;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdProductInProduction;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdProductStockDetailVo;
import com.cloud.system.service.ICdProductStockService;
import com.cloud.system.util.EasyExcelUtilOSS;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 成品库存主表  提供者
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@RestController
@Api(tags = "成品库存主表")
@RequestMapping("productStock")
public class CdProductStockController extends BaseController {

    @Autowired
    private ICdProductStockService cdProductStockService;

    /**
     * 查询成品库存主表
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询成品库存主表 ", response = CdProductStock.class)
    public CdProductStock get(Long id) {
        return cdProductStockService.selectByPrimaryKey(id);

    }

    /**
     * 查询成品库存主表 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "成品库存主表 查询分页", response = CdProductStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdProductStock cdProductStock) {
        startPage();
        List<CdProductStock> cdProductStockList = listByCondition(cdProductStock);
        return getDataTable(cdProductStockList);
    }

    /**
     * 导出成品库存主表列表
     */
    @GetMapping("export")
    @HasPermissions("system:productStock:export")
    @ApiOperation(value = "导出成品库存主表列表", response = CdProductStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdProductStock cdProductStock) {

        R result = cdProductStockService.export(cdProductStock);
        if(!result.isSuccess()){
            return result;
        }
        CdProductStockDetailVo cdProductStockDetail = (CdProductStockDetailVo)result.get("data");
        List<SheetExcelData>  sheetExcelDataList= new ArrayList<>();
        SheetExcelData sheetExcelDataZ = new SheetExcelData();
        sheetExcelDataZ.setDataList(cdProductStockDetail.getCdProductStockList());
        sheetExcelDataZ.setSheetName("汇总数据");
        sheetExcelDataZ.setTClass(CdProductStock.class);
        sheetExcelDataList.add(sheetExcelDataZ);
        SheetExcelData sheetExcelDataL = new SheetExcelData();
        sheetExcelDataL.setDataList(cdProductStockDetail.getCdProductWarehouseListL());
        sheetExcelDataL.setSheetName("在库库存");
        sheetExcelDataL.setTClass(CdProductWarehouse.class);
        sheetExcelDataList.add(sheetExcelDataL);

        SheetExcelData sheetExcelDataT = new SheetExcelData();
        sheetExcelDataT.setDataList(cdProductStockDetail.getCdProductPassageList());
        sheetExcelDataT.setSheetName("在途库存");
        sheetExcelDataT.setTClass(CdProductPassage.class);
        sheetExcelDataList.add(sheetExcelDataT);

        SheetExcelData sheetExcelDataC = new SheetExcelData();
        sheetExcelDataC.setDataList(cdProductStockDetail.getCdProductInProductionList());
        sheetExcelDataC.setSheetName("在产库存");
        sheetExcelDataC.setTClass(CdProductInProduction.class);
        sheetExcelDataList.add(sheetExcelDataC);

        SheetExcelData sheetExcelDataB = new SheetExcelData();
        sheetExcelDataB.setDataList(cdProductStockDetail.getCdProductWarehouseListB());
        sheetExcelDataB.setSheetName("不良库存");
        sheetExcelDataB.setTClass(CdProductWarehouse.class);
        sheetExcelDataList.add(sheetExcelDataB);

        return EasyExcelUtilOSS.writeMultiExcel("成品库存.xlsx", sheetExcelDataList);
    }

    /**
     * 组装查询条件
     *
     * @param cdProductStock
     * @return
     */
    private List<CdProductStock> listByCondition(CdProductStock cdProductStock) {
        Example example = new Example(CdProductStock.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());

        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            List<String> productMaterialCodeList = Arrays.asList(cdProductStock.getProductMaterialCode().split(","));
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        List<CdProductStock> cdProductStockList = cdProductStockService.selectByExample(example);
        return cdProductStockList;
    }

    /**
     * 新增保存成品库存主表
     */
    @PostMapping("save")
    @OperLog(title = "新增保存成品库存主表 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存成品库存主表 ", response = R.class)
    public R addSave(@RequestBody CdProductStock cdProductStock) {
        cdProductStockService.insertSelective(cdProductStock);
        return R.data(cdProductStock.getId());
    }

    /**
     * 修改保存成品库存主表
     */
    @PostMapping("update")
    @OperLog(title = "修改保存成品库存主表 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存成品库存主表 ", response = R.class)
    public R editSave(@RequestBody CdProductStock cdProductStock) {
        return toAjax(cdProductStockService.updateByPrimaryKeySelective(cdProductStock));
    }

    /**
     * 删除成品库存主表
     */
    @PostMapping("remove")
    @OperLog(title = "删除成品库存主表 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除成品库存主表 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdProductStockService.deleteByIds(ids));
    }

    /**
     * 实时同步成品库存
     * @param cdProductStockList
     * @return
     */
    @HasPermissions("system:productStock:sycProductStock")
    @PostMapping("sycProductStock")
    @OperLog(title = "实时同步成品库存 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "实时同步成品库存 ", response = R.class)
    public R sycProductStock(@RequestBody List<CdProductStock> cdProductStockList) {
        SysUser sysUser = getUserInfo(SysUser.class);
        cdProductStockList.forEach(cdProductStock -> {
            if(StringUtils.isBlank(cdProductStock.getProductFactoryCode())
                    || StringUtils.isBlank(cdProductStock.getProductMaterialCode())){
                throw new BusinessException("入参工厂号和专用号不能为空");
            }
        });
        return cdProductStockService.sycProductStock(cdProductStockList,sysUser);
    }

    /**
     * 定时任务同步成品库存接口
     *
     * @return
     */
    @PostMapping("timeSycProductStock")
    @OperLog(title = "定时任务同步成品库存接口 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时任务同步成品库存接口 ", response = R.class)
    public R timeSycProductStock() {
        return cdProductStockService.timeSycProductStock();
    }

    /**
     * 根据Example查询一条数据
     * @param cdProductStock
     * @return
     */
    @PostMapping("findOneByExample")
    @ApiOperation(value = "根据Example查询一条数据", response = R.class)
    public R findOneByExample(@RequestBody CdProductStock cdProductStock) {
        Example example = new Example(CdProductStock.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            criteria.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
        }
        cdProductStock=cdProductStockService.findByExampleOne(example);
        return R.data(cdProductStock);
    }

    /**
     * 根据工厂，专用号分组取成品库存
     * @param dicts
     * @return
     */
    @PostMapping("selectProductStockToMap")
    public R selectProductStockToMap(@RequestBody List<Dict> dicts){
        return cdProductStockService.selectProductStockToMap(dicts);
    }


    /**
     * 根据生产工厂、成品专用号查询成品库存
     */
    @PostMapping("queryOneByFactoryAndMaterial")
    @ApiOperation(value = "根据生产工厂、成品专用号查询成品库存", response = CdProductStock.class)
    public R queryOneByFactoryAndMaterial(@RequestBody List<CdProductStock> list) {
        return cdProductStockService.selectList(list);
    }
}
