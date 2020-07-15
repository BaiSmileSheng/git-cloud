package com.cloud.settle.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.settle.domain.entity.vo.SmsInvoiceInfoSVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.settle.service.ISmsInvoiceInfoService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 发票信息  提供者
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@RestController
@RequestMapping("invoiceInfo")
@Api(tags = "发票信息  提供者")
public class SmsInvoiceInfoController extends BaseController {

    @Autowired
    private ISmsInvoiceInfoService smsInvoiceInfoService;

    /**
     * 查询发票信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询发票信息 ", response = SmsInvoiceInfo.class)
    public SmsInvoiceInfo get(Long id) {
        return smsInvoiceInfoService.selectByPrimaryKey(id);

    }

    /**
     * 根据月度结算单号查询发票信息
     */
    @GetMapping("selectByMouthSettleId")
    @ApiOperation(value = "根据月度结算单号查询发票信息 ", response = SmsInvoiceInfo.class)
    public List<SmsInvoiceInfo> selectByMouthSettleId(@RequestParam("mouthSettleId") String mouthSettleId) {
        Example example = new Example(SmsInvoiceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mouthSettleId",mouthSettleId);
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfoService.selectByExample(example);
        return smsInvoiceInfoList;

    }

    /**
     * 查询发票信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "发票信息 查询分页", response = SmsInvoiceInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsInvoiceInfo smsInvoiceInfo) {
        Example example = new Example(SmsInvoiceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfoService.selectByExample(example);
        return getDataTable(smsInvoiceInfoList);
    }


    /**
     * 新增保存发票信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存发票信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存发票信息 ", response = R.class)
    public R addSave(@RequestBody SmsInvoiceInfo smsInvoiceInfo) {
        //校验入参
        ValidatorUtils.validateEntity(smsInvoiceInfo);
        smsInvoiceInfoService.insertSelective(smsInvoiceInfo);
        return R.data(smsInvoiceInfo.getId());
    }

    /**
     * 批量新增或修改保存发票信息
     * @param smsInvoiceInfoS 发票信息集合
     */
    @HasPermissions("settle:invoiceInfo:batchAddSaveOrUpdate")
    @PostMapping("batchAddSaveOrUpdate")
    @OperLog(title = "批量新增或修改保存发票信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "批量新增或修改保存发票信息 ", response = R.class)
    public R batchAddSaveOrUpdate(@RequestBody SmsInvoiceInfoSVo smsInvoiceInfoS) {
        if(null == smsInvoiceInfoS || StringUtils.isBlank(smsInvoiceInfoS.getMouthSettleId())){
            return R.error("批量新增发票信息入参为空");
        }
        return smsInvoiceInfoService.batchAddSaveOrUpdate(smsInvoiceInfoS);
    }

    /**
     * 修改保存发票信息
     */
    @HasPermissions("settle:invoiceInfo:update")
    @PostMapping("update")
    @OperLog(title = "修改保存发票信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存发票信息 ", response = R.class)
    public R editSave(@RequestBody SmsInvoiceInfo smsInvoiceInfo) {
        return toAjax(smsInvoiceInfoService.updateByPrimaryKeySelective(smsInvoiceInfo));
    }

    /**
     * 删除发票信息
     */
    @HasPermissions("settle:invoiceInfo:remove")
    @PostMapping("remove")
    @OperLog(title = "删除发票信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除发票信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsInvoiceInfoService.deleteByIds(ids));
    }

}
