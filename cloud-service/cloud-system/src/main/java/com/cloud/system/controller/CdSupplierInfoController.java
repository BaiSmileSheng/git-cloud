package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.service.ICdSupplierInfoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 供应商信息  提供者
 *
 * @author cs
 * @date 2020-05-28
 */
@RestController
@RequestMapping("supplier")
public class CdSupplierInfoController extends BaseController {

    @Autowired
    private ICdSupplierInfoService cdSupplierInfoService;

    /**
     * 查询供应商信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询供应商信息 ", response = CdSupplierInfo.class)
    public CdSupplierInfo get(Long id) {
        return cdSupplierInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询供应商信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "供应商信息 查询分页", response = CdSupplierInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdSupplierInfo cdSupplierInfo) {
        Example example = new Example(CdSupplierInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdSupplierInfo> cdSupplierInfoList = cdSupplierInfoService.selectByExample(example);
        return getDataTable(cdSupplierInfoList);
    }


    /**
     * 新增保存供应商信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存供应商信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存供应商信息 ", response = R.class)
    public R addSave(@RequestBody CdSupplierInfo cdSupplierInfo) {
        cdSupplierInfoService.insertSelective(cdSupplierInfo);
        return R.data(cdSupplierInfo.getId());
    }

    /**
     * 修改保存供应商信息
     */
    @PostMapping("update")
    @OperLog(title = "修改保存供应商信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存供应商信息 ", response = R.class)
    public R editSave(@RequestBody CdSupplierInfo cdSupplierInfo) {
        return toAjax(cdSupplierInfoService.updateByPrimaryKeySelective(cdSupplierInfo));
    }

    /**
     * 删除供应商信息
     */
    @PostMapping("remove")
    @OperLog(title = "删除供应商信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除供应商信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdSupplierInfoService.deleteByIds(ids));
    }


    /**
     * 根据登录名查询供应商信息
     */
    @GetMapping("getByNick")
    @ApiOperation(value = "根据登录名查询供应商信息 ", response = CdSupplierInfo.class)
    public CdSupplierInfo getByNick(String loginName) {
        Example example = new Example(CdSupplierInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("delFlag", "0")
                .andEqualTo("nick", loginName);
        return cdSupplierInfoService.findByExampleOne(example);

    }

    /**
     * 根据供应商编号查供应商信息
     * @param supplierCode
     * @return
     */
    @GetMapping("selectOneBySupplierCode")
    @ApiOperation(value = "根据供应商编号查供应商信息 ", response = R .class)
    public R selectOneBySupplierCode(@RequestParam("supplierCode") String supplierCode){
        Example example = new Example(CdSupplierInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierCode",supplierCode);
        CdSupplierInfo cdSupplierInfo = cdSupplierInfoService.findByExampleOne(example);
        return R.data(cdSupplierInfo);
    }
}
