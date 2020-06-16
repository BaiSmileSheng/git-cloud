package com.cloud.system.controller;

import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.service.ICdProductPassageService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 成品库存在途明细  提供者
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@RestController
@RequestMapping("productPassage")
public class CdProductPassageController extends BaseController {

    @Autowired
    private ICdProductPassageService cdProductPassageService;

    /**
     * 查询成品库存在途明细
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询成品库存在途明细 ", response = CdProductPassage.class)
    public CdProductPassage get(Long id) {
        return cdProductPassageService.selectByPrimaryKey(id);

    }

    /**
     * 查询成品库存在途明细 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "成品库存在途明细 查询分页", response = CdProductPassage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdProductPassage cdProductPassage) {
        Example example = new Example(CdProductPassage.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdProductPassage> cdProductPassageList = cdProductPassageService.selectByExample(example);
        return getDataTable(cdProductPassageList);
    }

    /**
     * 查询成品库存在途明细 列表
     */
    @GetMapping("showListDetails")
    @ApiOperation(value = "查询成品库存在途明细 列表", response = CdProductPassage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "物料号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stockType", value = "0:良品;1:不良品", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = true, paramType = "query", dataType = "String")
    })
    public List<CdProductPassage> showListDetails(@ApiIgnore CdProductPassage cdProductPassage){
        Example example = new Example(CdProductPassage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",cdProductPassage.getProductMaterialCode());
        criteria.andEqualTo("productFactoryCode",cdProductPassage.getProductFactoryCode());
        List<CdProductPassage> cdProductPassageList = cdProductPassageService.selectByExample(example);
        return cdProductPassageList;
    }
    /**
     * 新增保存成品库存在途明细
     */
    @PostMapping("save")
    @OperLog(title = "新增保存成品库存在途明细 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存成品库存在途明细 ", response = R.class)
    public R addSave(@RequestBody CdProductPassage cdProductPassage) {
        cdProductPassageService.insertSelective(cdProductPassage);
        return R.data(cdProductPassage.getId());
    }

    /**
     * 修改保存成品库存在途明细
     */
    @PostMapping("update")
    @OperLog(title = "修改保存成品库存在途明细 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存成品库存在途明细 ", response = R.class)
    public R editSave(@RequestBody CdProductPassage cdProductPassage) {
        return toAjax(cdProductPassageService.updateByPrimaryKeySelective(cdProductPassage));
    }

    /**
     * 删除成品库存在途明细
     */
    @PostMapping("remove")
    @OperLog(title = "删除成品库存在途明细 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除成品库存在途明细 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdProductPassageService.deleteByIds(ids));
    }

}
