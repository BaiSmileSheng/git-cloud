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
        //TODO 区分供应商类型和海尔数据

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
    //@OperLog(title = "新增保存质量索赔 ", businessType = BusinessType.INSERT)
    //TODO files加日志时参数转换有问题
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
    //@OperLog(title = "修改保存质量索赔 ", businessType = BusinessType.UPDATE)
    //TODO
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
    //@OperLog(title = "删除质量索赔 ", businessType = BusinessType.DELETE)
    //TODO class com.cloud.system.domain.entity.SysOperLog is not a type supported by this encoder
    @ApiOperation(value = "删除质量索赔 ", response = SmsQualityOrder.class)
    public R remove(String ids) {
        try{
            List<SmsQualityOrder> selectListResult =  smsQualityOrderService.selectListById(ids);
            if(CollectionUtils.isEmpty(selectListResult)){
                return R.error("索赔单不存在");
            }
            for(SmsQualityOrder smsQualityOrder : selectListResult){
                Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
                if(!flagResult){
                    return R.error("请确认索赔单状态是否为待提交");
                }
            }
            return smsQualityOrderService.deleteSmsQualityOrderAndSysOss(ids,selectListResult);
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
            List<SmsQualityOrder> selectListResult =  smsQualityOrderService.selectListById(ids);
            for(SmsQualityOrder smsQualityOrder : selectListResult){
                Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
                if(!flagResult){
                    return R.error("请确认索赔单状态是否为待提交");
                }
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_1.getCode());
            }
            int count =  smsQualityOrderService.updateBatchByPrimaryKeySelective(selectListResult);
            return toAjax(count);
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
            List<SmsQualityOrder> selectListResult =  smsQualityOrderService.selectListById(ids);
            for(SmsQualityOrder smsQualityOrder : selectListResult){
                Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus())
                        ||QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrder.getQualityStatus());
                if(!flagResult){
                    return R.error("请确认索赔单状态是否为待供应商确认");
                }
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_2.getCode());
            }
            int count = smsQualityOrderService.updateBatchByPrimaryKeySelective(selectListResult);
            return toAjax(count);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

}
