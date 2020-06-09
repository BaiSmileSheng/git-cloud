package com.cloud.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.service.ICdSapSalePriceService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 成品销售价格  提供者
 *
 * @author cs
 * @date 2020-06-03
 */
@RestController
@RequestMapping("salePrice")
public class CdSapSalePriceController extends BaseController {

    @Autowired
    private ICdSapSalePriceService cdSapSalePriceService;

    /**
     * 查询成品销售价格
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询成品销售价格 ", response = CdSapSalePrice.class)
    public CdSapSalePrice get(Long id) {
        return cdSapSalePriceService.selectByPrimaryKey(id);

    }

    /**
     * 查询成品销售价格 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "成品销售价格 查询分页", response = CdSapSalePrice.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdSapSalePrice cdSapSalePrice) {
        Example example = new Example(CdSapSalePrice.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdSapSalePrice> cdSapSalePriceList = cdSapSalePriceService.selectByExample(example);
        return getDataTable(cdSapSalePriceList);
    }

    /**
     * 根据Example条件查询列表
     * @param materialCode
     * @param beginDate
     * @param endDate
     * @return List<CdSapSalePrice>
     */
    @GetMapping("findByMaterialCodeAndOraganization")
    public List<CdSapSalePrice> findByMaterialCodeAndOraganization(String materialCode,String oraganization, String beginDate, String endDate){
        //查询CdMaterialPriceInfo
        Example example = new Example(CdSapSalePrice.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(materialCode)) {
            criteria.andEqualTo("materialCode", materialCode);
        }
        if (StrUtil.isNotBlank(oraganization)) {
            criteria.andEqualTo("marketingOrganization", oraganization);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            criteria.andLessThanOrEqualTo("beginDate", beginDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            criteria.andGreaterThanOrEqualTo("endDate", endDate);
        }

        List<CdSapSalePrice> cdSapSalePriceList = cdSapSalePriceService.selectByExample(example);
        return cdSapSalePriceList;
    }

    /**
     * 新增保存成品销售价格
     */
    @PostMapping("save")
    @OperLog(title = "新增保存成品销售价格 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存成品销售价格 ", response = R.class)
    public R addSave(@RequestBody CdSapSalePrice cdSapSalePrice) {
        cdSapSalePriceService.insertSelective(cdSapSalePrice);
        return R.data(cdSapSalePrice.getId());
    }

    /**
     * 修改保存成品销售价格
     */
    @PostMapping("update")
    @OperLog(title = "修改保存成品销售价格 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存成品销售价格 ", response = R.class)
    public R editSave(@RequestBody CdSapSalePrice cdSapSalePrice) {
        return toAjax(cdSapSalePriceService.updateByPrimaryKeySelective(cdSapSalePrice));
    }

    /**
     * 删除成品销售价格
     */
    @PostMapping("remove")
    @OperLog(title = "删除成品销售价格 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除成品销售价格 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdSapSalePriceService.deleteByIds(ids));
    }


    /**
     * 根据专用号和销售组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdMaterialPriceInfo>
     */
    @PostMapping("selectPriceByInMaterialCodeAndDate")
    public Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(String materialCodes, String beginDate, String endDate) {
        List<String> materialCodeList= CollectionUtil.newArrayList(materialCodes.split(","));
        return cdSapSalePriceService.selectPriceByInMaterialCodeAndDate(materialCodeList,beginDate,endDate);
    }
}
