package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.service.ICdFactoryInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 工厂信息  提供者
 *
 * @author cs
 * @date 2020-06-03
 */
@RestController
@RequestMapping("factoryInfo")
@Api(tags = "工厂信息")
public class CdFactoryInfoController extends BaseController {

    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;

    /**
     * 查询工厂信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询工厂信息 ", response = CdFactoryInfo.class)
    public CdFactoryInfo get(Long id) {
        return cdFactoryInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询工厂信息
     */
    @GetMapping("getOne")
    @ApiOperation(value = "根据工厂编码查询工厂信息 ", response = CdFactoryInfo.class)
    public R selectOneByFactory(String factoryCode) {
        Example example = new Example(CdFactoryInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("factoryCode", factoryCode)
                .andEqualTo("delFlag","0");
        return R.data(cdFactoryInfoService.findByExampleOne(example));
    }

    /**
     * 根据公司V码查询
     */
    @GetMapping("selectAllByCompanyCodeV")
    @ApiOperation(value = "根据公司V码查询 ", response = CdFactoryInfo.class)
    public R selectAllByCompanyCodeV(String companyCodeV) {
         return cdFactoryInfoService.selectAllByCompanyCodeV(companyCodeV);
    }

    /**
     * 查询工厂信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "工厂信息 查询分页", response = CdFactoryInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdFactoryInfo cdFactoryInfo) {
        Example example = new Example(CdFactoryInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(example);
        return getDataTable(cdFactoryInfoList);
    }

    /**
     * 查询工厂信息 列表
     */
    @GetMapping("listAll")
    @ApiOperation(value = "查询工厂信息 列表 ", response = CdFactoryInfo.class)
    public R listAll(){
        Example example = new Example(CdFactoryInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(example);
        return R.data(cdFactoryInfoList);
    }

    /**
     * 新增保存工厂信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存工厂信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存工厂信息 ", response = R.class)
    public R addSave(@RequestBody CdFactoryInfo cdFactoryInfo) {
        cdFactoryInfoService.insertSelective(cdFactoryInfo);
        return R.data(cdFactoryInfo.getId());
    }

    /**
     * 修改保存工厂信息
     */
    @PostMapping("update")
    @OperLog(title = "修改保存工厂信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存工厂信息 ", response = R.class)
    public R editSave(@RequestBody CdFactoryInfo cdFactoryInfo) {
        return toAjax(cdFactoryInfoService.updateByPrimaryKeySelective(cdFactoryInfo));
    }

    /**
     * 删除工厂信息
     */
    @PostMapping("remove")
    @OperLog(title = "删除工厂信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除工厂信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdFactoryInfoService.deleteByIds(ids));
    }

    /**
     * 查询所有公司编码
     * @return
     */
    @GetMapping("getAllCompanyCode")
    @ApiOperation(value = "获取所有公司编码 ", response = R.class)
    public R getAllCompanyCode(){
        return cdFactoryInfoService.selectAllCompanyCode();
    }
}
