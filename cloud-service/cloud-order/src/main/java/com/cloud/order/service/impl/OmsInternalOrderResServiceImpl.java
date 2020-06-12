package com.cloud.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.mapper.OmsInternalOrderResMapper;
import com.cloud.order.service.IOmsInternalOrderResService;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteFactoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内单PR/PO原 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-05
 */
@Service
public class OmsInternalOrderResServiceImpl extends BaseServiceImpl<OmsInternalOrderRes> implements IOmsInternalOrderResService {
    @Autowired
    private OmsInternalOrderResMapper omsInternalOrderResMapper;

    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;

    @Autowired
    private RemoteBomService remoteBomService;

    static final String BOM_VERSION_EIGHT = "8";

    static final String BOM_VERSION_NINE = "9";


    @Override
    public R insert800PR(List<OmsInternalOrderRes> list) {
        // 1根据供应商V码去工厂信息表（cd_factory_info）中获取生产工厂编码及名称
        //2 首先通过生产工厂、成品物料号去BOM清单表（cd_bom_info）中获取BOM的版本号，优先8、9版本，有8选8，没8取9，其他取最小版本；
        Map<String, CdFactoryInfo> factoryInfoMap = remoteFactoryInfoService.selectAllByCompanyCodeV(null);
        if (MapUtil.isEmpty(factoryInfoMap)) {
            return R.error("获取工厂编码失败！");
        }
        //获取bom版本
        Map<String, Map<String, String>> bomMap = remoteBomService.selectVersionMap();
        if (MapUtil.isEmpty(bomMap)) {
            return R.error("获取bom版本失败！");
        }
        Date date = DateUtil.date();
        list.forEach(internalOrderRes -> {
            internalOrderRes.setCreateBy("定时任务");
            internalOrderRes.setCreateTime(date);
            String supplieCode = internalOrderRes.getSupplierCode();
            if (factoryInfoMap.get(supplieCode) != null) {
                String productFactoryCode = factoryInfoMap.get(supplieCode).getCompanyCode();
                String productFactoryDesc = factoryInfoMap.get(supplieCode).getCompanyDesc();
                internalOrderRes.setProductFactoryCode(productFactoryCode);
                internalOrderRes.setProductFactoryDesc(productFactoryDesc);
                String key = StrUtil.concat(true, internalOrderRes.getProductMaterialCode(), productFactoryCode);
                //key:成品物料号+生产工厂
                if (bomMap.get(key) != null) {
                    //获取BOM版本
                    String boms = bomMap.get(key).get("version");//逗号分隔多版本拼接
                    List<String> bomList = StrUtil.splitTrim(boms,StrUtil.COMMA);
                    if (CollUtil.contains(bomList, BOM_VERSION_EIGHT)) {
                        //有8取8
                        internalOrderRes.setVersion(BOM_VERSION_EIGHT);
                    } else if (CollUtil.contains(bomList, BOM_VERSION_NINE)){
                        //有9取9
                        internalOrderRes.setVersion(BOM_VERSION_NINE);
                    }else{
                        //取最小的
                        internalOrderRes.setVersion(bomList.stream().min((c,d)->StrUtil.compare(c,d,true)).get());
                    }
                }
            }
        });
        int rows=insertList(list);
        return rows > 0 ? R.ok() : R.error();
    }

    /**
     * 根据Marker删除
     * @param marker
     * @return
     */
	@Override
	public int deleteByMarker(String marker){
		 return omsInternalOrderResMapper.deleteByMarker(marker);
	}




}
