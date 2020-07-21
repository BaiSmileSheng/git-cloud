package com.cloud.system.controller;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.easyexcel.SheetExcelData;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.service.ICdRawMaterialStockService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * 原材料库存  提供者
 *
 * @author ltq
 * @date 2020-06-05
 */
@RestController
@RequestMapping("rawMaterialStock")
@Api(tags = "原材料库存")
public class CdRawMaterialStockController extends BaseController {

    @Autowired
    private ICdRawMaterialStockService cdRawMaterialStockService;

    private final static double MAX_SIZE_EXPORT = 50000;//单个sheet导出最大值;

    /**
     * 查询原材料库存 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询原材料库存 ", response = CdRawMaterialStock.class)
    public CdRawMaterialStock get(Long id) {
        return cdRawMaterialStockService.selectByPrimaryKey(id);

    }

    /**
     * 查询原材料库存 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "原材料库存 查询分页", response = CdRawMaterialStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storagePoint", value = "仓储点", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdRawMaterialStock cdRawMaterialStock) {
        Example example = new Example(CdRawMaterialStock.class);
        Example.Criteria criteria = example.createCriteria();
        listByCondition(cdRawMaterialStock,criteria);
        startPage();
        List<CdRawMaterialStock> cdRawMaterialStockList = cdRawMaterialStockService.selectByExample(example);
        return getDataTable(cdRawMaterialStockList);
    }

    /**
     * 组装条件
     * @param cdRawMaterialStock
     * @param criteria
     */
    private void listByCondition(CdRawMaterialStock cdRawMaterialStock,Example.Criteria criteria){
        if (StringUtils.isNotBlank(cdRawMaterialStock.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",cdRawMaterialStock.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(cdRawMaterialStock.getRawMaterialCode())) {
            criteria.andEqualTo("rawMaterialCode",cdRawMaterialStock.getRawMaterialCode());
        }
    }

    /**
     * 新增保存原材料库存 
     */
    @PostMapping("save")
    @OperLog(title = "新增保存原材料库存 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存原材料库存 ", response = R.class)
    public R addSave(@RequestBody CdRawMaterialStock cdRawMaterialStock) {
        cdRawMaterialStockService.insertSelective(cdRawMaterialStock);
        return R.data(cdRawMaterialStock.getId());
    }

    /**
     * 修改保存原材料库存 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存原材料库存 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存原材料库存 ", response = R.class)
    public R editSave(@RequestBody CdRawMaterialStock cdRawMaterialStock) {
        return toAjax(cdRawMaterialStockService.updateByPrimaryKeySelective(cdRawMaterialStock));
    }

    /**
     * 删除原材料库存 
     */
    @PostMapping("remove")
    @OperLog(title = "删除原材料库存 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除原材料库存 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdRawMaterialStockService.deleteByIds(ids));
    }

    /**
     * @Description: 导出原材料库存报表
     * @Param: [cdRawMaterialStock]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/9
     */
    @HasPermissions("system:rawMaterialStock:exportRawMaterial")
    @OperLog(title = "导出原材料库存报表", businessType = BusinessType.EXPORT)
    @GetMapping("/exportRawMaterial")
    @ApiOperation(value = "导出原材料库存报表", response = CdRawMaterialStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "storagePoint", value = "仓储点", required = false,paramType = "query", dataType = "String")
    })
    public R exportRawMaterialExcel(@ApiIgnore CdRawMaterialStock cdRawMaterialStock){
        Example example = new Example(CdRawMaterialStock.class);
        Example.Criteria criteria = example.createCriteria();
        listByCondition(cdRawMaterialStock,criteria);
        //导出时不导出可用库存为0的
        criteria.andNotEqualTo("currentStock",0);
        double count = cdRawMaterialStockService.selectCountByExample(example);
        double size = 0;
        size = Math.ceil(count/MAX_SIZE_EXPORT);
        List<SheetExcelData> sheetExcelDataList = new ArrayList<>();
        for(int i=0; i < size; i++){
            Example exampleSize = new Example(CdRawMaterialStock.class);
            Example.Criteria criteriaSize = exampleSize.createCriteria();
            listByCondition(cdRawMaterialStock,criteriaSize);
            int pageNum = (int)i + 1;
            PageHelper.startPage(pageNum, (int)MAX_SIZE_EXPORT);
            List<CdRawMaterialStock> cdRawMaterialStockList = cdRawMaterialStockService.selectByExample(exampleSize);
            SheetExcelData sheetExcelData = new SheetExcelData();
            sheetExcelData.setDataList(cdRawMaterialStockList);
            sheetExcelData.setTClass(CdRawMaterialStock.class);
            sheetExcelData.setSheetName("原材料库存报表sheet"+pageNum);
            sheetExcelDataList.add(sheetExcelData);
        }
        return EasyExcelUtilOSS.writeMultiExcel("原材料库存报表.xlsx",sheetExcelDataList);
    }
    /**
     * 查询原材料库存
     */
    @PostMapping("selectByList")
    @ApiOperation(value = "根据对象查询原材料库存 ", response = CdRawMaterialStock.class)
    public R selectByList(@RequestBody List<CdRawMaterialStock> list){
        return cdRawMaterialStockService.selectByList(list);
    }

    /**
     * 实时获取原材料库存信息
     * @param list
     * @return
     */
    @HasPermissions("system:rawMaterialStock:currentqueryRawMaterialStock")
    @PostMapping("currentqueryRawMaterialStock")
    @OperLog(title = "实时获取原材料库存信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "实时获取原材料库存信息 ", response = R.class)
    public R currentqueryRawMaterialStock(@RequestBody List<CdRawMaterialStock> list){
        SysUser sysUser = getUserInfo(SysUser.class);
        list.forEach(cdRawMaterialStock -> {
            if(StringUtils.isBlank(cdRawMaterialStock.getProductFactoryCode())
                    || StringUtils.isBlank(cdRawMaterialStock.getRawMaterialCode())){
                throw new BusinessException("入参工厂号和物料号不能为空");
            }
        });
        return cdRawMaterialStockService.currentqueryRawMaterialStock(list,sysUser);
    }
}
