package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.mapper.CdRawMaterialStockMapper;
import com.cloud.system.service.ICdRawMaterialStockService;
import com.cloud.system.util.EasyExcelUtilOSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 原材料库存 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-05
 */
@Service
public class CdRawMaterialStockServiceImpl extends BaseServiceImpl<CdRawMaterialStock> implements ICdRawMaterialStockService {
    @Autowired
    private CdRawMaterialStockMapper cdRawMaterialStockMapper;

    /**
     * @Description: 导出原材料库存报表
     * @Param: [cdRawMaterialStock]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/9
     */
    @Override
    public R exportRawMaterialExcel(CdRawMaterialStock cdRawMaterialStock) {
        List<CdRawMaterialStock> cdRawMaterialStocks = cdRawMaterialStockMapper.select(cdRawMaterialStock);
        return EasyExcelUtilOSS.writeExcel(cdRawMaterialStocks, "原材料库存报表.xlsx", "sheet", new CdRawMaterialStock());
    }

    /**
     * 删除全部数据
     * @return
     */
    @Override
    public R deleteAll() {
        cdRawMaterialStockMapper.deleteAll();
        return R.ok();
    }
}
