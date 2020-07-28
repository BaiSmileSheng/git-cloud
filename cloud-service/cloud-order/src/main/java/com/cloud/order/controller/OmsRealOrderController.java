package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.domain.entity.vo.OmsRealOrderExcelExportVo;
import com.cloud.order.domain.entity.vo.OmsRealOrderExcelImportVo;
import com.cloud.order.easyexcel.RealOrderWriteHandler;
import com.cloud.order.enums.RealOrderFromEnum;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 真单 提供者
 *
 * @author ltq
 * @date 2020-06-15
 */
@RestController
@RequestMapping("realOrder")
@Api(tags = "真单 提供者")
public class OmsRealOrderController extends BaseController {

    @Autowired
    private IOmsRealOrderService omsRealOrderService;


    /**
     * 查询真单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询真单", response = OmsRealOrder.class)
    public R get(Long id) {
        OmsRealOrder omsRealOrder = omsRealOrderService.selectByPrimaryKey(id);
        return R.data(omsRealOrder);

    }

    /**
     * 查询真单列表
     */
    @GetMapping("list")
    @ApiOperation(value = "真单查询分页", response = OmsRealOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "订单分类", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderClass", value = "订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付日期起始值", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付日期结束值", required = false, paramType = "query", dataType = "String"),
    })
    public TableDataInfo list(@ApiIgnore OmsRealOrder omsRealOrder) {

        Example example = assemblyConditions(omsRealOrder);
        //排产员查对应工厂的数据,业务经理查自己导入的
        SysUser sysUser = getUserInfo(SysUser.class);
        if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
            if (StringUtils.isBlank(omsRealOrder.getProductFactoryCode())) {
                example.and().andIn("productFactoryCode", Arrays.asList(
                        DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        } else if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_SCBJL)){
            example.and().andEqualTo("createBy", sysUser.getLoginName());
        }
        startPage();
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(example);
        return getDataTable(omsRealOrderList);
    }

    /**
     * 组装查询条件
     *
     * @return
     */
    private Example assemblyConditions(OmsRealOrder omsRealOrder) {
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //专用号 工厂 交付日期  订单来源  订单分类  订单类型  客户编号  审核状态
        if (StringUtils.isNotBlank(omsRealOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsRealOrder.getProductMaterialCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsRealOrder.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderFrom())) {
            criteria.andEqualTo("orderFrom", omsRealOrder.getOrderFrom());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderType())) {
            criteria.andEqualTo("orderType", omsRealOrder.getOrderType());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderClass())) {
            criteria.andEqualTo("orderClass", omsRealOrder.getOrderClass());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getCustomerCode())) {
            criteria.andEqualTo("customerCode", omsRealOrder.getCustomerCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", omsRealOrder.getAuditStatus());
        }

        if(StringUtils.isNotBlank(omsRealOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("deliveryDate",omsRealOrder.getBeginTime());
        }
        if(StringUtils.isNotBlank(omsRealOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("deliveryDate", omsRealOrder.getEndTime());
        }
        example.orderBy("createTime").desc();
        return example;
    }

    /**
     * 导出
     *
     * @return
     */
    @HasPermissions("order:realOrder:export")
    @GetMapping("export")
    @ApiOperation(value = "真单导出", response = OmsRealOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "订单分类", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderClass", value = "订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore OmsRealOrder omsRealOrder) {
        Example example = assemblyConditions(omsRealOrder);
        //排产员查对应工厂的数据,业务经理查自己导入的
        SysUser sysUser = getUserInfo(SysUser.class);
        if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
            if (StringUtils.isBlank(omsRealOrder.getProductFactoryCode())) {
                example.and().andIn("productFactoryCode", Arrays.asList(
                        DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        } else if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_SCBJL)){
            example.and().andEqualTo("createBy", sysUser.getLoginName());
        }
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(example);
        String fileName = "真单.xlsx";
        return EasyExcelUtilOSS.writeExcel(omsRealOrderList, fileName, fileName, new OmsRealOrderExcelExportVo());
    }

    /**
     * 导入模板下载
     *
     * @return
     */
    @HasPermissions("order:realOrder:exportExample")
    @GetMapping("exportExample")
    @ApiOperation(value = "导入模板下载", response = OmsRealOrder.class)
    public R exportExample() {
        String fileName = "真单.xlsx";
        return EasyExcelUtilOSS.writePostilExcel(Arrays.asList(), fileName, fileName, new OmsRealOrderExcelImportVo(),new RealOrderWriteHandler());
    }

    /**
     * 内单导入
     * @param file
     * @return
     */
    @HasPermissions("order:realOrder:importByPCY")
    @PostMapping("importByPCY")
    @ApiOperation(value = "内单导入", response = OmsRealOrder.class)
    public R importByPCY(@RequestPart("file") MultipartFile file)throws IOException {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRealOrderService.importRealOrderFile(file,RealOrderFromEnum.ORDER_FROM_1.getCode(),sysUser);
    }

    /**
     * 外单导入
     * @param file
     * @return
     */
    @HasPermissions("order:realOrder:importByYW")
    @PostMapping("importByYW")
    @ApiOperation(value = "外单导入", response = OmsRealOrder.class)
    public R importByYW(@RequestPart("file") MultipartFile file)throws IOException {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRealOrderService.importRealOrderFile(file,RealOrderFromEnum.ORDER_FROM_2.getCode(),sysUser);
    }

    /**
     * 新增保存真单
     */
    @PostMapping("save")
    @OperLog(title = "新增保存真单", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存真单", response = R.class)
    public R addSave(@RequestBody OmsRealOrder omsRealOrder) {
        omsRealOrderService.insertSelective(omsRealOrder);
        return R.data(omsRealOrder.getId());
    }

    /**
     * 修改保存真单
     */
    @HasPermissions("order:realOrder:updateByYWOrPCY")
    @PostMapping("updateByYWOrPCY")
    @OperLog(title = "修改保存真单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存真单", response = R.class)
    public R updateByYWOrPCY(@RequestBody OmsRealOrder omsRealOrder) {
        //排产员查对应工厂的数据,业务经理查自己导入的
        SysUser sysUser = getUserInfo(SysUser.class);
        long userId = getCurrentUserId();
        //修改交付日期时必须加备注
        if(StringUtils.isNotBlank(omsRealOrder.getDeliveryDate()) && StringUtils.isBlank(omsRealOrder.getRemark())){
            return R.error("请填写备注");
        }
        R result = omsRealOrderService.editSaveOmsRealOrder(omsRealOrder, sysUser, userId);
        return result;
    }


    /**
     * 修改保存真单
     */
    @PostMapping("update")
    @OperLog(title = "修改保存真单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存真单", response = R.class)
    public R editSave(@RequestBody OmsRealOrder omsRealOrder) {
        return toAjax(omsRealOrderService.updateByPrimaryKeySelective(omsRealOrder));
    }

    /**
     * 删除真单
     */
    @HasPermissions("order:realOrder:remove")
    @PostMapping("remove")
    @OperLog(title = "删除真单", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除真单", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestParam(value = "ids",required = false) String ids,@RequestBody OmsRealOrder omsRealOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        long currentUserId = getCurrentUserId();
        return omsRealOrderService.deleteOmsRealOrder(ids,omsRealOrder,sysUser,currentUserId);
    }


    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     */
    @PostMapping("timeCollectToOmsRealOrder")
    @OperLog(title = "定时任务每天在获取到PO信息后 进行需求汇总", businessType = BusinessType.INSERT)
    @ApiOperation(value = "定时任务每天在获取到PO信息后 进行需求汇总", response = R.class)
    public R timeCollectToOmsRealOrder() {
        return omsRealOrderService.timeCollectToOmsRealOrder();
    }

}
