package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.service.ISmsQualityOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 质量索赔  提供者
 *
 * @author cs
 * @date 2020-05-27
 */
@RestController
@RequestMapping("qualityOrder")
@Api(tags = "质量索赔  提供者")
public class SmsQualityOrderController extends BaseController {

    @Autowired
    private ISmsQualityOrderService smsQualityOrderService;


    /**
     * 查询质量索赔
     *
     * @param id 主键id
     * @return SmsQualityOrder 质量索赔信息
     */
    @GetMapping("get")
    @ApiOperation(value = "查询质量索赔", response = SmsQualityOrder.class)
    public SmsQualityOrder get(Long id) {
        return smsQualityOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询质量索赔详情
     *
     * @param id 主键id
     * @return 质量索赔信息详情(包含文件信息)
     */
    @GetMapping("selectById")
    @ApiOperation(value = "查询质量索赔详情", response = SmsQualityOrder.class)
    public R selectById(Long id) {
        return smsQualityOrderService.selectById(id);
    }


    /**
     * 分页查询质量索赔列表
     *
     * @param smsQualityOrder 质量索赔信息
     * @return TableDataInfo 质量索赔分页信息
     */
    @GetMapping("list")
    @ApiOperation(value = "分页查询质量索赔列表", response = SmsQualityOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "qualityNo", value = "索赔单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "qualityStatus", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsQualityOrder smsQualityOrder) {
        Example example = assemblyConditions(smsQualityOrder);
        startPage();
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderService.selectByExample(example);
        return getDataTable(smsQualityOrderList);
    }

    /**
     * 导出查询质量索赔列表
     *
     * @param smsQualityOrder 质量索赔信息
     * @return TableDataInfo 质量索赔分页信息
     */
    @HasPermissions("settle:qualityOrder:export")
    @GetMapping("export")
    @ApiOperation(value = "导出查询质量索赔列表", response = SmsQualityOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qualityNo", value = "索赔单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "qualityStatus", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query", dataType = "String")
    })
    public R export(SmsQualityOrder smsQualityOrder) {
        Example example = assemblyConditions(smsQualityOrder);
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderService.selectByExample(example);
        String fileName = "质量索赔 .xlsx";
        return EasyExcelUtil.writeExcel(smsQualityOrderList, fileName, fileName, new SmsQualityOrder());
    }

