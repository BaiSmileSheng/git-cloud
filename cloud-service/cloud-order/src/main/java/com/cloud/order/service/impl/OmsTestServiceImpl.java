package com.cloud.order.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsTest;
import com.cloud.order.mapper.OmsTestMapper;
import com.cloud.order.service.IOmsTestService;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.RemoteCdSapSalePriceInfoService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单类测试Service业务层处理
 *
 * @author cloud
 * @date 2020-05-02
 */
@Service
@Slf4j
public class OmsTestServiceImpl extends BaseServiceImpl<OmsTest> implements IOmsTestService {
    @Autowired
    private OmsTestMapper omsTestMapper;
    @Autowired
    private RemoteCdSapSalePriceInfoService remoteCdSapSalePriceInfoService;

    @Override
    @GlobalTransactional
    public void updateTest() {
        log.info("----------------------" + RootContext.getXID());
        //1. 更新oms_test表
        int count = omsTestMapper.updateByPrimaryKey(OmsTest.builder()
                .id(1L)
                .testA("李四")
                .testB(2L)
                .build());
        System.out.println("count = " + count);
        CdSapSalePrice cdSapSalePrice = new CdSapSalePrice().builder()
                .conditionsType("PR02").marketingOrganization("8410")
                .materialCode("test").beginDate(new Date()).endDate(new Date())
                .pricingRecordNo("1111").salePrice("123").build();

        remoteCdSapSalePriceInfoService.addSave(cdSapSalePrice);
        System.out.println("count = " + count);
        log.error("OmsTestServiceImpl_updateTest_cdSapSalePrice:{}", cdSapSalePrice);
        throw new BusinessException("测试seata");
    }
}
