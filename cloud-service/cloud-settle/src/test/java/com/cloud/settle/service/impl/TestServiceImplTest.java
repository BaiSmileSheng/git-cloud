package com.cloud.settle.service.impl;

import com.cloud.settle.service.IOmsTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestServiceImplTest {
    @Autowired
    private IOmsTestService omsTestService;

    @Test
    public void seataTest() {
        omsTestService.updateTest();
    }
}
