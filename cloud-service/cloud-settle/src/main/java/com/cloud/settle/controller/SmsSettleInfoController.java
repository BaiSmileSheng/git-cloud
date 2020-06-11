package com.cloud.settle.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.TimeTypeEnum;
import com.cloud.settle.service.ISmsSettleInfoService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 加工费结算  提供者
 *
 * @author Lihongcxia
 * @date 2020-05-26
 */
@RestController
@RequestMapping("smsSettleInfo")
@Api(tags = "加工费结算  提供者")
public class SmsSettleInfoController extends BaseController {

    @Autowired
    private ISmsSettleInfoService smsSettleInfoService;

    /**
     * 查询加工费结算
     * @param id 主键id
     * @return SmsSettleInfo 加工费结算信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询加工费结算", response = SmsSettleInfo.class)
    public SmsSettleInfo get(Long id) {
        return smsSettleInfoService.selectByPrimaryKey(id);

    }

    /**
     * 分页查询加工费结算 列表
     * @param smsSettleInfo 加工费结算信息
     * @return TableDataInfo 加工费结算分页列表
     */
    @GetMapping("list")
    @ApiOperation(value = "加工费结算查询分页", response = SmsSettleInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsSettleInfo smsSettleInfo) {
        startPage();
        Example example = exampleListCondition(smsSettleInfo);

        List<SmsSettleInfo> smsSettleInfoList = smsSettleInfoService.selectByExample(example);
        return getDataTable(smsSettleInfoList);
    }

    /**
     * 查询加工费结算 列表
     * @param smsSettleInfo 加工费结算信息
     * @return List<SmsSettleInfo> 加工费结算列表
     */
    @GetMapping("listByCondition")
    @ApiOperation(value = "加工费结算条件查询", response = SmsSettleInfo.class)
    public List<SmsSettleInfo> listByCondition(SmsSettleInfo smsSettleInfo) {
        startPage();
        Example example = exampleListCondition(smsSettleInfo);
        List<SmsSettleInfo> smsSettleInfoList = smsSettleInfoService.selectByExample(example);
        return smsSettleInfoList;
    }

    /**
     * 计算加工费(定时任务调用)
     * @return 成功或失败
     */
    @PostMapping(value = "smsSettleInfoCalculate")
    @ApiOperation(value = "计算加工费(定时任务调用)", response = SmsSettleInfo.class)
    public R smsSettleInfoCalculate() {
        R result = smsSettleInfoService.smsSettleInfoCalculate();
        return result;
    }
    /**
     * 组装查询条件 加工费结算信息
     * @param smsSettleInfo 加工费信息
     * @return 组装查询条件
     */
    private Example exampleListCondition(SmsSettleInfo smsSettleInfo){
        Example example = new Example(SmsSettleInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(smsSettleInfo.getLineNo())){
            criteria.andEqualTo("lineNo",smsSettleInfo.getLineNo());
        }
        if(StringUtils.isNotBlank(smsSettleInfo.getSupplierCode())){
            criteria.andEqualTo("supplierCode",smsSettleInfo.getSupplierCode());
        }
        if(StringUtils.isNotBlank(smsSettleInfo.getProductOrderCode())){
            criteria.andEqualTo("productOrderCode",smsSettleInfo.getProductOrderCode());
        }
        if(StringUtils.isNotBlank(smsSettleInfo.getOrderStatus())){
            criteria.andEqualTo("orderStatus",smsSettleInfo.getOrderStatus());
        }
        if(TimeTypeEnum.BASIC_BEGIN_TIME_TYPE.getCode().equals(smsSettleInfo.getTimeType())){
            if(StringUtils.isNotBlank(smsSettleInfo.getBeginTime())){
                criteria.andGreaterThanOrEqualTo("produceStartDate",smsSettleInfo.getBeginTime());
            }
            if(StringUtils.isNotBlank(smsSettleInfo.getEndTime())){
                criteria.andLessThanOrEqualTo("produceStartDate",smsSettleInfo.getEndTime());
            }
        }
        if(TimeTypeEnum.BASIC_END_TIME_TYPE.getCode().equals(smsSettleInfo.getTimeType())){
            if(StringUtils.isNotBlank(smsSettleInfo.getBeginTime())){
                criteria.andGreaterThanOrEqualTo("produceEndDate",smsSettleInfo.getBeginTime());
            }
            if(StringUtils.isNotBlank(smsSettleInfo.getEndTime())){
                criteria.andLessThanOrEqualTo("produceEndDate",smsSettleInfo.getEndTime());
            }
        }

        if(TimeTypeEnum.ACTUAL_END_TIME_TYPE.getCode().equals(smsSettleInfo.getTimeType())){
            if(StringUtils.isNotBlank(smsSettleInfo.getBeginTime())){
                criteria.andGreaterThanOrEqualTo("actualEndDate",smsSettleInfo.getBeginTime());
            }
            if(StringUtils.isNotBlank(smsSettleInfo.getEndTime())){
                criteria.andLessThanOrEqualTo("actualEndDate",smsSettleInfo.getEndTime());
            }
        }

        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if(flagUserType){
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode",supplierCode);
        }
        return example;
    }

    /**
     * 新增保存加工费结算
     * @param smsSettleInfo 加工费结算信息
     * @return R 新增成功或失败
     */
    @PostMapping("save")
    @OperLog(title = "新增加工费结算 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存加工费结算 ", response = SmsSettleInfo.class)
    public R addSave(@RequestBody SmsSettleInfo smsSettleInfo) {
        return toAjax(smsSettleInfoService.insertUseGeneratedKeys(smsSettleInfo));
    }

    /**
     * 修改保存加工费结算
     * @param smsSettleInfo 加工费结算信息
     * @return R 修改成功或失败
     */
    @PostMapping("update")
    @OperLog(title = "修改保存加工费结算 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存加工费结算 ", response = SmsSettleInfo.class)
    public R editSave(@RequestBody SmsSettleInfo smsSettleInfo) {
        return toAjax(smsSettleInfoService.updateByPrimaryKeySelective(smsSettleInfo));
    }

    /**
     *删除加工费结算
     * @param ids 主键id
     * @return 删除成功或失败
     */
    @PostMapping("remove")
    @OperLog(title = "删除加工费结算 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除加工费结算 ", response = SmsSettleInfo.class)
    public R remove(String ids) {
        return toAjax(smsSettleInfoService.deleteByIds(ids));
    }

}
