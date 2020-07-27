package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrderAnalysis;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo;
import com.cloud.order.domain.entity.vo.OmsRealOrderVo;
import com.cloud.order.service.IOmsProductionOrderAnalysisService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 待排产订单分析  提供者
 *
 * @author ltq
 * @date 2020-06-15
 */
@RestController
@RequestMapping("analysis")
@Api(tags = "待排产订单分析")
public class OmsProductionOrderAnalysisController extends BaseController {

    @Autowired
    private IOmsProductionOrderAnalysisService omsProductionOrderAnalysisService;
    @Autowired
    private IOmsRealOrderService omsRealOrderService;

    /**
     * 查询待排产订单分析
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询待排产订单分析 ", response = OmsProductionOrderAnalysis.class)
    public OmsProductionOrderAnalysis get(Long id) {
        return omsProductionOrderAnalysisService.selectByPrimaryKey(id);

    }

    /**
     * 查询待排产订单分析 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "待排产订单分析-查询分页", response = OmsProductionOrderAnalysis.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore OmsProductionOrderAnalysis omsProductionOrderAnalysis) {
        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //排产员查询工厂下的数据
            if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
                List<String> facoryList = Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(","));
                String factorys = facoryList.stream().map(f -> "\'" + f +"\'").collect(Collectors.joining(","));
                omsProductionOrderAnalysis.setProductFactoryCodeList(factorys);
            }
        }
        startPage();
        List<OmsProductionOrderAnalysisVo> omsProductionOrderAnalysisList = omsProductionOrderAnalysisService.selectListPage(omsProductionOrderAnalysis);
        return getDataTable(omsProductionOrderAnalysisList);
    }


    /**
     * 新增保存待排产订单分析
     */
    @PostMapping("save")
    @OperLog(title = "新增保存待排产订单分析 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存待排产订单分析 ", response = R.class)
    public R addSave(@RequestBody OmsProductionOrderAnalysis omsProductionOrderAnalysis) {
        omsProductionOrderAnalysisService.insertSelective(omsProductionOrderAnalysis);
        return R.data(omsProductionOrderAnalysis.getId());
    }

    /**
     * 修改保存待排产订单分析
     */
    @PostMapping("update")
    @OperLog(title = "修改保存待排产订单分析 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存待排产订单分析 ", response = R.class)
    public R editSave(@RequestBody OmsProductionOrderAnalysis omsProductionOrderAnalysis) {
        return toAjax(omsProductionOrderAnalysisService.updateByPrimaryKeySelective(omsProductionOrderAnalysis));
    }

    /**
     * 删除待排产订单分析
     */
    @PostMapping("remove")
    @OperLog(title = "删除待排产订单分析 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除待排产订单分析 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductionOrderAnalysisService.deleteByIds(ids));
    }
    /**
     * 待排产订单分析-数据导出
     */
    @GetMapping("export")
    @ApiOperation(value = "待排产订单分析-数据导出", response = OmsProductionOrderAnalysis.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false,paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrderAnalysis:export")
    public R export(@ApiIgnore OmsProductionOrderAnalysis omsProductionOrderAnalysis){
        Example example = new Example(OmsProductionOrderAnalysis.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(omsProductionOrderAnalysis.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode","%"+omsProductionOrderAnalysis.getProductMaterialCode()+"%");
        }
        if (StringUtils.isNotBlank(omsProductionOrderAnalysis.getProductFactoryCode())) {
            criteria.andLike("productFactoryCode","%"+omsProductionOrderAnalysis.getProductFactoryCode()+"%");
        }
        if (StringUtils.isNotBlank(omsProductionOrderAnalysis.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",omsProductionOrderAnalysis.getOrderFrom());
        }
        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //排产员查询工厂下的数据
            if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        }
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.add(Calendar.DATE,14);
        criteria.andGreaterThan("productDate",sft.format(new Date()));
        criteria.andLessThanOrEqualTo("productDate",sft.format(date.getTime()));
        List<OmsProductionOrderAnalysis> omsProductionOrderAnalysisList = omsProductionOrderAnalysisService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(omsProductionOrderAnalysisList, "待排产订单分析数据.xlsx", "sheet", new OmsProductionOrderAnalysis());
    }
    /**
     * Description:  查询客户缺口量明细
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @GetMapping("customerList")
    @ApiOperation(value = "待排产订单分析-查询客户缺口量明细", response = OmsRealOrderVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品物料号", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productDate", value = "生产日期", required = true,paramType = "query", dataType = "String")
    })
    public R getCustomerList(@ApiIgnore OmsRealOrder omsRealOrder){
        return omsProductionOrderAnalysisService.queryRealOrder(omsRealOrder);
    }
    /**
     * Description:  待排产订单分析汇总定时任务
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/17
     */
    @PostMapping("productionOrderAnalysisGatherJob")
    @ApiOperation(value = "待排产订单分析汇总定时任务", response = OmsRealOrder.class)
    public R productionOrderAnalysisGatherJob(){
        return omsProductionOrderAnalysisService.saveAnalysisGather();
    }

    /**
     * Description:  查询成品库存信息
     * Param: [cdProductStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/30
     */
    @GetMapping("getProductStock")
    @ApiOperation(value = "查询成品库存信息", response = CdProductStock.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品物料号", required = true,paramType = "query", dataType = "String")
    })
    public R getProductStock(@ApiIgnore CdProductStock cdProductStock){
        return omsProductionOrderAnalysisService.getProductStock(cdProductStock);
    }

    /**
     * Description:  查询需求量明细
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/7/27
     */
    @GetMapping("getDemandList")
    @ApiOperation(value = "待排产订单分析-查询客户缺口量明细", response = OmsRealOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品物料号", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productDate", value = "生产日期", required = true,paramType = "query", dataType = "String")
    })
    public R getDemandList(@ApiIgnore OmsRealOrder omsRealOrder){
        return omsProductionOrderAnalysisService.getDemandList(omsRealOrder);
    }
}
