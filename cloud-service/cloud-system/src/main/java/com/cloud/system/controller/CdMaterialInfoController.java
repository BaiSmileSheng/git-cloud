package com.cloud.system.controller;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.*;
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
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 物料信息  提供者
 *
 * @author ltq
 * @date 2020-06-01
 */
@RestController
@RequestMapping("material")
public class CdMaterialInfoController extends BaseController {

    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    /**
     * 查询物料信息 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询物料信息 ", response = CdMaterialInfo.class)
    public CdMaterialInfo get(Long id) {
        return cdMaterialInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询物料信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "物料信息 查询分页", response = CdMaterialInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdMaterialInfo cdMaterialInfo) {
        Example example = new Example(CdMaterialInfo.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoService.selectByExample(example);
        return getDataTable(cdMaterialInfoList);
    }


    /**
     * 新增保存物料信息 
     */
    @PostMapping("save")
    @OperLog(title = "新增保存物料信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存物料信息 ", response = R.class)
    public R addSave(@RequestBody CdMaterialInfo cdMaterialInfo) {
        cdMaterialInfoService.insertSelective(cdMaterialInfo);
        return R.data(cdMaterialInfo.getId());
    }

    /**
     * 修改保存物料信息 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存物料信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物料信息 ", response = R.class)
    public R editSave(@RequestBody CdMaterialInfo cdMaterialInfo) {
        return toAjax(cdMaterialInfoService.updateByPrimaryKeySelective(cdMaterialInfo));
    }

    /**
     * 删除物料信息 
     */
    @PostMapping("remove")
    @OperLog(title = "删除物料信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除物料信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdMaterialInfoService.deleteByIds(ids));
    }

    /**
     * 获取MDM系统物料信息数据并保存
     */
    @PostMapping("saveMaterialInit")
    @OperLog(title = "保存接口获取的物料信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "保存接口获取的物料信息 ", response = R.class)
    public R saveMaterialInfo(){return cdMaterialInfoService.saveMaterialInfo();}

}
