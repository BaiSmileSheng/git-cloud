package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.service.ICdProductOverdueService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 超期库存  提供者
 *
 * @author lihongxia
 * @date 2020-06-17
 */
@RestController
@RequestMapping("productOverdue")
@Api(tags = "超期库存  提供者")
public class CdProductOverdueController extends BaseController {

    @Autowired
    private ICdProductOverdueService cdProductOverdueService;

    /**
     * 查询超期库存
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询超期库存 ", response = CdProductOverdue.class)
    public CdProductOverdue get(Long id) {
        return cdProductOverdueService.selectByPrimaryKey(id);

    }

    /**
     * 查询超期库存 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "超期库存 查询分页", response = CdProductOverdue.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生成工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "物料号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdProductOverdue cdProductOverdue) {
        Example example = assemblyConditions(cdProductOverdue);
        startPage();
        List<CdProductOverdue> cdProductOverdueList = cdProductOverdueService.selectByExample(example);
        return getDataTable(cdProductOverdueList);
    }

    /**
     * 组装查询条件
     * @param cdProductOverdue
     * @return
     */
    private Example assemblyConditions(CdProductOverdue cdProductOverdue){
        Example example = new Example(CdProductOverdue.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductOverdue.getCreateBy())){
            criteria.andEqualTo("createBy",cdProductOverdue.getCreateBy());
        }
        if(StringUtils.isNotBlank(cdProductOverdue.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode",cdProductOverdue.getProductFactoryCode());
        }
        if(StringUtils.isNotBlank(cdProductOverdue.getProductMaterialCode())){
            criteria.andEqualTo("productMaterialCode",cdProductOverdue.getProductMaterialCode());
        }
        return example;
    }
    /**
     * 导出模板
     * @return
     */
    @GetMapping("exportTemplate")
    @HasPermissions("system:productOverdue:exportTemplate")
    @ApiOperation(value = "导出模板", response = CdProductOverdue.class)
    public R exportTemplate(){
        String fileName = "超期库存模板.xlsx";
        return EasyExcelUtil.writeExcel(Arrays.asList(),fileName,fileName,new CdProductOverdue());
    }

    @GetMapping("export")
    @HasPermissions("system:productOverdue:export")
    @ApiOperation(value = "超期库存 导出", response = CdProductOverdue.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生成工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "物料号", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdProductOverdue cdProductOverdue){
        String fileName = "超期库存.xlsx";
        Example example = assemblyConditions(cdProductOverdue);
        List<CdProductOverdue> cdProductOverdueList = cdProductOverdueService.selectByExample(example);
        return EasyExcelUtil.writeExcel(cdProductOverdueList,fileName,fileName,new CdProductOverdue());
    }
    /**
     * 导入
     * @return
     */
    @PostMapping("importFactoryStorehouse")
    @HasPermissions("system:productOverdue:importFactoryStorehouse")
    @ApiOperation(value = "导入", response = CdProductOverdue.class)
    public R importFactoryStorehouse(@RequestParam("file") MultipartFile file){
        List<CdProductOverdue> list =  (List<CdProductOverdue>)EasyExcelUtil.readMulExcel(file,new CdProductOverdue());
        if(CollectionUtils.isEmpty(list)){
            return R.error("导入数据不存在");
        }
        for(CdProductOverdue cdProductOverdue : list){
            //校验入参
            ValidatorUtils.validateEntity(cdProductOverdue);
            cdProductOverdue.setDelFlag(DeleteFlagConstants.NO_DELETED);
            cdProductOverdue.setCreateBy(getLoginName());
            cdProductOverdue.setCreateTime(new Date());
        }
        return cdProductOverdueService.importFactoryStorehouse(list);
    }


    /**
     * 新增保存超期库存
     */
    @PostMapping("save")
    @OperLog(title = "新增保存超期库存 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存超期库存 ", response = R.class)
    public R addSave(@RequestBody CdProductOverdue cdProductOverdue) {
        cdProductOverdueService.insertSelective(cdProductOverdue);
        return R.data(cdProductOverdue.getId());
    }

    /**
     * 修改保存超期库存
     */
    @PostMapping("update")
    @OperLog(title = "修改保存超期库存 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存超期库存 ", response = R.class)
    public R editSave(@RequestBody CdProductOverdue cdProductOverdue) {
        return toAjax(cdProductOverdueService.updateByPrimaryKeySelective(cdProductOverdue));
    }

    /**
     * 删除超期库存
     */
    @PostMapping("remove")
    @OperLog(title = "删除超期库存 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除超期库存 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdProductOverdueService.deleteByIds(ids));
    }

}
