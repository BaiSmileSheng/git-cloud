package com.cloud.settle.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.enums.ClaimOtherStatusEnum;
import com.cloud.settle.service.ISmsClaimOtherService;
import com.cloud.settle.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 其他索赔 提供者
 *
 * @author cs
 * @date 2020-06-02
 */
@RestController
@RequestMapping("claimOther")
@Api(tags = "其他索赔 提供者")
public class SmsClaimOtherController extends BaseController {

    @Autowired
    private ISmsClaimOtherService smsClaimOtherService;

    /**
     * 查询其他索赔
     * @param id 主键
     * @return 成功或失败
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询其他索赔", response = SmsClaimOther.class)
    public R get(Long id) {
        SmsClaimOther smsClaimOther = smsClaimOtherService.selectByPrimaryKey(id);
        return R.data(smsClaimOther);

    }

    /**
     * 查询其他索赔(包含文件信息)
     * @param id 主键
     * @return 成功或失败
     */
    @GetMapping("selectById")
    @ApiOperation(value = "查询其他索赔(包含文件信息)", response = R.class)
    public R selectById(Long id) {
        return smsClaimOtherService.selectById(id);

    }


    /**
     * 查询其他索赔列表
     */
    @GetMapping("list")
    @ApiOperation(value = "其他索赔查询分页", response = SmsClaimOther.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "claimCode", value = "索赔单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "claimOtherStatus", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore SmsClaimOther smsClaimOther) {
        Example example = assemblyConditions(smsClaimOther);
        startPage();
        List<SmsClaimOther> smsClaimOtherList = smsClaimOtherService.selectByExample(example);
        return getDataTable(smsClaimOtherList);
    }

    /**
     * 导出其他索赔列表
     * @param smsClaimOther 其他索赔查询条件
     * @return
     */
    @HasPermissions("settle:claimOther:export")
    @GetMapping("export")
    @ApiOperation(value = "导出其他索赔列表", response = SmsClaimOther.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "claimCode", value = "索赔单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "claimOtherStatus", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore SmsClaimOther smsClaimOther) {
        Example example = assemblyConditions(smsClaimOther);
        List<SmsClaimOther> smsClaimOtherList = smsClaimOtherService.selectByExample(example);
        String fileName = "其他索赔.xlsx";
        return EasyExcelUtilOSS.writeExcel(smsClaimOtherList,fileName,fileName,new SmsClaimOther());
    }

