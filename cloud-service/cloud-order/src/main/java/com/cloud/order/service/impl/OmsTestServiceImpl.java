package com.cloud.order.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.SysOperLog;
import com.cloud.system.feign.RemoteLogService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsTestMapper;
import com.cloud.order.domain.entity.OmsTest;
import com.cloud.order.service.IOmsTestService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

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
    private RemoteLogService logService;


    @GlobalTransactional
    @Override
    public void updateTest() {
        log.info("----------------------" + RootContext.getXID());
        //1. 更新oms_test表
        int count = omsTestMapper.updateByPrimaryKey(OmsTest.builder()
                .id(1L)
                .testA("李四")
                .testB(2L)
                .build());
        System.out.println("count = " + count);
        //2. 增加一条操作日志
        R r = logService.insertOperlog(SysOperLog.builder()
                .operId(1111L)
                .title("测试1").build());
        if (!r.isSuccess()) {
            throw new BusinessException("111");
        }
        System.out.println("count = " + count);

    }
}
