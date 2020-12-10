package com.cloud.settle.controller;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.vo.SmsRawMaterialScrapOrderZBVo;
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
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.service.ISmsRawMaterialScrapOrderService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.Arrays;
import java.util.List;
/**
 * 原材料报废申请 提供者
 *
 * @author ltq
 * @date 2020-12-07
 */
@RestController
@RequestMapping("rawMaterialScrapOrder")
public class SmsRawMaterialScrapOrderController extends BaseController {

    @Autowired
    private ISmsRawMaterialScrapOrderService smsRawMaterialScrapOrderService;

    /**
     * 查询原材料报废申请
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询原材料报废申请", response = SmsRawMaterialScrapOrder.class)
    public R get(Long id) {
        return R.data(smsRawMaterialScrapOrderService.selectByPrimaryKey(id));

    }

    /**
     * 查询原材料报废申请列表
     */
    @GetMapping("list")
    @ApiOperation(value = "原材料报废申请查询分页", response = SmsRawMaterialScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:rawMaterialScrapOrder:list")
    public TableDataInfo list(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        Example example = checkParams(smsRawMaterialScrapOrder);
        startPage();
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrderList = smsRawMaterialScrapOrderService.selectByExample(example);
        return getDataTable(smsRawMaterialScrapOrderList);
    }

    public Example checkParams(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder){
        Example example = new Example(SmsRawMaterialScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        SysUser sysUser = getUserInfo(SysUser.class);
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getRawScrapNo())) {
            criteria.andEqualTo("rawScrapNo",smsRawMaterialScrapOrder.getRawScrapNo());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getSupplierCode())) {
            criteria.andEqualTo("supplierCode",smsRawMaterialScrapOrder.getSupplierCode());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getRawMaterialCode())) {
            criteria.andEqualTo("rawMaterialCode",smsRawMaterialScrapOrder.getRawMaterialCode());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getFactoryCode())) {
            criteria.andEqualTo("factoryCode",smsRawMaterialScrapOrder.getFactoryCode());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getIsCheck())) {
            criteria.andEqualTo("isCheck",smsRawMaterialScrapOrder.getIsCheck());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getIsMaterialObject())) {
            criteria.andEqualTo("isMaterialObject",smsRawMaterialScrapOrder.getIsMaterialObject());
        }
        if (StrUtil.isNotBlank(smsRawMaterialScrapOrder.getScrapStatus())) {
            criteria.andEqualTo("scrapStatus",smsRawMaterialScrapOrder.getScrapStatus());
        }
        if(StrUtil.isNotEmpty(smsRawMaterialScrapOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsRawMaterialScrapOrder.getEndTime()).offset(DateField.DAY_OF_MONTH,1));
        }
        if(StrUtil.isNotEmpty(smsRawMaterialScrapOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime", smsRawMaterialScrapOrder.getBeginTime());
        }
        //供应商：查询本工厂的    业务科：查询
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_YWK)){
                    //业务科查询已提交状态自己管理工厂的申请单  采购组权限：sys_data_scope  例：8310,8410
                    criteria.andNotEqualTo("scrapStatus", ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode());
                    criteria.andIn("factoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
                }
            }
        }
        return example;
    }
    /**
     * 新增保存原材料报废申请
     */
    @PostMapping("save")
    @OperLog(title = "新增保存原材料报废申请", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存原材料报废申请", response = R.class)
    @HasPermissions("settle:rawMaterialScrapOrder:save")
    public R addSave(@RequestBody SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsRawMaterialScrapOrderService.insetRawScrap(smsRawMaterialScrapOrder,sysUser);
    }

    /**
     * 修改保存原材料报废申请
     */
    @PostMapping("update")
    @OperLog(title = "修改保存原材料报废申请", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存原材料报废申请", response = R.class)
    @HasPermissions("settle:rawMaterialScrapOrder:update")
    public R editSave(@RequestBody SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsRawMaterialScrapOrderService.editRawScrap(smsRawMaterialScrapOrder,sysUser);
    }

    /**
     * 提交原材料报废申请
     */
    @PostMapping("commit")
    @OperLog(title = "提交原材料报废申请", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "提交原材料报废申请", response = R.class)
    @HasPermissions("settle:rawMaterialScrapOrder:commit")
    public R commit(@RequestBody SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return smsRawMaterialScrapOrderService.commit(smsRawMaterialScrapOrder,sysUser);
    }

    /**
     * 删除原材料报废申请
     */
    @PostMapping("remove")
    @OperLog(title = "删除原材料报废申请", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除原材料报废申请", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("settle:rawMaterialScrapOrder:remove")
    public R remove(@RequestBody String ids) {
        return smsRawMaterialScrapOrderService.remove(ids);
    }
    /**
     * 导出原材料报废申请列表
     */
    @GetMapping("export")
    @ApiOperation(value = "原材料报废申请导出", response = SmsRawMaterialScrapOrder.class)
    @HasPermissions("settle:rawMaterialScrapOrder:export")
    public R export(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        Example example = checkParams(smsRawMaterialScrapOrder);
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrderList = smsRawMaterialScrapOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsRawMaterialScrapOrderList,"原材料报废申请表.xlsx","sheet",new SmsRawMaterialScrapOrderZBVo());
    }

    /**
     * 总部导出原材料报废申请列表
     */
    @GetMapping("exportZB")
    @ApiOperation(value = "总部原材料报废申请导出", response = SmsRawMaterialScrapOrder.class)
    @HasPermissions("settle:rawMaterialScrapOrder:exportZB")
    public R exportZB(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {
        Example example = checkParams(smsRawMaterialScrapOrder);
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrderList = smsRawMaterialScrapOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsRawMaterialScrapOrderList,"原材料报废申请表.xlsx","sheet",new SmsRawMaterialScrapOrder());
    }
    /**
     * 根据创建时间查询原材料报废申请
     */
    @GetMapping("listByTime")
    public R listByTime(@RequestParam("createTimeStart") String createTimeStart, @RequestParam("endTimeStart") String endTimeStart){
        Example example = new Example(SmsRawMaterialScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("createTime",createTimeStart);
        criteria.andLessThanOrEqualTo("createTime",endTimeStart);
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrders = smsRawMaterialScrapOrderService.selectByExample(example);
        return R.data(smsRawMaterialScrapOrders);
    }
    /**
     * 定时任务更新价格
     * @param
     * @return
     */
    @PostMapping("updateRawScrapJob")
    public R updateRawScrapJob(){
        return smsRawMaterialScrapOrderService.updateRawScrapJob();
    }
}
