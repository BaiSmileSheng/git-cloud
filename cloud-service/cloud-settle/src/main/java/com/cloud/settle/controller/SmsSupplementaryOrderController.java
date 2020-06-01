package com.cloud.settle.controller;

import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
import com.cloud.settle.util.DataScopeUtil;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

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

    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;

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
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder,criteria);
        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
            //供应商查询自己工厂下的申请单
            criteria.andLike("supplierCode", sysUser.getSupplierCode());
        }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //海尔内部
            if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_JIT)){
                //JIT查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                criteria.andEqualTo("stuffStatus", SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
                criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }else if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)){
                //小微主查看jit审核成功的自己工厂权限下的物耗申请单
                criteria.andEqualTo("stuffStatus", SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode());
                criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        }else{
            return null;
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
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffNo", value = "物耗单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "stuffStatus", value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "申请日期开始", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "申请日期结束", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo listGeneral(@ApiIgnore() SmsSupplementaryOrder smsSupplementaryOrder) {
        Example example = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(smsSupplementaryOrder,criteria);
        startPage();
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByExample(example);
        return getDataTable(smsSupplementaryOrderList);
    }

    /**
     * 分页查询条件
     * @param smsSupplementaryOrder
     * @param criteria
     */
    void listCondition(SmsSupplementaryOrder smsSupplementaryOrder,Example.Criteria criteria) {
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getStuffNo())) {
            criteria.andLike("stuffNo", smsSupplementaryOrder.getStuffNo());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getSupplierCode())) {
            criteria.andLike("supplierCode", smsSupplementaryOrder.getSupplierCode());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getSupplierName())) {
            criteria.andLike("supplierName", smsSupplementaryOrder.getSupplierName());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getRawMaterialCode())) {
            criteria.andLike("rawMaterialCode", smsSupplementaryOrder.getRawMaterialCode());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getStuffStatus())) {
            criteria.andEqualTo("stuffStatus", smsSupplementaryOrder.getStuffStatus());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("createTime", smsSupplementaryOrder.getBeginTime());
        }
        if (StringUtils.isNotBlank(smsSupplementaryOrder.getEndTime())) {
            criteria.andLessThanOrEqualTo("createTime", smsSupplementaryOrder.getEndTime());
        }
    }

    /**
     * 新增保存物耗申请单
     */
    @PostMapping("save")
    @OperLog(title = "新增保存物耗申请单 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存物耗申请单 ", response = R.class)
    public R addSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        smsSupplementaryOrderService.insertSelective(smsSupplementaryOrder);
        return R.data(smsSupplementaryOrder.getId());
    }

    /**
     * 修改保存物耗申请单 -- 无状态校验
     */
    @PostMapping("update")
    public R update(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        return toAjax(smsSupplementaryOrderService.updateByPrimaryKeySelective(smsSupplementaryOrder));
    }

    /**
     * 编辑物耗申请单功能  --有状态校验
     */
    @PostMapping("editSave")
    @OperLog(title = "修改保存物耗申请单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物耗申请单 ", response = R.class)
    public R editSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        Long id = smsSupplementaryOrder.getId();
        //判断状态是否是未提交
        R rCheck=checkCondition(id);
        SmsSupplementaryOrder smsSupplementaryOrderCheck = (SmsSupplementaryOrder) rCheck.get("data");
        //校验物料号是否同步了sap价格
        R r=remoteCdMaterialPriceInfoService.checkSynchroSAP(smsSupplementaryOrderCheck.getRawMaterialCode());
        if(r.isSuccess()){
            return toAjax(smsSupplementaryOrderService.updateByPrimaryKeySelective(smsSupplementaryOrder));
        }else {
            return r;
        }

    }

    /**
     * 删除物耗申请单
     */
    @PostMapping("remove")
    @OperLog(title = "删除物耗申请单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物耗申请单 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        if(StringUtils.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        return toAjax(smsSupplementaryOrderService.deleteByIds(ids));
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return 返回SmsSupplementaryOrder信息
     */
    public R checkCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsSupplementaryOrder smsSupplementaryOrderCheck = smsSupplementaryOrderService.selectByPrimaryKey(id);
        if (smsSupplementaryOrderCheck == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode().equals(smsSupplementaryOrderCheck.getStuffStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsSupplementaryOrderCheck);
    }
}