    /**
     * 组装查询条件
     *
     * @param smsQualityOrder 质量索赔信息
     * @return
     */
    private Example assemblyConditions(SmsQualityOrder smsQualityOrder) {
        Example example = new Example(SmsQualityOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(smsQualityOrder.getQualityNo())) {
            criteria.andEqualTo("qualityNo", smsQualityOrder.getQualityNo());
        }
        if (StringUtils.isNotBlank(smsQualityOrder.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", smsQualityOrder.getSupplierCode());
        }
        if (StringUtils.isNotBlank(smsQualityOrder.getSupplierName())) {
            criteria.andLike("supplierName", smsQualityOrder.getSupplierName());
        }
        if (StringUtils.isNotBlank(smsQualityOrder.getQualityStatus())) {
            if(QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrder.getQualityStatus())){
                List<String> list = new ArrayList<>();
                list.add(QualityStatusEnum.QUALITY_STATUS_1.getCode());
                list.add(QualityStatusEnum.QUALITY_STATUS_7.getCode());
                criteria.andIn("qualityStatus",list);
            }
            criteria.andEqualTo("qualityStatus", smsQualityOrder.getQualityStatus());
        }
        if (StringUtils.isNotBlank(smsQualityOrder.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("createTime", smsQualityOrder.getBeginTime());
        }
        if (StringUtils.isNotBlank(smsQualityOrder.getEndTime())) {
            criteria.andLessThanOrEqualTo("createTime", smsQualityOrder.getEndTime());
        }
        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if (flagUserType) {
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode", supplierCode);
        }
        return example;
    }

    /**
     * 新增保存质量索赔
     *
     * @param smsQualityOrderReq 质量索赔信息
     * @return 新增id
     */
    @HasPermissions("settle:qualityOrder:save")
    @PostMapping("save")
    @ApiOperation(value = "新增保存质量索赔(包含文件)", response = R.class)
    public R addSave(@RequestParam("smsQualityOrderReq") String smsQualityOrderReq, @RequestParam("files") MultipartFile[] files) {
        SmsQualityOrder smsQualityOrder = JSONObject.parseObject(smsQualityOrderReq, SmsQualityOrder.class);
        //校验入参
        ValidatorUtils.validateEntity(smsQualityOrder);
        String name = getLoginName();
        smsQualityOrder.setCreateBy(name);
        smsQualityOrderService.addSmsQualityOrderAndSysOss(smsQualityOrder, files);
        return R.data(smsQualityOrder.getId());
    }

    /**
     * 修改保存质量索赔
     *
     * @param smsQualityOrder 质量索赔信息
     * @return 修改成功或失败
     */
    @PostMapping("editSave")
    @OperLog(title = "修改保存质量索赔 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存质量索赔 ", response = R.class)
    public R editSave(@RequestBody SmsQualityOrder smsQualityOrder) {
        return toAjax(smsQualityOrderService.updateByPrimaryKeySelective(smsQualityOrder));
    }

    /**
     * 修改保存质量索赔(包含文件信息)
     *
     * @param smsQualityOrderReq 质量索赔信息
     * @return 修改结果成功或失败
     */
    @HasPermissions("settle:qualityOrder:update")
    @PostMapping("update")
    @ApiOperation(value = "修改保存质量索赔(包含文件信息)", response = R.class)
    public R updateQuality(@RequestParam("smsQualityOrder") String smsQualityOrderReq, @RequestParam("files") MultipartFile[] files) {
        SmsQualityOrder smsQualityOrder = JSONObject.parseObject(smsQualityOrderReq, SmsQualityOrder.class);
        //校验入参
        ValidatorUtils.validateEntity(smsQualityOrder,SmsQualityOrder.class);
        smsQualityOrder.setUpdateBy(getLoginName());
        return smsQualityOrderService.updateSmsQualityOrderAndSysOss(smsQualityOrder, files);
    }

    /**
     * 删除质量索赔
     *
     * @param ids 主键
     * @return 删除结果成功或失败
     */
    @PostMapping("remove")
    @HasPermissions("settle:qualityOrder:remove")
    @ApiOperation(value = "删除质量索赔 ", response = R.class)
    public R remove(String ids) {
        return smsQualityOrderService.deleteSmsQualityOrderAndSysOss(ids);
    }

    /**
     * 提交索赔单
     *
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @HasPermissions("settle:qualityOrder:submit")
    @PostMapping("submit")
    @OperLog(title = "提交索赔单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "提交索赔单 ", response = R.class)
    public R submit(String ids) {
        return smsQualityOrderService.submit(ids);
    }

    /**
     * 供应商确认索赔单
     *
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @HasPermissions("settle:qualityOrder:supplierConfirm")
    @PostMapping("supplierConfirm")
    @OperLog(title = "供应商确认索赔单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "供应商确认索赔单 ", response = R.class)
    public R supplierConfirm(String ids) {
        return smsQualityOrderService.supplierConfirm(ids);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param files
     * @return 索赔单供应商申诉结果成功或失败
     */
    @HasPermissions("settle:qualityOrder:supplierAppeal")
    @PostMapping("supplierAppeal")
    @ApiOperation(value = "索赔单供应商申诉 ", response = R.class)
    public R supplierAppeal(@RequestParam("id") Long id, @RequestParam("complaintDescription") String complaintDescription, @RequestParam("files") MultipartFile[] files) {
        SmsQualityOrder smsQualityOrder = new SmsQualityOrder();
        smsQualityOrder.setId(id);
        smsQualityOrder.setComplaintDescription(complaintDescription);
        smsQualityOrder.setUpdateBy(getLoginName());
        return smsQualityOrderService.supplierAppeal(smsQualityOrder, files);
    }

    /**
     * 48H超时未确认发送邮件
     *
     * @return 成功或失败
     */
    @PostMapping("overTimeSendMail")
    @ApiOperation(value = "48H超时未确认发送邮件 ", response = SmsQualityOrder.class)
    public R overTimeSendMail() {
        return smsQualityOrderService.overTimeSendMail();
    }

    /**
     * 72H超时供应商自动确认
     *
     * @return 成功或失败
     */
    @PostMapping("overTimeConfim")
    @ApiOperation(value = "72H超时供应商自动确认 ", response = SmsQualityOrder.class)
    public R overTimeConfim() {
        return smsQualityOrderService.overTimeConfim();
    }
}
