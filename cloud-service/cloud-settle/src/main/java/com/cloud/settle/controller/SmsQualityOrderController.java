package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.service.ISmsQualityOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 质量索赔  提供者
 *
 * @author cs
 * @date 2020-05-27
 */
@RestController
@RequestMapping("qualityOrder")
public class SmsQualityOrderController extends BaseController {

    @Autowired
    private ISmsQualityOrderService smsQualityOrderService;


    /**
     * 查询质量索赔
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
     * @param smsQualityOrder  质量索赔信息
     * @return TableDataInfo 质量索赔分页信息
     */
    @GetMapping("list")
    @ApiOperation(value = "分页查询质量索赔列表", response = SmsQualityOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsQualityOrder smsQualityOrder) {
        Example example = new Example(SmsQualityOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(smsQualityOrder.getQualityNo())){
            criteria.andEqualTo("qualityNo",smsQualityOrder.getQualityNo());
        }
        if(StringUtils.isNotBlank(smsQualityOrder.getSupplierCode())){
            criteria.andEqualTo("supplierCode",smsQualityOrder.getSupplierCode());
        }
        if(StringUtils.isNotBlank(smsQualityOrder.getSupplierName())){
            criteria.andLike("supplierName",smsQualityOrder.getSupplierName());
        }
        if(StringUtils.isNotBlank(smsQualityOrder.getQualityStatus())){
            criteria.andEqualTo("qualityStatus",smsQualityOrder.getQualityStatus());
        }
        if(StringUtils.isNotBlank(smsQualityOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime",smsQualityOrder.getBeginTime());
        }
        if(StringUtils.isNotBlank(smsQualityOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime",smsQualityOrder.getEndTime());
        }
        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if(flagUserType){
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode",supplierCode);
        }
        startPage();
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderService.selectByExample(example);
        return getDataTable(smsQualityOrderList);
    }


    /**
     * 新增保存质量索赔
     * @param smsQualityOrderReq 质量索赔信息
     * @return 新增id
     */
    @PostMapping("save")
    @ApiOperation(value = "新增保存质量索赔 ", response = SmsQualityOrder.class)
    public R addSave(@RequestParam("smsQualityOrderReq") String smsQualityOrderReq,@RequestParam("files") MultipartFile[] files) {
        try{
            SmsQualityOrder smsQualityOrder = JSONObject.parseObject(smsQualityOrderReq,SmsQualityOrder.class);
            smsQualityOrderService.addSmsQualityOrderAndSysOss(smsQualityOrder,files);
            return R.data(smsQualityOrder.getId());
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 修改保存质量索赔
     * @param smsQualityOrderReq 质量索赔信息
     * @return 修改结果成功或失败
     */
    @PostMapping("update")
    @ApiOperation(value = "修改保存质量索赔 ", response = SmsQualityOrder.class)
    public R editSave(@RequestParam("smsQualityOrder") String smsQualityOrderReq,@RequestParam("files") MultipartFile[] files) {
        try{
            SmsQualityOrder smsQualityOrder = JSONObject.parseObject(smsQualityOrderReq,SmsQualityOrder.class);
            return smsQualityOrderService.updateSmsQualityOrderAndSysOss(smsQualityOrder,files);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除质量索赔
     * @param ids 主键
     * @return 删除结果成功或失败
     */
    @PostMapping("remove")
    @ApiOperation(value = "删除质量索赔 ", response = SmsQualityOrder.class)
    public R remove(String ids) {
        try{
            return smsQualityOrderService.deleteSmsQualityOrderAndSysOss(ids);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 提交索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @PostMapping("submit")
    @OperLog(title = "提交索赔单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "提交索赔单 ", response = SmsQualityOrder.class)
    public R submit(String ids){
        try{
            return smsQualityOrderService.submit(ids);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @PostMapping("supplierConfirm")
    @OperLog(title = "供应商确认索赔单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "供应商确认索赔单 ", response = SmsQualityOrder.class)
    public R supplierConfirm(String ids){
        try{
            return smsQualityOrderService.supplierConfirm(ids);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

}
