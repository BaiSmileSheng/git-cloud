package com.cloud.settle.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.domain.entity.SmsSupplementaryOrderZB;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
import com.cloud.settle.util.DataScopeUtil;
import com.cloud.settle.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import com.google.common.collect.Lists;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 物耗申请单
 *
 * @author cs
 * @date 2020-05-26
 */
@RestController
@RequestMapping("supplementary")
@Api(tags = "物耗管理 ")
public class SmsSupplementaryOrderController extends BaseController {

    @Autowired
    private ISmsSupplementaryOrderService smsSupplementaryOrderService;


    /**
     * 查询物耗申请单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询物耗申请单", response = SmsSupplementaryOrder.class)
    public SmsSupplementaryOrder get(Long id) {
        return smsSupplementaryOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询物耗申请单 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "物耗管理查询分页", response = SmsSupplementaryOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder, criteria);
        SysUser sysUser = getUserInfo(SysUser.class);
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //海尔内部
                Example.Criteria criteriaRole = example.createCriteria();
                if (StrUtil.isEmpty(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()))) {
                    return getDataTable(new ArrayList<SmsSupplementaryOrder>());
                }
                if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_JIT)) {
                    //JIT查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                    criteriaRole.orNotEqualTo("stuffStatus", SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode());
                    criteriaRole.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
                if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)) {
                    List<String> statusXWZ = CollectionUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZBH.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JSWC.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_YDX.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_WDX.getCode());
                    criteriaRole.orIn("stuffStatus", statusXWZ);
                    //小微主查看jit审核成功的自己工厂权限下的物耗申请单
                    criteriaRole.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
                example.and(criteriaRole);
            }
        }
        startPage();
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return getDataTable(smsSupplementaryOrderList);
    }


    /**
     * 查询物耗申请单 列表-总部
     */
    @GetMapping("listGeneral")
    @ApiOperation(value = "物耗管理查询分页-总部", response = SmsSupplementaryOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo listGeneral(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder, criteria);
        startPage();
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return getDataTable(smsSupplementaryOrderList);
    }

