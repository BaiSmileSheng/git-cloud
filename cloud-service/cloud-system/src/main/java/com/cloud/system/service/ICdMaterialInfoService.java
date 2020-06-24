package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.webService.material.RowRisk;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * 物料信息 Service接口
 *
 * @author ltq
 * @date 2020-06-01
 */
public interface ICdMaterialInfoService extends BaseService<CdMaterialInfo> {
    /**
     * @Description: 保存MDM接口获取的物料信息数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    R saveMaterialInfo();

    /**
     * @Description: 接口获取MDM物料信息
     * @Param: [list, pageAll, page, batchId]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    R materialInfoInterface(List<RowRisk> list, int page, String batchId);

    /**
     * @Description: 根据工厂、物料批量更新
     * @Param: [list]
     * @return: int
     * @Author: ltq
     * @Date: 2020/6/5
     */
    int updateBatchByFactoryAndMaterial(List<CdMaterialInfo> list);

    /**
     * @Description: 更新SAP获取的UPH数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    R updateUphBySap();

    /**
     * 根据物料号集合查询物料信息
     * @param materialCodes
     * @return
     */
    R selectInfoByInMaterialCodeAndMaterialType(List<String> materialCodes,String materialType);
    /**
     * Description:  根据成品专用号、生产工厂、物料类型查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    R selectListByMaterialList(List<Dict> list);
    }
