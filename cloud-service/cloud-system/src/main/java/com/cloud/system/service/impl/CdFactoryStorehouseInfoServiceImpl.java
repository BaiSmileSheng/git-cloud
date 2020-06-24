package com.cloud.system.service.impl;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.mapper.CdFactoryStorehouseInfoMapper;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工厂库位 Service业务层处理
 *
 * @author cs
 * @date 2020-06-15
 */
@Service
public class CdFactoryStorehouseInfoServiceImpl extends BaseServiceImpl<CdFactoryStorehouseInfo> implements ICdFactoryStorehouseInfoService {
    @Autowired
    private CdFactoryStorehouseInfoMapper cdFactoryStorehouseInfoMapper;

    /**
     * 根据工厂，客户编码分组取接收库位
     *
     * @param dicts
     * @return
     */
    public R selectStorehouseToMap(List<Dict> dicts) {
        return R.data(cdFactoryStorehouseInfoMapper.selectStorehouseToMap(dicts));
    }

    /**
     * 批量新增或修改
     *
     * @param list 工厂库位信息集合
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R batchInsertOrUpdate(List<CdFactoryStorehouseInfo> list) {
        //1.根据list去重  工厂编号+客户编号做唯一值
        Map<String, CdFactoryStorehouseInfo> map = new HashMap<>();
        list.forEach(cdFactoryStorehouseInfo -> {
            String key = cdFactoryStorehouseInfo.getProductFactoryCode() + cdFactoryStorehouseInfo.getCustomerCode();
            map.put(key, cdFactoryStorehouseInfo);
        });
        List<CdFactoryStorehouseInfo> listReq = map.values().stream().collect(Collectors.toList());
        //2.批量查询存在的即修改的
        List<CdFactoryStorehouseInfo> updateList = cdFactoryStorehouseInfoMapper.batchSelectListByCondition(listReq);
        Map<String, CdFactoryStorehouseInfo> updateMap = updateList.stream().collect(Collectors.toMap(cd -> cd.getProductFactoryCode() + cd.getCustomerCode(),
                cdProductStock -> cdProductStock));
        //3.新增的集合
        List<CdFactoryStorehouseInfo> insertList = new ArrayList<>();
        map.keySet().forEach(keyString -> {
            if (!updateMap.containsKey(keyString)) {
                insertList.add(map.get(keyString));
            }
        });
        cdFactoryStorehouseInfoMapper.updateBatchByPrimaryKeySelective(updateList);
        cdFactoryStorehouseInfoMapper.insertList(insertList);
        return R.ok();
    }
}

