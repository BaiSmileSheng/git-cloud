package com.cloud.settle.controller;

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
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.settle.util.DataScopeUtil;
import com.cloud.settle.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import com.sap.conn.jco.JCoException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * 报废申请  提供者
 *
 * @author cs
 * @date 2020-05-29
 */
@RestController
@RequestMapping("scrapOrder")
@Api(tags = "报废申请单")
public class SmsScrapOrderController extends BaseController {

    @Autowired
    private ISmsScrapOrderService smsScrapOrderService;



    /**
     * 查询报废申请
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询报废申请 ", response = SmsScrapOrder.class)
    public SmsScrapOrder get(Long id) {
        return smsScrapOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询报废申请 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "报废申请 查询分页", response = SmsScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "报废状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "到", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsScrapOrder smsScrapOrder) {
        Example example = new Example(SmsScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        SysUser sysUser = getUserInfo(SysUser.class);
        criteria.andEqualTo(smsScrapOrder);
        if(StrUtil.isNotEmpty(smsScrapOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsScrapOrder.getEndTime()).offset(DateField.DAY_OF_MONTH,1));
        }
        if(StrUtil.isNotEmpty(smsScrapOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime", smsScrapOrder.getBeginTime());
        }
        //供应商：查询本工厂的    业务科：查询
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_YWK)){
                    //业务科查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                    criteria.andEqualTo("scrapStatus", ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
                    criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
            }
        }
        startPage();
        List<SmsScrapOrder> smsScrapOrderList = smsScrapOrderService.selectByExample(example);
        return getDataTable(smsScrapOrderList);
    }


    /**
     * 新增保存报废申请
     */
    @PostMapping("save")
    @OperLog(title = "新增保存报废申请 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存报废申请 ", response = R.class)
    @HasPermissions("settle:scrapOrder:save")
    public R addSave(@RequestBody SmsScrapOrder smsScrapOrder) {
        if (StrUtil.isNotEmpty(getLoginName())) {
            smsScrapOrder.setCreateBy(getLoginName());
        }
        return smsScrapOrderService.addSave(smsScrapOrder);
    }

    /**
     * 修改保存报废申请
     */
    @PostMapping("update")
    public R update(@RequestBody SmsScrapOrder smsScrapOrder) {
        return toAjax(smsScrapOrderService.updateByPrimaryKeySelective(smsScrapOrder));
    }

    /**
     * 编辑报废申请单功能  --有状态校验
     */
    @PostMapping("editSave")
    @OperLog(title = "修改保存报废申请 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存报废申请 ", response = R.class)
    @HasPermissions("settle:scrapOrder:save")
    public R editSave(@RequestBody SmsScrapOrder smsScrapOrder) {
        if (StrUtil.isNotEmpty(getLoginName())) {
            smsScrapOrder.setUpdateBy(getLoginName());
        }
        return smsScrapOrderService.editSave(smsScrapOrder);

    }

    /**
     * 删除报废申请
     */
    @PostMapping("remove")
    @OperLog(title = "删除报废申请 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除报废申请 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return smsScrapOrderService.remove(ids);
    }

    /**
     * 定时任务更新指定月份SAP销售价格
     * @param month
     * @return
     */
    @GetMapping("updateSAPPriceEveryMonth")
    @ApiOperation(value = "定时任务更新指定月份销售价格 ", response = R.class)
    public R updateSAPPriceEveryMonth(String month) throws JCoException {
        return smsScrapOrderService.updateSAPPriceEveryMonth(month);
    }

    /**
     * 定时任务更新指定月份销售价格到报废表
     * @param month
     * @return
     */
    @GetMapping("updatePriceEveryMonth")
    @ApiOperation(value = "定时任务更新指定月份销售价格到报废表 ", response = R.class)
    public R updatePriceEveryMonth(String month){
        return smsScrapOrderService.updatePriceEveryMonth(month);
    }

    /**
     * 查询报废申请 导出
     */
    @GetMapping("export")
    @ApiOperation(value = "报废申请 导出", response = SmsScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "报废状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "到", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:scrapOrder:export")
    public R export(@ApiIgnore() SmsScrapOrder smsScrapOrder) {
        Example example = new Example(SmsScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(smsScrapOrder);
        SysUser sysUser = getUserInfo(SysUser.class);
        if(StrUtil.isNotEmpty(smsScrapOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsScrapOrder.getEndTime()).offset(DateField.DAY_OF_MONTH,1));
        }
        if(StrUtil.isNotEmpty(smsScrapOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime", smsScrapOrder.getBeginTime());
        }
        //供应商：查询本工厂的    业务科：查询
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_YWK)){
                    //业务科查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                    criteria.andEqualTo("scrapStatus", ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
                    criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
            }
        }
        List<SmsScrapOrder> smsScrapOrderList = smsScrapOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsScrapOrderList,"报废申请.xlsx","sheet",new SmsScrapOrder());
    }

    /**
     * 业务科审批通过传SAP261
     * @param smsScrapOrder
     * @return
     */
    @PostMapping("autidSuccessToSAP261")
    public R autidSuccessToSAP261(@RequestBody SmsScrapOrder smsScrapOrder){
        return smsScrapOrderService.autidSuccessToSAP261(smsScrapOrder);
    }
}
