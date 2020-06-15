package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 工厂库位  提供者
 *
 * @author cs
 * @date 2020-06-15
 */
@RestController
@RequestMapping("factoryStorehouse")
public class CdFactoryStorehouseInfoController extends BaseController {

    @Autowired
    private ICdFactoryStorehouseInfoService cdFactoryStorehouseInfoService;

    /**
     * 查询工厂库位
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询工厂库位 ", response = CdFactoryStorehouseInfo.class)
    public CdFactoryStorehouseInfo get(Long id) {
        return cdFactoryStorehouseInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询工厂库位 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "工厂库位 查询分页", response = CdFactoryStorehouseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        Example example = new Example(CdFactoryStorehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdFactoryStorehouseInfo> cdFactoryStorehouseInfoList = cdFactoryStorehouseInfoService.selectByExample(example);
        return getDataTable(cdFactoryStorehouseInfoList);
    }


    /**
     * 新增保存工厂库位
     */
    @PostMapping("save")
    @OperLog(title = "新增保存工厂库位 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存工厂库位 ", response = R.class)
    public R addSave(@RequestBody CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        cdFactoryStorehouseInfoService.insertSelective(cdFactoryStorehouseInfo);
        return R.data(cdFactoryStorehouseInfo.getId());
    }

    /**
     * 修改保存工厂库位
     */
    @PostMapping("update")
    @OperLog(title = "修改保存工厂库位 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存工厂库位 ", response = R.class)
    public R editSave(@RequestBody CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        return toAjax(cdFactoryStorehouseInfoService.updateByPrimaryKeySelective(cdFactoryStorehouseInfo));
    }

    /**
     * 删除工厂库位
     */
    @PostMapping("remove")
    @OperLog(title = "删除工厂库位 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除工厂库位 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdFactoryStorehouseInfoService.deleteByIds(ids));
    }

}
