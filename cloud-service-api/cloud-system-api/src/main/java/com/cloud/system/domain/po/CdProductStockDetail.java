package com.cloud.system.domain.po;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.system.domain.entity.CdProductInProduction;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.CdProductWarehouse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;

/**
 * 成品库存主 对象 cd_product_stock
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品库存详细信息 ")
public class CdProductStockDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 成品库存主
     */
    private List<CdProductStock> cdProductStockList;

    /**
     * 成品库存在产明细
     */
    private List<CdProductInProduction> cdProductInProductionList;

    /**
     * 成品库存在途明细
     */
    private List<CdProductPassage> cdProductPassageList;

    /**
     * 成品库存在库明细
     */
    private List<CdProductWarehouse> cdProductWarehouseList;
}
