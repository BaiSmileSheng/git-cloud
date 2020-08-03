package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdSettleProductMaterialExcelImportVo;
import com.cloud.system.domain.vo.CdSettleProductMaterialExportVo;
import com.cloud.system.service.ICdSettleProductMaterialService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.cloud.system.util.SettleProductMaterialWriteHandler;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
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


    @GetMapping("/exportMul")
    @HasPermissions("system:settleProducMaterial:exportMul")
    @ApiOperation(value = "导入模板", response = CdSettleProductMaterial.class)
    public R exportMul() {
        String fileName = "物料号和加工费号对应关系数据.xlsx";
        return EasyExcelUtilOSS.writePostilExcel(Arrays.asList(),fileName,fileName,new CdSettleProductMaterialExcelImportVo(),
                new SettleProductMaterialWriteHandler());
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @PostMapping("/importMul")
    @HasPermissions("system:settleProducMaterial:importMul")
    @ResponseBody
    @ApiOperation(value = "导入")
    public R importMul(@RequestPart("file") MultipartFile file) throws Exception{
        SysUser sysUser = getUserInfo(SysUser.class);
        R r = cdSettleProductMaterialService.importMul(sysUser,file);
        return r;
    }

    /**
     * 根据成品物料编码 查物料号和加工费号对应关系
     * @param productMaterialCode 成品物料编码
     * @param rawMaterialCode 加工费号
     * @return 物料号和加工费号对应关系列表
     */
    @GetMapping("listByCode")
    @ApiOperation(value = "查询物料号和加工费号对应关系", response = CdSettleProductMaterial.class)
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
            @ApiImplicitParam(name = "rawMaterialCode", value = "加工费号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "outsourceWay", value = "加工委外方式", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdSettleProductMaterial cdSettleProductMaterial) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        assemblyConditions(cdSettleProductMaterial, criteria);
        startPage();
        example.orderBy("createTime").desc();
        List<CdSettleProductMaterial> cdSettleProductMaterialList = cdSettleProductMaterialService.selectByExample(example);
        return getDataTable(cdSettleProductMaterialList);
    }

    /**
     * 组装查询参数
     * @param cdSettleProductMaterial
     * @param criteria
     */
    private void assemblyConditions(@ApiIgnore CdSettleProductMaterial cdSettleProductMaterial, Example.Criteria criteria) {
        if(StringUtils.isNotBlank(cdSettleProductMaterial.getProductMaterialCode())){
            criteria.andEqualTo("productMaterialCode",cdSettleProductMaterial.getProductMaterialCode());
        }
        if(StringUtils.isNotBlank(cdSettleProductMaterial.getOutsourceWay())){
            criteria.andEqualTo("outsourceWay",cdSettleProductMaterial.getOutsourceWay());
        }
        if(StringUtils.isNotBlank(cdSettleProductMaterial.getRawMaterialCode())){
            criteria.andEqualTo("rawMaterialCode",cdSettleProductMaterial.getRawMaterialCode());
        }
    }

    /**
     * 导出
     * @param cdSettleProductMaterial
     * @return
     */
    @HasPermissions("system:settleProducMaterial:export")
    @GetMapping("export")
    @ApiOperation(value = "物料号和加工费号对应关系 导出", response = CdSettleProductMaterial.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品物料编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "加工费号", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdSettleProductMaterial cdSettleProductMaterial) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        assemblyConditions(cdSettleProductMaterial, criteria);
        example.orderBy("createTime").desc();
        List<CdSettleProductMaterial> cdSettleProductMaterialList = cdSettleProductMaterialService.selectByExample(example);
        String fileName = "物料号和加工费号维护.xlsx";
        return EasyExcelUtilOSS.writeExcel(cdSettleProductMaterialList,fileName,fileName,new CdSettleProductMaterialExportVo());
    }

    /**
     * 新增保存物料号和加工费号对应关系
     */
    @HasPermissions("system:settleProducMaterial:save")
    @PostMapping("save")
    @OperLog(title = "新增保存物料号和加工费号对应关系 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存物料号和加工费号对应关系 ", response = R.class)
    public R addSave(@RequestBody CdSettleProductMaterial cdSettleProductMaterial) {
        //校验入参
        ValidatorUtils.validateEntity(cdSettleProductMaterial);
        SysUser sysUser = getUserInfo(SysUser.class);
        cdSettleProductMaterial.setCreateBy(sysUser.getLoginName());
        cdSettleProductMaterial.setUpdateBy(sysUser.getLoginName());
        cdSettleProductMaterial.setUpdateTime(new Date());
        R r = cdSettleProductMaterialService.insertProductMaterial(cdSettleProductMaterial);
        return r ;
    }

    /**
     * 修改保存物料号和加工费号对应关系
     */
    @HasPermissions("system:settleProducMaterial:update")
    @PostMapping("update")
    @OperLog(title = "修改保存物料号和加工费号对应关系 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物料号和加工费号对应关系 ", response = R.class)
    public R editSave(@RequestBody CdSettleProductMaterial cdSettleProductMaterial) {
        //校验入参
        ValidatorUtils.validateEntity(cdSettleProductMaterial);
        SysUser sysUser = getUserInfo(SysUser.class);
        cdSettleProductMaterial.setUpdateBy(sysUser.getLoginName());
        R r = cdSettleProductMaterialService.updateProductMaterial(cdSettleProductMaterial);
        return r;
    }

    /**
     * 删除物料号和加工费号对应关系
     */
    @HasPermissions("system:settleProducMaterial:remove")
    @PostMapping("remove")
    @OperLog(title = "删除物料号和加工费号对应关系 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物料号和加工费号对应关系 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdSettleProductMaterialService.deleteByIdsWL(ids));
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
