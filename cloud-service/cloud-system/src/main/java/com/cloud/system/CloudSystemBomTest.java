package com.cloud.system;

import com.cloud.common.core.domain.R;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import com.cloud.system.service.ICdProductStockService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 启动程序
 *
 * @author cloud
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudSystemBomTest{

    @Autowired
    private SystemFromSap601InterfaceService systemFromSap601InterfaceService;

    @Test
    public void  bomTest(){
        R result = systemFromSap601InterfaceService.sycBomInfo();
//        R result = systemFromSap601InterfaceService.queryBomInfoFromSap601(Arrays.asList("8310"),Arrays.asList("0061800246B"));
    }

    @Test
    public void  sycRawMaterialStockTest(){
        R result = systemFromSap601InterfaceService.sycRawMaterialStock();
    }

    @Autowired
    private ICdMaterialPriceInfoService cdMaterialPriceInfoService;

    @Test
    public void synPriceJGF(){
        cdMaterialPriceInfoService.synPriceJGF();
    }

    @Autowired
    private ICdProductStockService cdProductStockService;

    @Test
    public void timeSycProductStock(){
        cdProductStockService.timeSycProductStock();
    }
}
