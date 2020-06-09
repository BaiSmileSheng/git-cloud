package com.cloud.settle.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.mapper.SmsScrapOrderMapper;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.RemoteCdSapSalePriceInfoService;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.cloud.system.feign.RemoteSequeceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报废申请 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Slf4j
@Service
public class SmsScrapOrderServiceImpl extends BaseServiceImpl<SmsScrapOrder> implements ISmsScrapOrderService {
    @Autowired
    private SmsScrapOrderMapper smsScrapOrderMapper;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private RemoteFactoryLineInfoService remotefactoryLineInfoService;
    @Autowired
    private RemoteCdSapSalePriceInfoService remoteCdSapSalePriceInfoService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;

    /**
     * 编辑报废申请单功能  --有状态校验
     * @param smsScrapOrder
     * @return
     */
    @Override
    public R editSave(SmsScrapOrder smsScrapOrder) {
        Long id = smsScrapOrder.getId();
        log.info(StrUtil.format("报废申请修改保存开始：参数为{}", smsScrapOrder.toString()));
        //校验状态是否是未提交
        R rCheckStatus = checkCondition(id);
        SmsScrapOrder smsScrapOrderCheck = (SmsScrapOrder) rCheckStatus.get("data");
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,smsScrapOrderCheck.getProductOrderCode());
        if (!rCheck.isSuccess()) {
            return rCheck;
        }
        int rows = updateByPrimaryKeySelective(smsScrapOrder);
        return rows > 0 ? R.ok() : R.error("更新错误！");
    }

    /**
     * 新增保存报废申请
     * @param smsScrapOrder
     * @return
     */
    @Override
    public R addSave(SmsScrapOrder smsScrapOrder) {
        log.info(StrUtil.format("报废申请新增保存开始：参数为{}", smsScrapOrder.toString()));
        //生产订单号
        String productOrderCode = smsScrapOrder.getProductOrderCode();
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,productOrderCode);
        if (!rCheck.isSuccess()) {
            return rCheck;
        }

        String seq = remoteSequeceService.selectSeq("scrap_seq", 4);
        StringBuffer scrapNo = new StringBuffer();
        //WH+年月日+4位顺序号
        scrapNo.append("BF").append(DateUtils.dateTime()).append(seq);
        smsScrapOrder.setScrapNo(scrapNo.toString());
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        smsScrapOrder.setMachiningPrice(omsProductionOrder.getProcessCost());
        //根据线体号查询供应商编码
        CdFactoryLineInfo factoryLineInfo = remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode());
        if (factoryLineInfo != null) {
            smsScrapOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
            smsScrapOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        }
        smsScrapOrder.setFactoryCode(omsProductionOrder.getFactoryCode());
        CdFactoryInfo cdFactoryInfo = remoteFactoryInfoService.selectOneByFactory(omsProductionOrder.getFactoryCode());
        if (cdFactoryInfo == null) {
            log.error(StrUtil.format("(报废)报废申请新增保存开始：公司信息为空参数为{}", omsProductionOrder.getFactoryCode()));
            return R.error("公司信息为空！");
        }
        smsScrapOrder.setCompanyCode(cdFactoryInfo.getCompanyCode());
        if (StrUtil.isBlank(smsScrapOrder.getScrapStatus())) {
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode());
        }
        //根据物料号  有效期查询成品销售价格
        String date = DateUtils.getTime();
        List<CdSapSalePrice> sapSalePrices = remoteCdSapSalePriceInfoService.findByMaterialCode(smsScrapOrder.getProductMaterialCode(),date,date);
        if (sapSalePrices == null || sapSalePrices.size() == 0) {
            log.error(StrUtil.format("(报废)报废申请修改保存开始：物料销售价格未维护参数为{}", smsScrapOrder.getProductMaterialCode()));
            return R.error("物料销售价格未维护！");
        }
        CdSapSalePrice cdSapSalePrice = sapSalePrices.get(0);
        smsScrapOrder.setCurrency(cdSapSalePrice.getConditionsMonetary());
        smsScrapOrder.setMaterialPrice(new BigDecimal(cdSapSalePrice.getSalePrice()));
        smsScrapOrder.setDelFlag("0");
//        smsScrapOrder.setCreateBy(sysUser.getLoginName());
        smsScrapOrder.setCreateTime(DateUtils.getNowDate());
        int rows=insertSelective(smsScrapOrder);
        if (rows > 0) {
            return R.data(smsScrapOrder.getId());
        }else{
            return R.error("报废申请插入失败！");
        }
    }

    /**
     * 1、校验物料号是否同步了sap价格
     * @param smsScrapOrder
     * @param productOrderCode
     * @return
     */
    R checkScrapOrderCondition(SmsScrapOrder smsScrapOrder,String productOrderCode) {
        if (smsScrapOrder.getScrapAmount() == null) {
            return R.error("报废数量为空！");
        }
        if (productOrderCode == null) {
            return R.error("生产订单号为空！");
        }
        int applyNum = smsScrapOrder.getScrapAmount();//申请量
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if (omsProductionOrder == null) {
            return R.error("订单信息不存在！");
        }
        //5、校验申请量是否大于订单量
        BigDecimal productNum = omsProductionOrder.getProductNum();
        if (new BigDecimal(applyNum).compareTo(productNum) > 0) {
            return R.error("申请量不得大于订单量");
        }
        return R.ok();
    }

    /**
     * 删除报废申请
     * @param ids
     * @return
     */
    @Override
    public R remove(String ids) {
        log.info(StrUtil.format("报废申请删除开始：id为{}", ids));
        if(StringUtils.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        int rows = deleteByIds(ids);
        return rows > 0 ? R.ok() : R.error("删除错误！");
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
        SmsScrapOrder smsScrapOrder = selectByPrimaryKey(id);
        if (smsScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsScrapOrder);
    }

    /**
     * 根据月份和状态查询
     * @param month
     * @param scrapStatus
     * @return
     */
    @Override
    public List<SmsScrapOrder> selectByMonthAndStatus(String month, List<String> scrapStatus) {
        return smsScrapOrderMapper.selectByMonthAndStatus(month,scrapStatus);
    }
}
