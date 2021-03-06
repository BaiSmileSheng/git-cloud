package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.vo.CdBomInfoOtherSysVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * bom清单数据 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ICdBomInfoService extends BaseService<CdBomInfo> {
    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    R checkBomNum(String productMaterialCode, String rawMaterialCode, int applyNum);

    /**
     * @Description: 定时任务获取BOM清单-保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    R saveBomInfoBySap();

    /**
     * 根据物料号工厂分组取bom版本
     * @param dicts
     * @return
     */
    Map<String,Map<String, String>> selectVersionMap(List<Dict> dicts);

    /**
     * 删除全表数据
     * @return
     */
    R deleteAll();
    /**
     * Description:  根据成品专用号、生产工厂、版本查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    R selectBomList(List<Dict> list);

    /**
     * 实时更新bom信息
     * @param cdBomInfoOtherSysVo
     * @return
     */
    R pbomUpdateBom(@RequestBody CdBomInfoOtherSysVo cdBomInfoOtherSysVo);
}
