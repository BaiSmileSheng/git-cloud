package com.cloud.order.service;

import com.cloud.common.easyexcel.service.ExcelCheckManager;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;

/**
 * T+1、T+2草稿计划导入校验接口
 *
 * @author cs
 * @date 2020-06-22
 */
public interface IOms2weeksDemandOrderEditImportService extends ExcelCheckManager<Oms2weeksDemandOrderEdit> {

}