    /**
     * 根据条件查询列表
     */
    @GetMapping("listByCondition")
    @ApiOperation(value = "根据条件查询列表", response = SmsSupplementaryOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String")
    })
    public R listByCondition(SmsSupplementaryOrder smsSupplementaryOrder){
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder, criteria);
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return R.data(smsSupplementaryOrderList);
    }

    /**
     * 根据创建时间查询物耗申请单 列表
     */
    @GetMapping("listByTime")
    public R listByTime(@RequestParam("createTimeStart") String createTimeStart,@RequestParam("endTimeStart") String endTimeStart){
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("createTime",createTimeStart);
        criteria.andLessThanOrEqualTo("createTime",endTimeStart);
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return R.data(smsSupplementaryOrderList);
    }
    /**
     * 分页查询条件
     *
     * @param smsSupplementaryOrder
     * @param criteria
     */
    void listCondition(SmsSupplementaryOrder smsSupplementaryOrder, Example.Criteria criteria) {
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getStuffNo())) {
            criteria.andEqualTo("stuffNo", smsSupplementaryOrder.getStuffNo());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getProductOrderCode())) {
            criteria.andEqualTo("productOrderCode", smsSupplementaryOrder.getProductOrderCode());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", smsSupplementaryOrder.getSupplierCode());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getSupplierName())) {
            criteria.andLike("supplierName", StringUtils.format("%{}%",smsSupplementaryOrder.getSupplierName()));
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getRawMaterialCode())) {
            criteria.andEqualTo("rawMaterialCode", smsSupplementaryOrder.getRawMaterialCode());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getStuffStatus())) {
            criteria.andEqualTo("stuffStatus", smsSupplementaryOrder.getStuffStatus());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("createTime", smsSupplementaryOrder.getBeginTime());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getEndTime())) {
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsSupplementaryOrder.getEndTime()).offset(DateField.DAY_OF_MONTH,1));
        }
    }

    /**
     * 新增保存物耗申请单
     */
    @PostMapping("saveList")
    @ApiOperation(value = "新增保存物耗申请单参数为List", response = R.class)
    @HasPermissions("settle:supplementary:save")
    public R addSave(@RequestBody List<SmsSupplementaryOrder> smsSupplementaryOrders) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsSupplementaryOrderService.addSaveList(smsSupplementaryOrders,sysUser);
    }

    /**
     * 新增保存物耗申请单
     */
    @PostMapping("save")
    public R addSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsSupplementaryOrderService.addSave(smsSupplementaryOrder,sysUser);
    }

    /**
     * 修改保存物耗申请单 -- 无状态校验
     */
    @PostMapping("update")
    public R update(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        return toAjax(smsSupplementaryOrderService.updateByPrimaryKeySelective(smsSupplementaryOrder));
    }

    /**
     * 编辑保存物耗申请单功能  --有逻辑校验
     */
    @PostMapping("editSaveList")
    @OperLog(title = "修改保存物耗申请单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物耗申请单List ", response = R.class)
    @HasPermissions("settle:supplementary:save")
    public R editSave(@RequestBody List<SmsSupplementaryOrder> smsSupplementaryOrders) {
        return smsSupplementaryOrderService.editSaveList(smsSupplementaryOrders);
    }

    /**
     * 编辑保存物耗申请单功能  --有逻辑校验
     */
    @PostMapping("editSave")
    @OperLog(title = "修改保存物耗申请单 ", businessType = BusinessType.UPDATE)
    public R editSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        return smsSupplementaryOrderService.editSave(smsSupplementaryOrder);
    }

    /**
     * 删除物耗申请单
     */
    @PostMapping("remove")
    @OperLog(title = "删除物耗申请单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物耗申请单 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("settle:supplementary:remove")
    public R remove(@RequestBody String ids) {
        return smsSupplementaryOrderService.remove(ids);
    }

    /**
     * 定时任务更新指定月份原材料价格到物耗表
     *
     * @param month
     * @return
     */
    @GetMapping("updatePriceEveryMonth")
    @ApiOperation(value = "定时任务更新指定月份原材料价格到物耗表 ", response = R.class)
    public R updatePriceEveryMonth(String month) {
        return smsSupplementaryOrderService.updatePriceEveryMonth(month);
    }

    /**
     * 查询物耗申请单 导出
     */
    @GetMapping("export")
    @ApiOperation(value = "物耗管理导出", response = SmsSupplementaryOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:supplementary:export")
    public R export(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder, criteria);
        SysUser sysUser = getUserInfo(SysUser.class);
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //海尔内部
                Example.Criteria criteriaRole = example.createCriteria();
                if (StrUtil.isEmpty(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()))) {
                    return EasyExcelUtilOSS.writeExcel(new ArrayList<SmsSupplementaryOrder>(), "物耗申请.xlsx", "sheet", new SmsSupplementaryOrder());
                }
                if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_JIT)) {
                    //JIT查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                    criteriaRole.andNotEqualTo("stuffStatus", SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode());
                    criteriaRole.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
                if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)) {
                    //小微主查看jit审核成功的自己工厂权限下的物耗申请单
                    List<String> statusXWZ = CollectionUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZBH.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JSWC.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_YDX.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode(),
                            SupplementaryOrderStatusEnum.WH_ORDER_STATUS_WDX.getCode());
                    criteriaRole.orIn("stuffStatus", statusXWZ);
                    criteriaRole.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
                example.and(criteriaRole);
            }
        }
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsSupplementaryOrderList, "物耗申请.xlsx", "sheet", new SmsSupplementaryOrder());
    }

    /**
     * 查询物耗申请单 导出-总部
     */
    @GetMapping("exportGeneral")
    @ApiOperation(value = "物耗管理查询导出-总部", response = SmsSupplementaryOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:supplementary:exportZB")
    public R exportGeneral(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder, criteria);
        startPage();
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);

        List<SmsSupplementaryOrderZB> smsSupplementaryOrderZBList = Lists.transform(smsSupplementaryOrderList, (entity) -> {
            SmsSupplementaryOrderZB vo = new SmsSupplementaryOrderZB();
            BeanUtil.copyProperties(entity,vo);
            return vo;
        });
        return EasyExcelUtilOSS.writeExcel(smsSupplementaryOrderZBList, "物耗申请总部.xlsx", "sheet", new SmsSupplementaryOrderZB());
    }

    /**
     * 业务科审批通过传SAPY61
     * @param smsSupplementaryOrder
     * @return
     */
    @PostMapping("autidSuccessToSAPY61")
    public R autidSuccessToSAPY61(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder){
        return smsSupplementaryOrderService.autidSuccessToSAPY61(smsSupplementaryOrder);
    }

    /**
     * 根据状态查物料号
     * @param status
     * @return 物料号集合
     */
    @GetMapping("materialCodeListByStatus")
    @ApiOperation(value = "根据状态查物料号", response = String.class)
    public R materialCodeListByStatus(String status){
        return R.data(smsSupplementaryOrderService.materialCodeListByStatus(status));
    }
}
