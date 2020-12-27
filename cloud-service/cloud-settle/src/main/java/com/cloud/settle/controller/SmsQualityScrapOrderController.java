package com.cloud.settle.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.domain.entity.vo.SmsQualityScrapOrderSupplierExportVo;
import com.cloud.settle.enums.QualityScrapOrderStatusEnum;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.util.DataScopeUtil;
import com.cloud.settle.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.service.ISmsQualityScrapOrderService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.Arrays;
import java.util.List;

/**
 * 质量报废 提供者
 *
 * @author ltq
 * @date 2020-12-10
 */
@RestController
@RequestMapping("qualityScrapOrder")
@Api(tags = "质量部报废模块")
public class SmsQualityScrapOrderController extends BaseController {

    @Autowired
    private ISmsQualityScrapOrderService smsQualityScrapOrderService;

    /**
     * 查询质量报废
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询质量报废", response = SmsQualityScrapOrder.class)
    public R get(Long id) {
        return R.data(smsQualityScrapOrderService.selectByPrimaryKey(id));

    }

    /**
     * 查询质量报废列表
     */
    @GetMapping("list")
    @ApiOperation(value = "质量报废查询分页", response = SmsQualityScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "订单状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期查询开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "创建日期查询结束", required = false, paramType = "query", dataType = "String"),
    })
    @HasPermissions("settle:qualityScrapOrder:list")
    public TableDataInfo list(SmsQualityScrapOrder smsQualityScrapOrder) {
        Example example = checkParams(smsQualityScrapOrder);
        startPage();
        List<SmsQualityScrapOrder> smsQualityScrapOrderList = smsQualityScrapOrderService.selectByExample(example);
        return getDataTable(smsQualityScrapOrderList);
    }

    public Example checkParams(SmsQualityScrapOrder smsQualityScrapOrder) {
        Example example = new Example(SmsQualityScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        SysUser sysUser = getUserInfo(SysUser.class);
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getScrapNo())) {
            criteria.andEqualTo("scrapNo", smsQualityScrapOrder.getScrapNo());
        }
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", smsQualityScrapOrder.getProductMaterialCode());
        }
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", smsQualityScrapOrder.getSupplierCode());
        }
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getSupplierName())) {
            criteria.andLike("supplierName", "%"+smsQualityScrapOrder.getSupplierName()+"%");
        }
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getFactoryCode())) {
            criteria.andEqualTo("factoryCode", smsQualityScrapOrder.getFactoryCode());
        }
        if (StrUtil.isNotBlank(smsQualityScrapOrder.getScrapStatus())) {
            criteria.andEqualTo("scrapStatus", smsQualityScrapOrder.getScrapStatus());
        }
        if (StrUtil.isNotEmpty(smsQualityScrapOrder.getEndTime())) {
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsQualityScrapOrder.getEndTime()).offset(DateField.DAY_OF_MONTH, 1));
        }
        if (StrUtil.isNotEmpty(smsQualityScrapOrder.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("createTime", smsQualityScrapOrder.getBeginTime());
        }
        //供应商：查询本工厂的    业务科：查询
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
                criteria.andNotEqualTo("scrapStatus", QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DTJ);
            } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //海尔工厂用户根据工厂查询
                criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));

            }
        }
        example.orderBy("createTime").desc();
        return example;
    }


    /**
     * 新增保存质量报废
     */
    @PostMapping("save")
    @OperLog(title = "新增保存质量报废", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存质量报废", response = R.class)
    @HasPermissions("settle:qualityScrapOrder:save")
    public R addSave(@RequestBody SmsQualityScrapOrder smsQualityScrapOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.insertQualityScrap(smsQualityScrapOrder,sysUser);
    }

    /**
     * 新增保存质量报废-多条
     */
    @PostMapping("saveList")
    @OperLog(title = "新增保存质量报废-多条", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存质量报废-多条", response = R.class)
    @HasPermissions("settle:qualityScrapOrder:saveList")
    public R addSaveList(@RequestBody List<SmsQualityScrapOrder> smsQualityScrapOrders) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.insertQualityScrapList(smsQualityScrapOrders,sysUser);
    }

    /**
     * 修改保存质量报废
     */
    @PostMapping("update")
    @OperLog(title = "修改保存质量报废", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存质量报废", response = R.class)
    @HasPermissions("settle:qualityScrapOrder:update")
    public R editSave(@RequestBody SmsQualityScrapOrder smsQualityScrapOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.updateQualityScrap(smsQualityScrapOrder,sysUser);
    }

    /**
     * 删除质量报废
     */
    @PostMapping("remove")
    @OperLog(title = "删除质量报废", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除质量报废", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("settle:qualityScrapOrder:remove")
    public R remove(@RequestBody String ids) {
        return smsQualityScrapOrderService.remove(ids);
    }

    /**
     * 提交保存质量报废
     */
    @PostMapping("commit")
    @OperLog(title = "提交保存质量报废", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "提交保存质量报废", response = R.class)
    @HasPermissions("settle:qualityScrapOrder:commit")
    public R commit(@RequestBody String id) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.commitQualityScrap(id,sysUser);
    }

    /**
     * 查询质量报废列表
     */
    @GetMapping("export")
    @ApiOperation(value = "质量报废查询导出", response = SmsQualityScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "订单状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期查询开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "创建日期查询结束", required = false, paramType = "query", dataType = "String"),
    })
    @HasPermissions("settle:qualityScrapOrder:export")
    public R export(SmsQualityScrapOrder smsQualityScrapOrder) {
        Example example = checkParams(smsQualityScrapOrder);
        List<SmsQualityScrapOrder> smsQualityScrapOrderList = smsQualityScrapOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsQualityScrapOrderList,"质量部报废申请表.xlsx","sheet",new SmsQualityScrapOrder());
    }
    /**
     * 定时更新质量部报废订单价格
     */
    @PostMapping("updatePriceJob")
    public R updatePriceJob(){
        return smsQualityScrapOrderService.updatePriceJob();
    }

    /**
     * 供应商确认
     */
    @PostMapping("confirm")
    @HasPermissions("settle:qualityScrapOrder:confirm")
    public R confirm(@RequestBody String ids){
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.confirm(ids,sysUser);
    }
    /**
     * 质量部报废供应商申诉(包含文件信息)
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @return 质量部报废供应商申诉结果成功或失败
     */
    @HasPermissions("settle:qualityScrapOrder:appealSupplier")
    @PostMapping("appealSupplier")
    @ApiOperation(value = "质量部报废供应商申诉 ", response = R.class)
    public R appealSupplier(@RequestParam("id") Long id, @RequestParam("complaintDescription") String complaintDescription,
                        @RequestParam("ossIds") String ossIds){
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsQualityScrapOrderService.appealSupplier(id,complaintDescription,ossIds,sysUser);
    }
    /**
     * 审批流更新报废数据
     */
    @PostMapping("updateAct")
    public R updateAct(@RequestBody SmsQualityScrapOrder smsQualityScrapOrder, Integer result
            ,String comment
            ,String auditor){
        return smsQualityScrapOrderService.updateAct(smsQualityScrapOrder,result,comment,auditor);
    }

    /**
     * 查询质量报废列表
     */
    @GetMapping("exportSupplier")
    @ApiOperation(value = "质量部报废供应商查询导出", response = SmsQualityScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "订单状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期查询开始", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "创建日期查询结束", required = false, paramType = "query", dataType = "String"),
    })
    @HasPermissions("settle:qualityScrapOrder:exportSupplier")
    public R exportSupplier(SmsQualityScrapOrder smsQualityScrapOrder) {
        Example example = checkParams(smsQualityScrapOrder);
        List<SmsQualityScrapOrder> smsQualityScrapOrderList = smsQualityScrapOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsQualityScrapOrderList,"质量部报废申请表.xlsx","sheet",new SmsQualityScrapOrderSupplierExportVo());
    }
    /**
     * 详情
     */
    @GetMapping("selectDetails")
    @HasPermissions("settle:qualityScrapOrder:selectDetails")
    @ApiOperation(value = "根据id查询质量报废明细-详情", response = R.class)
    public R selectDetails(Long id){
        return smsQualityScrapOrderService.selectDeatils(id);
    }

}
