package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.service.ISmsDelaysDeliveryService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
/**
 * 延期交付索赔  提供者
 *
 * @author cs
 * @date 2020-06-01
 */
@RestController
@RequestMapping("delaysDelivery")
public class SmsDelaysDeliveryController extends BaseController {

    @Autowired
    private ISmsDelaysDeliveryService smsDelaysDeliveryService;

    /**
     * 查询延期交付索赔
     * @param id
     * @return 延期交付索赔信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询延期交付索赔 ", response = SmsDelaysDelivery.class)
    public SmsDelaysDelivery get(Long id) {
        return smsDelaysDeliveryService.selectByPrimaryKey(id);

    }

    /**
     * 查询延期交付索赔详情
     * @param id 主键id
     * @return 延期交付索赔详情(包含文件信息)
     */
    @GetMapping("selectById")
    @ApiOperation(value = "查询延期交付索赔详情", response = SmsDelaysDelivery.class)
    public R selectById(Long id) {
        return smsDelaysDeliveryService.selectById(id);
    }

    /**
     * 查询延期交付索赔 列表
     * @param smsDelaysDelivery
     * @return 延期交付索赔列表
     */
    @GetMapping("list")
    @ApiOperation(value = "延期交付索赔 查询分页", response = SmsDelaysDelivery.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "delaysNo", value = "索赔单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "delaysStatus", value = "索赔状态", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生成订单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false,paramType = "query", dataType = "String"),

    })
    public TableDataInfo list(@ApiIgnore SmsDelaysDelivery smsDelaysDelivery) {
        Example example = new Example(SmsDelaysDelivery.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(smsDelaysDelivery.getDelaysNo())){
            criteria.andEqualTo("delaysNo",smsDelaysDelivery.getDelaysNo());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getSupplierCode())){
            criteria.andEqualTo("supplierCode",smsDelaysDelivery.getSupplierCode());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getDelaysStatus())){
            criteria.andEqualTo("delaysStatus",smsDelaysDelivery.getDelaysStatus());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getProductLineCode())){
            criteria.andEqualTo("productLineCode",smsDelaysDelivery.getProductLineCode());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getProductOrderCode())){
            criteria.andEqualTo("productOrderCode",smsDelaysDelivery.getProductOrderCode());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime",smsDelaysDelivery.getBeginTime());
        }
        if(StringUtils.isNotBlank(smsDelaysDelivery.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime",smsDelaysDelivery.getEndTime());
        }
        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if(flagUserType){
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode",supplierCode);
        }
        startPage();
        List<SmsDelaysDelivery> smsDelaysDeliveryList = smsDelaysDeliveryService.selectByExample(example);
        return getDataTable(smsDelaysDeliveryList);
    }

    /**
     * 新增保存延期交付索赔
     * @param smsDelaysDelivery
     * @return 成功或失败
     */
    @PostMapping("save")
    @OperLog(title = "新增保存延期交付索赔 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存延期交付索赔 ", response = R.class)
    public R addSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery) {
        smsDelaysDeliveryService.insertSelective(smsDelaysDelivery);
        return R.data(smsDelaysDelivery.getId());
    }


    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    @PostMapping("batchAddDelaysDelivery")
    @OperLog(title = "批量新增保存延期交付索赔(并发送邮件) ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "批量新增保存延期交付索赔(并发送邮件) ", response = R.class)
    public R batchAddDelaysDelivery() {

        return smsDelaysDeliveryService.batchAddDelaysDelivery();
    }

    /**
     * 修改保存延期交付索赔
     * @param smsDelaysDelivery
     * @return 成功或失败
     */
    @PostMapping("update")
    @OperLog(title = "修改保存延期交付索赔 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存延期交付索赔 ", response = R.class)
    public R editSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery) {
        return toAjax(smsDelaysDeliveryService.updateByPrimaryKeySelective(smsDelaysDelivery));
    }

    /**
     * 删除延期交付索赔
     * @param ids
     * @return 成功或失败
     */
    @PostMapping("remove")
    @OperLog(title = "删除延期交付索赔 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除延期交付索赔 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsDelaysDeliveryService.deleteByIds(ids));
    }

    /**
     * 延期索赔单供应商申诉(包含文件信息)
     * @param smsDelaysDeliveryReq 延期索赔信息
     * @return 延期索赔单供应商申诉结果成功或失败
     */
    @PostMapping("supplierAppeal")
    @ApiOperation(value = "延期索赔单供应商申诉(包含文件信息) ", response = SmsDelaysDelivery.class)
    public R supplierAppeal(@RequestParam("smsQualityOrder") String smsDelaysDeliveryReq, @RequestParam("files") MultipartFile[] files) {
        try{
            SmsDelaysDelivery smsDelaysDelivery = JSONObject.parseObject(smsDelaysDeliveryReq,SmsDelaysDelivery.class);
            return smsDelaysDeliveryService.supplierAppeal(smsDelaysDelivery,files);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 供应商确认延期索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @PostMapping("supplierConfirm")
    @ApiOperation(value = "供应商确认延期索赔单", response = SmsDelaysDelivery.class)
    public R supplierConfirm(String ids){
        try{
            return smsDelaysDeliveryService.supplierConfirm(ids);
        }catch (Exception e){
            return R.error("供应商确认延期索赔单异常");

        }
    }

}
