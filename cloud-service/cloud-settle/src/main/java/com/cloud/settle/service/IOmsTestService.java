package com.cloud.settle.service;


import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsTest;

/**
 * 订单类测试Service接口
 *
 * @author cloud
 * @date 2020-05-02
 */
public interface IOmsTestService extends BaseService<OmsTest> {

    public void updateTest();
}
