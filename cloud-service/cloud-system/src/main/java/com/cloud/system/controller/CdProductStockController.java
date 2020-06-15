package com.cloud.system.controller;

import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.service.ICdProductStockService;
import com.cloud.common.core.page.TableDataInfo;

import java.math.BigDecimal;
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
            @ApiImplicitParam(name = "productFactoryCode", value = "专用号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdProductStock cdProductStock) {
        List<CdProductStock> cdProductStockList = listByCondition(cdProductStock);
        return getDataTable(cdProductStockList);
    }

    /**
     * 导出成品库存主表列表
     */
    @GetMapping("export")
    @ApiOperation(value = "成品库存主表 查询分页", response = CdProductStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "专用号", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdProductStock cdProductStock) {
        List<CdProductStock> cdProductStockList = listByCondition(cdProductStock);
        for (CdProductStock cdProductStockRes : cdProductStockList) {
            //在库库存
            BigDecimal stockWNum = cdProductStockRes.getStockWNum();
            //在途库存
            BigDecimal stockINum = cdProductStockRes.getStockINum();
            //寄售不足
            BigDecimal stockKNum = cdProductStockRes.getStockKNum();
            BigDecimal sumNum = stockWNum.add(stockINum).multiply(stockKNum);
            cdProductStockRes.setSumNum(sumNum);
        }
        return EasyExcelUtil.writeExcel(cdProductStockList, "成品库存.xlsx", "sheet", new CdProductStock());
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
        criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
        criteria.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
        startPage();
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
     * 同步成品库存
     *
     * @param factoryCode  工厂编号
     * @param materialCode 物料编号
     * @return
     */
    @PostMapping("sycProductStock")
    @OperLog(title = "同步成品库存 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "同步成品库存 ", response = R.class)
    public R sycProductStock(@RequestParam("factoryCode") String factoryCode, @RequestParam("materialCode") String materialCode) {

        return cdProductStockService.sycProductStock(factoryCode, materialCode);
    }

    /**
     * 定时任务同步原材料库存接口
     *
     * @return
     */
    @PostMapping("timeSycProductStock")
    @OperLog(title = "定时任务同步原材料库存接口 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时任务同步原材料库存接口 ", response = R.class)
    public R timeSycProductStock() {

        return cdProductStockService.timeSycProductStock();
    }
}
