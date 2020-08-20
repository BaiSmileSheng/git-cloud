package com.cloud.settle.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.bean.BeanUtils;
import com.cloud.settle.domain.entity.vo.SmsClaimCashDetailVo;
import com.cloud.settle.domain.entity.SmsClaimCashDetail;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.MonthSettleStatusEnum;
import com.cloud.settle.service.ISmsClaimCashDetailService;
import com.cloud.settle.service.ISmsInvoiceInfoService;
import com.cloud.settle.service.ISmsMouthSettleService;
import com.cloud.settle.service.ISmsSettleInfoService;
import com.cloud.settle.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.SettleRatioEnum;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "companyCode", value = "付款公司", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "kmsNo", value = "KEMS单据号", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsMouthSettle smsMouthSettle) {
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        BeanUtils.nullifyStrings(smsMouthSettle);
        criteria.andEqualTo(smsMouthSettle);
        SysUser sysUser = getUserInfo(SysUser.class);
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //海尔内部
                 if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)) {
                    //小微主查看内控确认数据
                     List<String> statusXWZ = CollectionUtil.newArrayList(MonthSettleStatusEnum.YD_SETTLE_STATUS_XWZDQR.getCode(),
                             MonthSettleStatusEnum.YD_SETTLE_STATUS_DFK.getCode(),
                             MonthSettleStatusEnum.YD_SETTLE_STATUS_JSWC.getCode());
                    criteria.andIn("settleStatus", statusXWZ);
                }
            }
        }
        startPage();
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        return getDataTable(smsMouthSettleList);
    }

    /**
     * 月度结算查询详情
     */
    @GetMapping("listDetail")
    @ApiOperation(value = "月度结算查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "月度结算id", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:mouthSettle:listDetail")
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
        startPage();
        List<SmsSettleInfo> settleInfos=smsSettleInfoService.selectByExample(exampleSettle);
        dict.put("settleInfos", settleInfos);

        //索赔明细
        Map<String, SmsClaimCashDetail> mapActual = smsClaimCashDetailService.selectSumCashGroupByClaimTypeActual(smsMouthSettle.getSettleNo());
        Map<String, SmsClaimCashDetail> mapHistory = smsClaimCashDetailService.selectSumCashGroupByClaimTypeHistory(smsMouthSettle.getSettleNo());

        List<SmsClaimCashDetailVo> list = new ArrayList<>();
        List<String> typeList = CollUtil.newArrayList(SettleRatioEnum.SPLX_BF.getCode()
        ,SettleRatioEnum.SPLX_WH.getCode(),SettleRatioEnum.SPLX_YQ.getCode()
        ,SettleRatioEnum.SPLX_ZL.getCode(),SettleRatioEnum.SPLX_QT.getCode());
        typeList.forEach(type->{
            SmsClaimCashDetailVo dto = new SmsClaimCashDetailVo();
            dto.setClaimType(type);
            if (MapUtil.isNotEmpty(mapActual)&&mapActual.get(type) != null) {
                dto.setActualCashAmount(mapActual.get(type).getCashAmount());
            }else {
                dto.setActualCashAmount(BigDecimal.ZERO);
            }
            if (MapUtil.isNotEmpty(mapHistory)&&mapHistory.get(type) != null) {
                dto.setHistoryCashAmount(mapHistory.get(type).getCashAmount());
            }else {
                dto.setHistoryCashAmount(BigDecimal.ZERO);
            }
            list.add(dto);
        });
        dict.put("mapAmount",list);
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
    @HasPermissions("settle:mouthSettle:xwzConfirm,settle:mouthSettle:nkConfirm")
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
    @HasPermissions("settle:mouthSettle:export")
    public R export(@ApiIgnore() SmsMouthSettle smsMouthSettle) {

        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        BeanUtils.nullifyStrings(smsMouthSettle);
        criteria.andEqualTo(smsMouthSettle);
        SysUser sysUser = getUserInfo(SysUser.class);
        if (!sysUser.isAdmin()) {
            if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
                //供应商查询自己工厂下的申请单
                criteria.andEqualTo("supplierCode", sysUser.getSupplierCode());
            } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //海尔内部
                if (sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)) {
                    //小微主查看内控确认数据
                    List<String> statusXWZ = CollectionUtil.newArrayList(MonthSettleStatusEnum.YD_SETTLE_STATUS_XWZDQR.getCode(),
                            MonthSettleStatusEnum.YD_SETTLE_STATUS_DFK.getCode(),
                            MonthSettleStatusEnum.YD_SETTLE_STATUS_JSWC.getCode());
                    criteria.andIn("settleStatus", statusXWZ);
                }
            }
        }
        startPage();
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(smsMouthSettleList, "月度结算.xlsx", "sheet", new SmsMouthSettle());
    }

    /**
     * 打印结算
     */
    @GetMapping("settlePrint")
    @ApiOperation(value = "打印结算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "settleNo", value = "结算单号", required = false,paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:mouthSettle:settlePrint")
    public R settlePrint(String settleNo) {
        return smsMouthSettleService.settlePrint(settleNo);
    }

    /**
     * 打印索赔单
     */
    @GetMapping("spPrint")
    @ApiOperation(value = "打印索赔单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "settleNo", value = "结算单号", required = false,paramType = "query", dataType = "String")
    })
    @HasPermissions("settle:mouthSettle:spPrint")
    public R spPrint(String settleNo) {
        return smsMouthSettleService.spPrint(settleNo);
    }
}
