package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

/**
 * 物料扩展信息 对象 cd_material_extend_info
 * 导入模板
 * @author lihongia
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物料扩展信息 ")
@HeadRowHeight(35)
@ContentRowHeight(20)
public class CdMaterialExtendInfoImportVo {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 物料号
     */
    @ColumnWidth(50)
    @ExcelProperty(value = "成品专用号",index = 0)
    @ApiModelProperty(value = "物料号")
    private String materialCode;

    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 产品类别
     */
    @ColumnWidth(50)
    @ExcelProperty(value = "产品类别",index = 1)
    @ApiModelProperty(value = "产品类别")
    private String productType;

    /**
     * 生命周期
     */
    @ColumnWidth(25)
    @ExcelProperty(value = "生命周期",index = 2)
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 可否加工承揽
     */
    @ColumnWidth(25)
    @ExcelProperty(value = "可否加工承揽",index = 3)
    @ApiModelProperty(value = "可否加工承揽")
    private String isPuttingOut;

    /**
     * 是否ZN认证
     */
    @ColumnWidth(25)
    @ExcelProperty(value = "是否ZN认证",index = 4)
    @ApiModelProperty(value = "是否ZN认证")
    private String isZnAttestation;

    /**
     * 建立日期
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "建立日期")
    private Date establishDate;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
