package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import cn.hutool.core.lang.Dict;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.pl.REGON;
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
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 物料扩展信息  提供者
 *
 * @author lihongia
 * @date 2020-06-15
 */
@RestController
@Api(tags = "物料扩展信息  提供者")
@RequestMapping("materialExtendInfo")
public class CdMaterialExtendInfoController extends BaseController {

    @Autowired
    private ICdMaterialExtendInfoService cdMaterialExtendInfoService;

    /**
     * 查询物料扩展信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询物料扩展信息 ", response = CdMaterialExtendInfo.class)
    public CdMaterialExtendInfo get(Long id) {
        return cdMaterialExtendInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询物料扩展信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "物料扩展信息 查询分页", response = CdMaterialExtendInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdMaterialExtendInfo cdMaterialExtendInfo) {
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        return getDataTable(cdMaterialExtendInfoList);
    }


    /**
     * 新增保存物料扩展信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存物料扩展信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存物料扩展信息 ", response = R.class)
    public R addSave(@RequestBody CdMaterialExtendInfo cdMaterialExtendInfo) {
        cdMaterialExtendInfoService.insertSelective(cdMaterialExtendInfo);
        return R.data(cdMaterialExtendInfo.getId());
    }

    /**
     * 修改保存物料扩展信息
     */
    @PostMapping("update")
    @OperLog(title = "修改保存物料扩展信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物料扩展信息 ", response = R.class)
    public R editSave(@RequestBody CdMaterialExtendInfo cdMaterialExtendInfo) {
        return toAjax(cdMaterialExtendInfoService.updateByPrimaryKeySelective(cdMaterialExtendInfo));
    }

    /**
     * 删除物料扩展信息
     */
    @PostMapping("remove")
    @OperLog(title = "删除物料扩展信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物料扩展信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdMaterialExtendInfoService.deleteByIds(ids));
    }

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    @PostMapping("timeSycProductStock")
    @OperLog(title = "定时任务传输成品物料接口 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时任务传输成品物料接口 ", response = R.class)
    public R timeSycMaterialCode() {
        return cdMaterialExtendInfoService.timeSycMaterialCode();
    }
    /**
     * 根据多个成品专用号查询
     *
     * @return
     */
    @PostMapping("selectByMaterialList")
    @ApiOperation(value = "根据多个成品专用号查询 ", response = R.class)
    public R selectByMaterialList(@RequestBody List<Dict> list){
        return cdMaterialExtendInfoService.selectByMaterialCodeList(list);
    }

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
    @GetMapping("selectMaterialCodeByLifeCycle")
    public R selectMaterialCodeByLifeCycle(@RequestParam("lifeCycle") String lifeCycle){
        return cdMaterialExtendInfoService.selectMaterialCodeByLifeCycle(lifeCycle);
    }

    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @PostMapping("selectInfoInMaterialCodes")
    public R selectInfoInMaterialCodes(@RequestBody List<String> materialCodes) {
        return cdMaterialExtendInfoService.selectInfoInMaterialCodes(materialCodes);
    }
    /**
     * 根据物料查询一条记录
     * @param materialCode
     * @return
     */
    @PostMapping("selectOneByMaterialCode")
    public R selectOneByMaterialCode(@RequestParam("materialCode") String materialCode){
        return cdMaterialExtendInfoService.selectOneByMaterialCode(materialCode);
    }
}