    /**
     * 组装查询条件
     * @param smsClaimOther 其他索赔
     * @return
     */
    private Example assemblyConditions(SmsClaimOther smsClaimOther){
        Example example = new Example(SmsClaimOther.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(smsClaimOther.getProductOrderCode())){
            criteria.andEqualTo("productOrderCode",smsClaimOther.getProductOrderCode());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getClaimCode())){
            criteria.andEqualTo("claimCode",smsClaimOther.getClaimCode());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getSupplierCode())){
            criteria.andEqualTo("supplierCode",smsClaimOther.getSupplierCode());
        }

        if(StringUtils.isNotBlank(smsClaimOther.getClaimOtherStatus())){
            if(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode().equals(smsClaimOther.getClaimOtherStatus())){
                List<String> list = new ArrayList<>();
                list.add(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode());
                list.add(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_7.getCode());
                criteria.andIn("claimOtherStatus",list);
            }else{
                criteria.andEqualTo("claimOtherStatus",smsClaimOther.getClaimOtherStatus());
            }
        }
        if(StringUtils.isNotBlank(smsClaimOther.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime",smsClaimOther.getBeginTime());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime", DateUtil.parse(smsClaimOther.getEndTime()).offset(DateField.DAY_OF_MONTH,1));
        }
        example.orderBy("createTime").desc();
        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if(flagUserType) {
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode", supplierCode);
            criteria.andNotEqualTo("claimOtherStatus",ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_0.getCode());
        }
        return example;
    }

    /**
     * 新增其他索赔信息(包含文件信息)
     * @param ossIds
     * @return
     */
    @HasPermissions("settle:claimOther:save")
    @PostMapping("save")
    @ApiOperation(value = "新增其他索赔信息(包含文件信息)", response = R.class)
    public R addSave(@RequestParam("smsClaimOther") String smsClaimOtherReq,@RequestParam(value = "ossIds",required = false)String ossIds) {
        SmsClaimOther smsClaimOther = JSONObject.parseObject(smsClaimOtherReq,SmsClaimOther.class);
        //校验入参
        ValidatorUtils.validateEntity(smsClaimOther);
        smsClaimOther.setCreateBy(getLoginName());
        R result = smsClaimOtherService.insertClaimOtherAndOss(smsClaimOther,ossIds);
        return result;
    }

    /**
     * 修改保存其他索赔(包含图片信息)
     * @param smsClaimOtherReq  其他索赔信息
     * @param ossIds  文件
     * @return 修改成功或失败
     */
    @HasPermissions("settle:claimOther:updateClaimOther")
    @PostMapping("updateClaimOther")
    @ApiOperation(value = "修改保存其他索赔(包含图片信息)", response = R.class)
    public R updateClaimOtherAndOss(@RequestParam("smsClaimOther") String smsClaimOtherReq,@RequestParam(value = "ossIds",required = false)String ossIds) {
        SmsClaimOther smsClaimOther = JSONObject.parseObject(smsClaimOtherReq,SmsClaimOther.class);
        //校验入参
        ValidatorUtils.validateEntity(smsClaimOther);
        smsClaimOther.setUpdateBy(getLoginName());
        R result = smsClaimOtherService.updateClaimOtherAndOss(smsClaimOther,ossIds);
        return result;
    }

    /**
     * 修改保存其他索赔
     * @param smsClaimOther 其他索赔信息
     * @return 成功或失败
     */
    @PostMapping("update")
    @OperLog(title = "修改保存其他索赔", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存其他索赔", response = R.class)
    public R editSave(@RequestBody SmsClaimOther smsClaimOther) {
        return toAjax(smsClaimOtherService.updateByPrimaryKeySelective(smsClaimOther));
    }

    /**
     * 删除其他索赔
     * @param ids 主键
     * @return 成功或失败
     */
    @HasPermissions("settle:claimOther:remove")
    @PostMapping("remove")
    @OperLog(title = "删除其他索赔", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除其他索赔", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestParam("ids") String ids) {
        R result = smsClaimOtherService.deleteClaimOtherAndOss(ids);
        return result;
    }

    /**
     * 提交其他索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @HasPermissions("settle:claimOther:submit")
    @PostMapping("submit")
    @OperLog(title = "提交其他索赔单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "提交其他索赔单", response = R.class)
    public R submit(String ids){
        return smsClaimOtherService.submit(ids);
    }

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @HasPermissions("settle:claimOther:supplierConfirm")
    @PostMapping("supplierConfirm")
    @OperLog(title = "供应商确认索赔单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "供应商确认索赔单 ", response = R.class)
    public R supplierConfirm(String ids){
        return smsClaimOtherService.supplierConfirm(ids);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @return 索赔单供应商申诉结果成功或失败
     */
    @HasPermissions("settle:claimOther:supplierAppeal")
    @PostMapping("supplierAppeal")
    @ApiOperation(value = "索赔单供应商申诉 ", response = R.class)
    public R supplierAppeal(@RequestParam("id") Long id,@RequestParam("complaintDescription")String complaintDescription, @RequestParam("ossIds")String ossIds) {
        SmsClaimOther smsClaimOther = new SmsClaimOther();
        smsClaimOther.setId(id);
        smsClaimOther.setComplaintDescription(complaintDescription);
        smsClaimOther.setUpdateBy(getLoginName());
        return smsClaimOtherService.supplierAppeal(smsClaimOther,ossIds);
    }

    /**
     * 48H超时未确认发送邮件
     * @return 成功或失败
     */
    @PostMapping("overTimeSendMail")
    @ApiOperation(value = "48H超时未确认发送邮件 ", response = SmsClaimOther.class)
    public R overTimeSendMail(){
        return smsClaimOtherService.overTimeSendMail();
    }

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @PostMapping("overTimeConfim")
    @ApiOperation(value = "72H超时供应商自动确认 ", response = SmsClaimOther.class)
    public R overTimeConfim(){
        return smsClaimOtherService.overTimeConfim();
    }
}
