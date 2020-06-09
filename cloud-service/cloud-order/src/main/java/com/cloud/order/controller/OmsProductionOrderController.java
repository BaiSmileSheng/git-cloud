package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * 排产订单  提供者
 *
 * @author cs
 * @date 2020-05-29
 */
@RestController
@RequestMapping("productionOrder")
@Api(tags = "排产订单")
public class OmsProductionOrderController extends BaseController {

    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;

    @Autowired
    private RemoteFactoryLineInfoService remoteFactoryLineInfoService;

    /**
     * 查询排产订单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询排产订单 ", response = OmsProductionOrder.class)
    public OmsProductionOrder get(Long id) {
        return omsProductionOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询排产订单 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "排产订单 查询分页", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryDesc", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productEndDate", value = "到", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductionOrder.getFactoryCode())) {
            criteria.andEqualTo("factoryDesc", omsProductionOrder.getFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
            criteria.andEqualTo("productLineCode",omsProductionOrder.getProductLineCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
            criteria.andEqualTo("status",omsProductionOrder.getStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (omsProductionOrder.getProductStartDate()!=null) {
            criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
        }
        if (omsProductionOrder.getProductEndDate()!=null) {
            criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getProductEndDate());
        }
        //查询订单状态已下达和已关单的两个状态的订单
        List<String> statusList = CollectionUtil.toList(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode(),
                ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
        criteria.andIn("status",statusList);

        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
            R r = remoteFactoryLineInfoService.selectLineCodeBySupplierCode(sysUser.getSupplierCode());
            if (r.get("data") == null || StrUtil.isBlank(r.get("data").toString())) {
                return null;
            }
            String lineCodes = r.get("data").toString();
            criteria.andIn("productLineCode",CollectionUtil.toList(lineCodes.split(",")));
        }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //班长、分主管查询工厂下的数据
            if(CollectionUtil.contains(sysUser.getRoleKeys(),RoleConstants.ROLE_KEY_BZ)
            ||CollectionUtil.contains(sysUser.getRoleKeys(),RoleConstants.ROLE_KEY_FZG)){
                criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        }
        startPage();
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return getDataTable(omsProductionOrderList);
    }

    /**
     * 查询排产订单 列表
     * @param productEndDateEnd  基本结束时间 结束值
     * @param actualEndDateStart 实际结束时间 起始值
     * @param actualEndDateEnd 实际结束时间 结束值
     * @return 排产订单 列表
     */
    @GetMapping("listForDelays")
    @ApiOperation(value = "查询延期关单的排产订单", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productEndDateEnd", value = "基本开始日期截止值", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "actualEndDateStart", value = "实际开始日期起始值", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "actualEndDateEnd", value = "实际开始日期结束值", required = true, paramType = "query", dataType = "String")
    })
    public List<OmsProductionOrder> listForDelays(@RequestParam("productEndDateEnd") String productEndDateEnd,
                                                  @RequestParam("actualEndDateStart") String actualEndDateStart,
                                                  @RequestParam("actualEndDateEnd") String actualEndDateEnd){
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(productEndDateEnd)){
            criteria.andLessThan("productEndDate",productEndDateEnd);
        }
        if(StringUtils.isNotBlank(actualEndDateStart)){
            criteria.andGreaterThanOrEqualTo("actualEndDate", actualEndDateStart);
        }
        if(StringUtils.isNotBlank(actualEndDateEnd)){
            criteria.andLessThan("actualEndDate", actualEndDateEnd);
        }
        criteria.andEqualTo("status",ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return omsProductionOrderList;
    }
    /**
     * 根据生产订单号查询排产订单信息
     * @param prodctOrderCode
     * @return OmsProductionOrder
     */
    @GetMapping("selectByProdctOrderCode")
    public OmsProductionOrder selectByProdctOrderCode(String prodctOrderCode) {
        if (StrUtil.isBlank(prodctOrderCode)) {
            throw new BusinessException("参数：生产订单号为空！");
        }
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productOrderCode",prodctOrderCode);
        OmsProductionOrder productionOrder = omsProductionOrderService.findByExampleOne(example);
        return productionOrder;
    }

    /**
     * 新增保存排产订单
     */
    @PostMapping("save")
    @OperLog(title = "新增保存排产订单 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存排产订单 ", response = R.class)
    public R addSave(@RequestBody OmsProductionOrder omsProductionOrder) {
        omsProductionOrderService.insertSelective(omsProductionOrder);
        return R.data(omsProductionOrder.getId());
    }

    /**
     * 修改保存排产订单
     */
    @PostMapping("update")
    @OperLog(title = "修改保存排产订单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存排产订单 ", response = R.class)
    public R editSave(@RequestBody OmsProductionOrder omsProductionOrder) {
        return toAjax(omsProductionOrderService.updateByPrimaryKeySelective(omsProductionOrder));
    }

    /**
     * 删除排产订单
     */
    @PostMapping("remove")
    @OperLog(title = "删除排产订单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除排产订单 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductionOrderService.deleteByIds(ids));
    }

    /**
     * 查询排产订单导出 列表
     */
    @GetMapping("export")
    @ApiOperation(value = "排产订单 导出", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryDesc", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productEndDate", value = "到", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductionOrder.getFactoryCode())) {
            criteria.andEqualTo("factoryDesc", omsProductionOrder.getFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
            criteria.andEqualTo("productLineCode",omsProductionOrder.getProductLineCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
            criteria.andEqualTo("status",omsProductionOrder.getStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (omsProductionOrder.getProductStartDate()!=null) {
            criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
        }
        if (omsProductionOrder.getProductEndDate()!=null) {
            criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getProductEndDate());
        }
        //查询订单状态已下达和已关单的两个状态的订单
//        List<String> statusList = CollectionUtil.toList(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode(),
//                ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
//        criteria.andIn("status",statusList);
//
//        SysUser sysUser = getUserInfo(SysUser.class);
//        if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
//            R r = remoteFactoryLineInfoService.selectLineCodeBySupplierCode(sysUser.getSupplierCode());
//            if (r.get("data") == null || StrUtil.isBlank(r.get("data").toString())) {
//                return null;
//            }
//            String lineCodes = r.get("data").toString();
//            criteria.andIn("productLineCode",CollectionUtil.toList(lineCodes.split(",")));
//        }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
//            //班长、分主管查询工厂下的数据
//            if(CollectionUtil.contains(sysUser.getRoleKeys(),RoleConstants.ROLE_KEY_BZ)
//                    ||CollectionUtil.contains(sysUser.getRoleKeys(),RoleConstants.ROLE_KEY_FZG)){
//                criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
//            }
//        }
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return EasyExcelUtil.writeExcel(omsProductionOrderList, "生产订单.xlsx", "sheet", new OmsProductionOrder());
    }
}
