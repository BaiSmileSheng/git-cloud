package com.cloud.system.service.impl;

    import cn.hutool.core.lang.Dict;
    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.mapper.CdFactoryStorehouseInfoMapper;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Map;

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
     * @param dicts
     * @return
     */
    public Map<String, Map<String, String>> selectStorehouseToMap(List<Dict> dicts) {
        return cdFactoryStorehouseInfoMapper.selectStorehouseToMap(dicts);
    }
}
