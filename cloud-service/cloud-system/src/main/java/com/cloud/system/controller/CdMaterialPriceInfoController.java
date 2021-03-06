package com.cloud.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.enums.PriceTypeEnum;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 查所有的加工费号
     */
    @GetMapping("listJGF")
    @ApiOperation(value = "查所有的加工费号", response = CdMaterialPriceInfo.class)
    public R listJGF(){
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("priceType", PriceTypeEnum.PRICE_TYPE_1.getCode());
        List<CdMaterialPriceInfo> cdMaterialPriceInfoList = cdMaterialPriceInfoService.selectByExample(example);
        List<String> list = cdMaterialPriceInfoList.stream().map(m -> m.getMaterialCode()).collect(Collectors.toList());
        return R.data(list);
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
     * @param materialCode
     * @param beginDate
     * @param endDate
     * @return CdMaterialPriceInfo  list
     */
    @GetMapping("findByMaterialCode")
    public List<CdMaterialPriceInfo> findByMaterialCodeAndPurchasingGroup(String materialCode,String purchasingGroup ,String beginDate, String endDate){
        //查询CdMaterialPriceInfo
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(materialCode)) {
            criteria.andEqualTo("materialCode", materialCode);
        }
        if (StrUtil.isNotBlank(purchasingGroup)) {
            criteria.andEqualTo("purchasingGroup", purchasingGroup);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            criteria.andLessThanOrEqualTo("beginDate", beginDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            criteria.andGreaterThanOrEqualTo("endDate", endDate);
        }
        List<CdMaterialPriceInfo> cdMaterialPriceInfoList = cdMaterialPriceInfoService.selectByExample(example);
        return cdMaterialPriceInfoList;
    }

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return R CdMaterialPriceInfo对象
     */
    @PostMapping("checkSynchroSAP")
    public R checkSynchroSAP(String materialCode){
        return cdMaterialPriceInfoService.checkSynchroSAP(materialCode);
    }

    /**
     * 根据物料号和采购组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdMaterialPriceInfo>
     */
    @PostMapping("selectPriceByInMaterialCodeAndDate")
    public Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(String materialCodes, String beginDate, String endDate) {
        List<String> materialCodeList= CollectionUtil.newArrayList(materialCodes.split(","));
        return cdMaterialPriceInfoService.selectPriceByInMaterialCodeAndDate(materialCodeList,beginDate,endDate);
    }

    /**
     * 定时加工费价格同步
     * @return 成功或失败
     */
    @PostMapping("synPriceJGF")
    @OperLog(title = "定时加工费价格同步", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时加工费价格同步", response = R.class)
    public R synPriceJGF(){
        R result = cdMaterialPriceInfoService.synPriceJGF();
        return result;
    }
    /**
     * 定时原材料价格同步
     * @return 成功或失败
     */
    @PostMapping("synPriceYCL")
    @OperLog(title = "定时原材料价格同步", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时原材料价格同步", response = R.class)
    public R synPriceYCL(){
        R result = cdMaterialPriceInfoService.synPriceYCL();
        return result;
    }

    /**
     * 根据唯一索引查一条数据
     * @param materialCode
     * @param purchasingOrganization
     * @param memberCode
     * @return
     */
    @GetMapping("selectOneByCondition")
    public R selectOneByCondition(@RequestParam("materialCode") String materialCode,
                                  @RequestParam("purchasingOrganization") String purchasingOrganization ,
                                  @RequestParam("memberCode") String memberCode,
                                  @RequestParam("priceType")String priceType){
        //查询CdMaterialPriceInfo
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("materialCode", materialCode);
        criteria.andEqualTo("purchasingOrganization", purchasingOrganization);
        criteria.andEqualTo("memberCode", memberCode);
        criteria.andEqualTo("priceType",priceType);
        CdMaterialPriceInfo cdMaterialPriceInfo = cdMaterialPriceInfoService.findByExampleOne(example);
        return R.data(cdMaterialPriceInfo);
    }

    /**
     * 按加工费号模糊查询
     */
    @GetMapping("selectByLikeByMaterialCode")
    @ApiOperation(value = "按加工费号模糊查询", response = CdMaterialPriceInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "专用号", required = false, paramType = "query", dataType = "String")
    })
    public R selectByLikeByMaterialCode(@RequestParam(value = "materialCode") String materialCode){
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("priceType", PriceTypeEnum.PRICE_TYPE_1.getCode());
        criteria.andLike("materialCode",materialCode + "%");
        startPage();
        List<CdMaterialPriceInfo> cdMaterialPriceInfoList = cdMaterialPriceInfoService.selectByExample(example);
        return R.data(cdMaterialPriceInfoList);
    }
    /**
     * 根据成品物料号查询SAP成本价格
     */
    @PostMapping("selectMaterialPrice")
    public R selectMaterialPrice(@RequestBody Map<String,List<CdSettleProductMaterial>> map){
        return cdMaterialPriceInfoService.selectMaterialPrice(map);
    }
    /**
     * 根据原材料物料和供应商编码查询价格
     */
    @PostMapping("selectBymaterialSupplierList")
    public R selectBymaterialSupplierList(@RequestBody List<CdMaterialPriceInfo> list){
        return cdMaterialPriceInfoService.selectBymaterialSupplierList(list);
    }
}
