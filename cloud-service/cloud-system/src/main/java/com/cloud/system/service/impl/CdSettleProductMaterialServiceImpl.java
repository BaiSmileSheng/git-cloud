package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdSettleProductMaterialMapper;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.service.ICdSettleProductMaterialService;
import com.cloud.common.core.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料号和加工费号对应关系 Service业务层处理
 *
 * @author cs
 * @date 2020-06-05
 */
@Service
public class CdSettleProductMaterialServiceImpl extends BaseServiceImpl<CdSettleProductMaterial> implements ICdSettleProductMaterialService {
    @Autowired
    private CdSettleProductMaterialMapper cdSettleProductMaterialMapper;

    /**
     * 批量新增或修改(根据 product_material_code+raw_material_code唯一性修改)
     * @param list 物料号和加工费号对应关系集合
     * @return 成功或失败
     */
    @Override
    public R batchInsertOrUpdate(List<CdSettleProductMaterial> list) {
        //1.去重避免导入数据中存在重复数据
        Map<String,CdSettleProductMaterial> map = new HashMap<>();
        for(CdSettleProductMaterial cdSettleProductMaterial : list){
            String key = cdSettleProductMaterial.getProductMaterialCode() + cdSettleProductMaterial.getRawMaterialCode();
            map.put(key,cdSettleProductMaterial);
        }
        //去重后的list
        List<CdSettleProductMaterial> listReq = map.values().stream().collect(Collectors.toList());
        //2.批量查询存在的就是要修改的集合
        List<CdSettleProductMaterial> updateList = cdSettleProductMaterialMapper.batchSelect(listReq);
        //要修改的集合转成map key:product_material_code+raw_material_code
        Map<String,CdSettleProductMaterial> updateMap =  new HashMap<>();
        for(CdSettleProductMaterial cdSettleProductMateriaRes : updateList){
            String key = cdSettleProductMateriaRes.getProductMaterialCode() + cdSettleProductMateriaRes.getRawMaterialCode();
            updateMap.put(key,cdSettleProductMateriaRes);
        }
        //3.批量新增的集合  去重后的list 减去 updateList
        List<CdSettleProductMaterial> insertList = new ArrayList<>();
        for(CdSettleProductMaterial cdSettleProductMateriaReq :listReq){
            String key = cdSettleProductMateriaReq.getProductMaterialCode() + cdSettleProductMateriaReq.getRawMaterialCode();
            if(!updateMap.containsKey(key)){
                insertList.add(cdSettleProductMateriaReq);
            }
        }
        if(updateList.size() > 0){
            cdSettleProductMaterialMapper.updateBatchByPrimaryKeySelective(updateList);
        }
        if(insertList.size() > 0){
            cdSettleProductMaterialMapper.insertList(insertList);
        }
        return R.ok();
    }
}
