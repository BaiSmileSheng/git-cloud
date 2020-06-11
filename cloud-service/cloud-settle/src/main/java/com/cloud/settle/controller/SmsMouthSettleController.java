package com.cloud.settle.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.service.ISmsClaimCashDetailService;
import com.cloud.settle.service.ISmsInvoiceInfoService;
import com.cloud.settle.service.ISmsMouthSettleService;
import com.cloud.settle.service.ISmsSettleInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 月度结算信息  提供者
 *
 * @author cs
 * @date 2020-06-04
 */
@RestController
@RequestMapping("mouthSettle")
@Api(tags = "月度结算")
public class SmsMouthSettleController extends BaseController {

    @Autowired
    private ISmsMouthSettleService smsMouthSettleService;
    @Autowired
    private ISmsSettleInfoService smsSettleInfoService;
    @Autowired
    private ISmsClaimCashDetailService smsClaimCashDetailService;
    @Autowired
    private ISmsInvoiceInfoService smsInvoiceInfoService;



    /**
     * 查询月度结算信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询月度结算信息 ", response = SmsMouthSettle.class)
    public SmsMouthSettle get(Long id) {
        return smsMouthSettleService.selectByPrimaryKey(id);

    }

    /**
     * 查询月度结算信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "月度结算信息 查询分页", response = SmsMouthSettle.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "settleNo", value = "结算单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dataMoth", value = "结算月份", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsMouthSettle smsMouthSettle) {
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(smsMouthSettle);
        startPage();
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        return getDataTable(smsMouthSettleList);
    }

    /**
     * 月度结算查询详情
     */
    @GetMapping("listDetail")
    @ApiOperation(value = "月度结算查询详情")
    public R listDetail(Long id) {
        SmsMouthSettle smsMouthSettle = smsMouthSettleService.selectByPrimaryKey(id);
        if (smsMouthSettle == null) {
            return R.error("月度结算信息不存在！");
        }
        Dict dict = Dict.create()
                .set("smsMouthSettle",smsMouthSettle);
        //加工费明细
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");
        Example exampleSettle = new Example(SmsSettleInfo.class);
        Example.Criteria criteriaSettle = exampleSettle.createCriteria();
        criteriaSettle.andEqualTo("supplierCode", smsMouthSettle.getSupplierCode())
                .andEqualTo("companyCode", smsMouthSettle.getCompanyCode())
                .andCondition("DATE_FORMAT(product_start_date, '%Y%m')='"+lastMonth+"'");
        List<SmsSettleInfo> settleInfos=smsSettleInfoService.selectByExample(exampleSettle);
        dict.put("settleInfos", settleInfos);

        //索赔明细
        List<Map<String, BigDecimal>> mapActual = smsClaimCashDetailService.selectSumCashGroupByClaimTypeActual(smsMouthSettle.getSettleNo());
        List<Map<String, BigDecimal>> mapHistory = smsClaimCashDetailService.selectSumCashGroupByClaimTypeHistory(smsMouthSettle.getSettleNo());

        dict.put("mapActual",mapActual);
        dict.put("mapHistory",mapHistory);

        List<SmsInvoiceInfo> smsInvoiceInfoList=smsInvoiceInfoService.selectByMouthSettleId(smsMouthSettle.getSettleNo());
        dict.put("invoices",smsInvoiceInfoList);
        return R.data(dict);

    }

    /**
     * 修改保存月度结算信息
     */
    @GetMapping("confirm")
    @ApiOperation(value = "内控、小微主确认 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "settleStatus", value = "状态：12、内控确认 13、小微主确认", required = true,paramType = "query", dataType = "String")
    })
    public R confirm(Long id,String settleStatus) {
        return smsMouthSettleService.confirm(id,settleStatus);
    }

    /**
     * 新增保存月度结算信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存月度结算信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存月度结算信息 ", response = R.class)
    public R addSave(@RequestBody SmsMouthSettle smsMouthSettle) {
        smsMouthSettleService.insertSelective(smsMouthSettle);
        return R.data(smsMouthSettle.getId());
    }

    /**
     * 修改保存月度结算信息
     */
    @PostMapping("update")
    @OperLog(title = "修改保存月度结算信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存月度结算信息 ", response = R.class)
    public R editSave(@RequestBody SmsMouthSettle smsMouthSettle) {
        return toAjax(smsMouthSettleService.updateByPrimaryKeySelective(smsMouthSettle));
    }

    /**
     * 删除月度结算信息
     */
    @PostMapping("remove")
    @OperLog(title = "删除月度结算信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除月度结算信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsMouthSettleService.deleteByIds(ids));
    }

    /**
     * 月度结算定时任务
     * 这是一个大工程
     * @return
     */
    @PostMapping("countMonthSettle")
    @ApiOperation(value = "月度结算定时任务", response = R.class)
    public R countMonthSettle(){
        return smsMouthSettleService.countMonthSettle();
    }

    /**
     * 查询月度结算信息导出 列表
     */
    @GetMapping("export")
    @ApiOperation(value = "月度结算信息 导出", response = SmsMouthSettle.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "settleNo", value = "结算单号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dataMoth", value = "结算月份", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false,paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore() SmsMouthSettle smsMouthSettle) {
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(smsMouthSettle);
        startPage();
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        return EasyExcelUtil.writeExcel(smsMouthSettleList, "月度结算.xlsx", "sheet", new SmsMouthSettle());
    }
}
