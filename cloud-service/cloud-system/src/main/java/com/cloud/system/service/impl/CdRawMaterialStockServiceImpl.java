package com.cloud.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.mapper.CdRawMaterialStockMapper;
import com.cloud.system.service.ICdRawMaterialStockService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Type;
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

    @Autowired
    private SystemFromSap601InterfaceService systemFromSap601InterfaceService;

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
    /**
     * Description:  根据List<CdRawMaterialStock>查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    public R selectByList(List<CdRawMaterialStock> list) {
        List<CdRawMaterialStock> cdRawMaterialStocks = cdRawMaterialStockMapper.selectByList(list);
        if (ObjectUtil.isEmpty(cdRawMaterialStocks) && cdRawMaterialStocks.size() <= 0) {
            cdRawMaterialStocks.add(CdRawMaterialStock.builder().build());
            return R.data(cdRawMaterialStocks);
        }
        return R.data(cdRawMaterialStockMapper.selectByList(list));
    }

    /**
     * 实时获取原材料库存信息
     * @param list
     * @param sysUser
     * @return
     */
    @Transactional
    @Override
    public R currentqueryRawMaterialStock(List<CdRawMaterialStock> list, SysUser sysUser) {
        R r = systemFromSap601InterfaceService.currentqueryRawMaterialStockFromSap601(list,sysUser);
        if(!r.isSuccess()){
            return r;
        }
        List<CdRawMaterialStock> listInsert = r.getCollectData(new TypeReference<List<CdRawMaterialStock>>() {});
        if(CollectionUtils.isEmpty(listInsert)){
            return R.error("在SAP没有查到数据");
        }
        listInsert.forEach(cdRawMaterialStock ->{
            Example example = new Example(CdRawMaterialStock.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("rawMaterialCode",cdRawMaterialStock.getRawMaterialCode());
            criteria.andEqualTo("productFactoryCode",cdRawMaterialStock.getProductFactoryCode());
            cdRawMaterialStockMapper.updateByExampleSelective(cdRawMaterialStock,example);
        });
        return R.ok();
    }
}
