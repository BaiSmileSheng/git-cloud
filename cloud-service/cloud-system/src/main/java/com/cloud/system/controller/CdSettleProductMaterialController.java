package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.service.ICdSettleProductMaterialService;
import com.cloud.system.util.EasyExcelUtilOSS;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * 物料号和加工费号对应关系  提供者
 *
 * @author cs
 * @date 2020-06-05
 */
@RestController
@RequestMapping("settleProducMaterial")
@Api(tags = "物料号和加工费号对应关系")
public class CdSettleProductMaterialController extends BaseController {

    @Autowired
    private ICdSettleProductMaterialService cdSettleProductMaterialService;


    @GetMapping("/export")
    @HasPermissions("system:settleProducMaterial:export")
    @ApiOperation(value = "导出模板", response = CdSettleProductMaterial.class)
    @OperLog(title = "操作日志", businessType = BusinessType.EXPORT)
    public R export() {
        String fileName = "物料号和加工费号对应关系数据.xlsx";
        return EasyExcelUtilOSS.writeExcel(Arrays.asList(),fileName,fileName,new CdSettleProductMaterial());
    }

    /**
     * 多sheet文件导入
     * @param file
     * @return
     */
    @PostMapping("/importMul")
    @ResponseBody
    @ApiOperation(value = "多sheet文件导入")
    public R mulImport(@RequestPart("file") MultipartFile file) {
        List<CdSettleProductMaterial> cdSettleProductMaterialList = (List<CdSettleProductMaterial>) EasyExcelUtil
                .readMulExcel(file,new CdSettleProductMaterial());
        cdSettleProductMaterialService.batchInsertOrUpdate(cdSettleProductMaterialList);
        return R.ok();
    }

    /**
     * 根据成品物料编码 查物料号和加工费号对应关系
     * @param productMaterialCode 成品物料编码
     * @param rawMaterialCode 加工费号
     * @return 物料号和加工费号对应关系列表
     */
    @GetMapping("listByCode")
    @ApiOperation(value = "物料号和加工费号对应关系 查询分页", response = CdSettleProductMaterial.class)
    public R listByCode(@RequestParam(value = "productMaterialCode") String productMaterialCode,
                                                    @RequestParam(value = "rawMaterialCode",required = false) String rawMaterialCode){
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("productMaterialCode",productMaterialCode);
        if(StringUtils.isNotBlank(rawMaterialCode)){
            criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        }
        List<CdSettleProductMaterial> cdSettleProductMaterialList = cdSettleProductMaterialService.selectByExample(example);
        return R.data(cdSettleProductMaterialList);
    }
    /**
     * 查询物料号和加工费号对应关系
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询物料号和加工费号对应关系 ", response = CdSettleProductMaterial.class)
    public CdSettleProductMaterial get(Long id) {
        return cdSettleProductMaterialService.selectByPrimaryKey(id);

    }

    /**
     * 查询物料号和加工费号对应关系 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "物料号和加工费号对应关系 查询分页", response = CdSettleProductMaterial.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品物料编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "加工费号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdSettleProductMaterial cdSettleProductMaterial) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdSettleProductMaterial.getProductMaterialCode())){
            criteria.andEqualTo("productMaterialCode",cdSettleProductMaterial.getProductMaterialCode());
        }
        if(StringUtils.isNotBlank(cdSettleProductMaterial.getRawMaterialCode())){
            criteria.andEqualTo("rawMaterialCode",cdSettleProductMaterial.getRawMaterialCode());
        }
        startPage();
        List<CdSettleProductMaterial> cdSettleProductMaterialList = cdSettleProductMaterialService.selectByExample(example);
        return getDataTable(cdSettleProductMaterialList);
    }


    /**
     * 新增保存物料号和加工费号对应关系
     */
    @PostMapping("save")
    @OperLog(title = "新增保存物料号和加工费号对应关系 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存物料号和加工费号对应关系 ", response = R.class)
    public R addSave(@RequestBody CdSettleProductMaterial cdSettleProductMaterial) {
        cdSettleProductMaterialService.insertSelective(cdSettleProductMaterial);
        return R.data(cdSettleProductMaterial.getId());
    }

    /**
     * 修改保存物料号和加工费号对应关系
     */
    @PostMapping("update")
    @OperLog(title = "修改保存物料号和加工费号对应关系 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物料号和加工费号对应关系 ", response = R.class)
    public R editSave(@RequestBody CdSettleProductMaterial cdSettleProductMaterial) {
        return toAjax(cdSettleProductMaterialService.updateByPrimaryKeySelective(cdSettleProductMaterial));
    }

    /**
     * 删除物料号和加工费号对应关系
     */
    @PostMapping("remove")
    @OperLog(title = "删除物料号和加工费号对应关系 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物料号和加工费号对应关系 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdSettleProductMaterialService.deleteByIds(ids));
    }

    /**
     * 查询物料号和委外方式查加工费号
     */
    @GetMapping("selectOne")
    @ApiOperation(value = "根据id查询物料号和加工费号对应关系 ", response = CdSettleProductMaterial.class)
    public R selectOne(@RequestParam(value = "productMaterialCode") String productMaterialCode,
                                             @RequestParam("outsourceWay") String outsourceWay ) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("outsourceWay",outsourceWay);
        CdSettleProductMaterial cdSettleProductMaterial = cdSettleProductMaterialService.findByExampleOne(example);
        return R.data(cdSettleProductMaterial);

    }

}
