package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.UserTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.service.ISmsClaimOtherService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 其他索赔 提供者
 *
 * @author cs
 * @date 2020-06-02
 */
@RestController
@RequestMapping("claimOther")
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
    public SmsClaimOther get(Long id) {
        return smsClaimOtherService.selectByPrimaryKey(id);

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
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsClaimOther smsClaimOther) {
        Example example = new Example(SmsClaimOther.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(smsClaimOther.getClaimCode())){
            criteria.andEqualTo("claimCode",smsClaimOther.getClaimCode());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getSupplierCode())){
            criteria.andEqualTo("supplierCode",smsClaimOther.getSupplierCode());
        }

        if(StringUtils.isNotBlank(smsClaimOther.getClaimOtherStatus())){
            criteria.andEqualTo("claimOtherStatus",smsClaimOther.getClaimOtherStatus());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime",smsClaimOther.getBeginTime());
        }
        if(StringUtils.isNotBlank(smsClaimOther.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime",smsClaimOther.getEndTime());
        }
        //供应商类型和海尔数据,如果是供应商则将供应商V码赋给供应商编号
        SysUser sysUser = getUserInfo(SysUser.class);
        Boolean flagUserType = UserTypeEnum.USER_TYPE_2.getCode().equals(sysUser.getUserType());
        if(flagUserType) {
            String supplierCode = sysUser.getSupplierCode();
            criteria.andEqualTo("supplierCode", supplierCode);
        }
        startPage();
        List<SmsClaimOther> smsClaimOtherList = smsClaimOtherService.selectByExample(example);
        return getDataTable(smsClaimOtherList);
    }

    /**
     * 新增其他索赔信息(包含文件信息)
     * @param files
     * @return
     */
    @PostMapping("save")
    @ApiOperation(value = "新增其他索赔信息(包含文件信息)", response = R.class)
    public R addSave(@RequestParam("smsClaimOther") String smsClaimOtherReq,@RequestParam("files") MultipartFile[] files) {
        SmsClaimOther smsClaimOther = JSONObject.parseObject(smsClaimOtherReq,SmsClaimOther.class);
        R result = smsClaimOtherService.insertClaimOtherAndOss(smsClaimOther,files);
        return result;
    }

    /**
     * 修改保存其他索赔(包含图片信息)
     * @param smsClaimOtherReq  其他索赔信息
     * @param files  文件
     * @return 修改成功或失败
     */
    @PostMapping("updateClaimOther")
    @ApiOperation(value = "修改保存其他索赔(包含图片信息)", response = R.class)
    public R updateClaimOtherAndOss(@RequestParam("smsClaimOther") String smsClaimOtherReq,@RequestParam("files") MultipartFile[] files) {
        SmsClaimOther smsClaimOther = JSONObject.parseObject(smsClaimOtherReq,SmsClaimOther.class);
        R result = smsClaimOtherService.updateClaimOtherAndOss(smsClaimOther,files);
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
    @PostMapping("submit")
    @OperLog(title = "提交其他索赔单", businessType = BusinessType.DELETE)
    @ApiOperation(value = "提交其他索赔单", response = R.class)
    public R submit(String ids){
        return smsClaimOtherService.submit(ids);
    }

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @PostMapping("supplierConfirm")
    @OperLog(title = "供应商确认索赔单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "供应商确认索赔单 ", response = R.class)
    public R supplierConfirm(String ids){
        return smsClaimOtherService.supplierConfirm(ids);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsClaimOtherReq 其他索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    @PostMapping("supplierAppeal")
    @ApiOperation(value = "索赔单供应商申诉 ", response = R.class)
    public R supplierAppeal(@RequestParam("smsClaimOther") String smsClaimOtherReq,@RequestParam("files") MultipartFile[] files) {
        SmsClaimOther smsClaimOther = JSONObject.parseObject(smsClaimOtherReq,SmsClaimOther.class);
        return smsClaimOtherService.supplierAppeal(smsClaimOther,files);
    }
}
