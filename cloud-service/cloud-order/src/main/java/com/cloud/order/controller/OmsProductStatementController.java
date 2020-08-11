package com.cloud.order.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.vo.OmsProductStatementExportVo;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
import com.cloud.order.domain.entity.OmsProductStatement;
import com.cloud.order.service.IOmsProductStatementService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.Date;
import java.util.List;

/**
 * T-1交付考核报表  提供者
 *
 * @author lihongxia
 * @date 2020-08-07
 */
@RestController
@RequestMapping("productStatement")
@Api(tags = "T-1交付考核报表  提供者")
public class OmsProductStatementController extends BaseController {

    @Autowired
    private IOmsProductStatementService omsProductStatementService;

    /**
     * 查询T-1交付考核报表
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询T-1交付考核报表 ", response = OmsProductStatement.class)
    public R get(Long id) {
        return R.data(omsProductStatementService.selectByPrimaryKey(id));

    }

    /**
     * 查询T-1交付考核报表 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "T-1交付考核报表 查询分页", response = OmsProductStatement.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "deliveryDate", value = "应交付日期", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore OmsProductStatement omsProductStatement) {
        Example example = assemblyConditions(omsProductStatement);
        startPage();
        List<OmsProductStatement> omsProductStatementList = omsProductStatementService.selectByExample(example);
        return getDataTable(omsProductStatementList);
    }

    /**
     * 导出 T-1交付考核报表 列表
     */
    @HasPermissions("order:productStatement:export")
    @GetMapping("export")
    @ApiOperation(value = "导出 T-1交付考核报表", response = OmsProductStatement.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "deliveryDate", value = "应交付日期", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore OmsProductStatement omsProductStatement) {
        Example example = assemblyConditions(omsProductStatement);
        startPage();
        List<OmsProductStatement> omsProductStatementList = omsProductStatementService.selectByExample(example);
        String fileName = "T-1交付考核报表.xlsx";
        return EasyExcelUtilOSS.writeExcel(omsProductStatementList, fileName, fileName, new OmsProductStatementExportVo());
    }
    /**
     * 组装查询条件
     *
     * @return
     */
    private Example assemblyConditions(OmsProductStatement omsProductStatement) {
        Example example = new Example(OmsProductStatement.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(omsProductStatement.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsProductStatement.getProductMaterialCode());
        }
        if (StringUtils.isNotBlank(omsProductStatement.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsProductStatement.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(omsProductStatement.getDeliveryDate())) {
            criteria.andEqualTo("deliveryDate", omsProductStatement.getDeliveryDate());
        }
        example.orderBy("deliveryDate").desc();
        return example;
    }

    /**
     * 新增保存T-1交付考核报表
     */
    @PostMapping("save")
    @OperLog(title = "新增保存T-1交付考核报表 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存T-1交付考核报表 ", response = R.class)
    public R addSave(@RequestBody OmsProductStatement omsProductStatement) {
        omsProductStatementService.insertSelective(omsProductStatement);
        return R.data(omsProductStatement.getId());
    }

    /**
     * 修改保存T-1交付考核报表
     */
    @HasPermissions("order:productStatement:update")
    @PostMapping("update")
    @OperLog(title = "修改保存T-1交付考核报表 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存T-1交付考核报表 ", response = R.class)
    public R editSave(@RequestBody OmsProductStatement omsProductStatement) {
        SysUser sysUser = getUserInfo(SysUser.class);
        omsProductStatement.setUpdateBy(sysUser.getLoginName());
        omsProductStatement.setUpdateTime(new Date());
        return toAjax(omsProductStatementService.updateByPrimaryKeySelective(omsProductStatement));
    }

    /**
     * 删除T-1交付考核报表
     */
    @PostMapping("remove")
    @OperLog(title = "删除T-1交付考核报表 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T-1交付考核报表 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductStatementService.deleteByIds(ids));
    }


    /**
     * 定时汇总T-1交付考核报
     */
    @PostMapping("timeAddSave")
    @OperLog(title = "定时汇总T-1交付考核报表", businessType = BusinessType.INSERT)
    @ApiOperation(value = "定时汇总T-1交付考核报表", response = R.class)
    public R timeAddSave() {
        R r = omsProductStatementService.timeAddSave();
        return R.data(r);
    }
}
