package com.cloud.system.controller;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.service.ICdMaterialInfoService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 物料信息  提供者
 *
 * @author ltq
 * @date 2020-06-01
 */
@RestController
@RequestMapping("material")
@Api(tags = "物料主数据信息")
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
     * @Description: 查询所有有效的物料数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @PostMapping("selectListByDelFlag")
    @ApiOperation(value = "查询所有有效的物料数据 ", response = R.class)
    public R selectListByDelFlag(){
        List<CdMaterialInfo> cdMaterialInfos = cdMaterialInfoService.select(CdMaterialInfo.builder().delFlag("0").build());
        return R.data(cdMaterialInfos);
    }

    @PostMapping("selectListByMaterialInfo")
    @ApiOperation(value = "根据条件查询List ", response = R.class)
    public R selectListByDelFlag(@RequestBody CdMaterialInfo cdMaterialInfo){
        List<CdMaterialInfo> cdMaterialInfos = cdMaterialInfoService.select(cdMaterialInfo);
        return R.data(cdMaterialInfos);
    }

    /**
     * 获取MDM系统物料信息数据并保存
     */
    @PostMapping("saveMaterialInit")
    @ApiOperation(value = "保存接口获取的物料信息 ", response = R.class)
    public R saveMaterialInfo(){return cdMaterialInfoService.saveMaterialInfo();}

    /**
     * 根据物料号查询一条物料信息(多条取一条)
     * @param materialCode
     * @return
     */
    @GetMapping("getByMaterialCode")
    @ApiOperation(value = "根据物料号查询物料信息 ", response = CdMaterialInfo.class)
    public R getByMaterialCode(@RequestParam("materialCode") String materialCode,@RequestParam("factoryCode") String factoryCode) {
        Example example = new Example(CdMaterialInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("materialCode", materialCode);
        criteria.andEqualTo("plantCode", factoryCode);
        List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoService.selectByExample(example);
        if(CollectionUtils.isEmpty(cdMaterialInfoList)){
            return R.error("物料信息不存在,请检查数据");
        }
        CdMaterialInfo cdMaterialInfo = cdMaterialInfoList.get(0);
        return R.data(cdMaterialInfo);
    }


    /**
     * 更新SAP获取的UPH数据
     */
    @PostMapping("updateUphBySap")
    @ApiOperation(value = "更新SAP获取的UPH数据 ", response = R.class)
    public R updateUphBySap(){
        return cdMaterialInfoService.updateUphBySap();
    }

    /**
     * 根据物料号集合查询物料信息
     * @param materialCodes
     * @param materialType
     * @return
     */
    @PostMapping("selectInfoByInMaterialCodeAndMaterialType")
    public R selectInfoByInMaterialCodeAndMaterialType(@RequestBody List<String> materialCodes,@RequestParam(value = "materialType")String materialType) {
        return cdMaterialInfoService.selectInfoByInMaterialCodeAndMaterialType(materialCodes,materialType);
    }
    /**
     * Description:  根据成品专用号、生产工厂、物料类型查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @PostMapping("selectListByMaterialList")
    @ApiOperation(value = "根据成品专用号、生产工厂、物料类型查询 ", response = R.class)
    public R selectListByMaterialList(@RequestBody List<Dict> list){
        return cdMaterialInfoService.selectListByMaterialList(list);
    }

    /**
     * 按物料号模糊查询物料信息 分页
     */
    @GetMapping("listByMaterialCode")
    @ApiOperation(value = "按物料号模糊查询物料信息 分页", response = CdMaterialInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "物料号", required = false,paramType = "query", dataType = "String")
    })
    public R listByMaterialCode(@RequestParam(value = "materialCode",required = false) String materialCode) {
        startPage();
        R result = cdMaterialInfoService.selectByMaterialCode(materialCode);
        return result;
    }

    /**
     * 按物料号查物料数据
     * @param list
     * @return
     */
    @PostMapping("selectListByMaterialCodeList")
    @ApiOperation(value = "根据成品专用号、生产工厂、物料类型查询 ", response = R.class)
    public R selectListByMaterialCodeList(@RequestBody List<Dict> list){
        return cdMaterialInfoService.selectListByMaterialCodeList(list);
    }
}
