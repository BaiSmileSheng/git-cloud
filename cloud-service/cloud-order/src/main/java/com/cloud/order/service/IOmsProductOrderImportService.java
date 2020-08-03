package com.cloud.order.service;

import com.cloud.common.easyexcel.service.ExcelCheckManager;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEditImport;
import com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo;

/**
 * 排产订单 导入校验接口
 *
 * @author ltq
 * @date 2020-08-03
 */
public interface IOmsProductOrderImportService extends ExcelCheckManager<OmsProductionOrderExportVo> {



}
