package com.cloud.settle.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.annotation.LoginUser;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 报废申请  提供者
 *
 * @author cs
 * @date 2020-05-29
 */
@RestController
@RequestMapping("scrapOrder")
@Api(tags = "报废申请单")
public class SmsScrapOrderController extends BaseController {

    @Autowired
    private ISmsScrapOrderService smsScrapOrderService;


    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    /**
     * 查询报废申请
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询报废申请 ", response = SmsScrapOrder.class)
    public SmsScrapOrder get(Long id) {
        return smsScrapOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询报废申请 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "报废申请 查询分页", response = SmsScrapOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapNo", value = "报废单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "供应商编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scrapStatus", value = "报废状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "创建日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "到", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() SmsScrapOrder smsScrapOrder,@ApiIgnore() @LoginUser SysUser sysUser) {
        Example example = new Example(SmsScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(smsScrapOrder);
        if(StrUtil.isNotEmpty(smsScrapOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("createTime", smsScrapOrder.getEndTime());
        }
        if(StrUtil.isNotEmpty(smsScrapOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("createTime", smsScrapOrder.getBeginTime());
        }
        startPage();
        List<SmsScrapOrder> smsScrapOrderList = smsScrapOrderService.selectByExample(example);
        return getDataTable(smsScrapOrderList);
    }


    /**
     * 新增保存报废申请
     */
    @PostMapping("save")
    @OperLog(title = "新增保存报废申请 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存报废申请 ", response = R.class)
    public R addSave(@RequestBody SmsScrapOrder smsScrapOrder) {
        smsScrapOrderService.insertSelective(smsScrapOrder);
        return R.data(smsScrapOrder.getId());
    }

    /**
     * 修改保存报废申请
     */
    @PostMapping("update")
    public R update(@RequestBody SmsScrapOrder smsScrapOrder) {
        return toAjax(smsScrapOrderService.updateByPrimaryKeySelective(smsScrapOrder));
    }

    /**
     * 编辑物耗申请单功能  --有状态校验
     */
    @PostMapping("editSave")
    @OperLog(title = "修改保存报废申请 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存报废申请 ", response = R.class)
    public R editSave(@RequestBody SmsScrapOrder smsScrapOrder) {
        Long id = smsScrapOrder.getId();
        //判断状态是否是未提交
        R rCheck=checkCondition(id);
        SmsScrapOrder smsScrapOrderCheck = (SmsScrapOrder) rCheck.get("data");
        //校验物料号是否同步了sap价格
        R r=remoteCdMaterialPriceInfoService.checkSynchroSAP(smsScrapOrderCheck.getProductMaterialCode());
        if(r.isSuccess()){
            return toAjax(smsScrapOrderService.updateByPrimaryKeySelective(smsScrapOrder));
        }else {
            return r;
        }

    }

    /**
     * 删除报废申请
     */
    @PostMapping("remove")
    @OperLog(title = "删除报废申请 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除报废申请 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        if(StringUtils.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        return toAjax(smsScrapOrderService.deleteByIds(ids));
    }


    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return 返回SmsScrapOrder信息
     */
    public R checkCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsScrapOrder smsScrapOrder = smsScrapOrderService.selectByPrimaryKey(id);
        if (smsScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsScrapOrder);
    }
}
