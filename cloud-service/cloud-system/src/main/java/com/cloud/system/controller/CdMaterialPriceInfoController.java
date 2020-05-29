package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.DateUtils;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * SAP成本价格
 *
 * @author cs
 * @date 2020-05-26
 */
@RestController
@RequestMapping("materialPrice")
@Api(tags = "SAP成本价格")
public class CdMaterialPriceInfoController extends BaseController {

    @Autowired
    private ICdMaterialPriceInfoService cdMaterialPriceInfoService;

    /**
     * 查询SAP成本价格
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询SAP成本价格 ", response = CdMaterialPriceInfo.class)
    public CdMaterialPriceInfo get(Long id) {
        return cdMaterialPriceInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询SAP成本价格 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "SAP成本价格 查询分页", response = CdMaterialPriceInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdMaterialPriceInfo cdMaterialPriceInfo) {
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdMaterialPriceInfo> cdMaterialPriceInfoList = cdMaterialPriceInfoService.selectByExample(example);
        return getDataTable(cdMaterialPriceInfoList);
    }


    /**
     * 新增保存SAP成本价格
     */
    @PostMapping("save")
    @OperLog(title = "新增保存SAP成本价格 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存SAP成本价格 ", response = R.class)
    public R addSave(@RequestBody CdMaterialPriceInfo cdMaterialPriceInfo) {
        cdMaterialPriceInfoService.insertSelective(cdMaterialPriceInfo);
        return R.data(cdMaterialPriceInfo.getId());
    }

    /**
     * 修改保存SAP成本价格
     */
    @PostMapping("update")
    @OperLog(title = "修改保存SAP成本价格 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存SAP成本价格 ", response = R.class)
    public R editSave(@RequestBody CdMaterialPriceInfo cdMaterialPriceInfo) {
        return toAjax(cdMaterialPriceInfoService.updateByPrimaryKeySelective(cdMaterialPriceInfo));
    }

    /**
     * 删除SAP成本价格
     */
    @PostMapping("remove")
    @OperLog(title = "删除SAP成本价格 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除SAP成本价格 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdMaterialPriceInfoService.deleteByIds(ids));
    }


    /**
     * 根据Example条件查询列表
     * @param example
     * @return CdMaterialPriceInfo  list
     */
    @GetMapping("example")
    public R findByExample(Example example){
        List<CdMaterialPriceInfo> cdMaterialPriceInfoList = cdMaterialPriceInfoService.selectByExample(example);
        return R.data(cdMaterialPriceInfoList);
    }

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return R CdMaterialPriceInfo对象
     */
    @PostMapping("checkSynchroSAP")
    public R checkSynchroSAP(String materialCode){
        String dateStr = DateUtils.getTime();
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        //根据物料号  有效期查询SAP价格
        criteria.andEqualTo("materialCode", materialCode)
                .andLessThanOrEqualTo("beginDate", dateStr)
                .andGreaterThanOrEqualTo("endDate", dateStr);
        R r = findByExample(example);
        if(r==null){
            throw new BusinessException("校验价格同步SAP失败！");
        }
        List<CdMaterialPriceInfo> materialPrices = (List<CdMaterialPriceInfo>) r.get("data");
        if(materialPrices==null||materialPrices.size()==0){
            throw new BusinessException("物料号未同步SAP价格！");
        }
        CdMaterialPriceInfo materialPrice = materialPrices.get(0);
        if(materialPrice==null||materialPrice.getProcessPrice()==null||materialPrice.getProcessPrice().compareTo(BigDecimal.ZERO)<0){
            throw new BusinessException("物料号未同步SAP价格！");
        }
        return R.data(materialPrice);
    }
}
