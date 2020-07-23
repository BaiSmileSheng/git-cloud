package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import cn.hutool.core.lang.Dict;
import com.cloud.system.util.MaterialExtendInfoWriteHandler;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdMaterialExtendInfoExportVo;
import com.cloud.system.domain.vo.CdMaterialExtendInfoImportVo;
import com.cloud.system.util.EasyExcelUtilOSS;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类别", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lifeCycle", value = "生命周期", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdMaterialExtendInfo cdMaterialExtendInfo) {
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdMaterialExtendInfo,criteria);
        startPage();
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        return getDataTable(cdMaterialExtendInfoList);
    }

    /**
     * 查询所有的专用号
     */
    @GetMapping("listCode")
    @ApiOperation(value = "查询所有的专用号", response = CdMaterialExtendInfo.class)
    public R listCode(){
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        List<String> list = cdMaterialExtendInfoList.stream().map(m ->m.getMaterialCode()).collect(Collectors.toList());
        return R.data(list);
    }
    /**
     * 组装条件
     * @param cdMaterialExtendInfo
     * @param criteria
     */
    private void listCondition(CdMaterialExtendInfo cdMaterialExtendInfo,Example.Criteria criteria){
        if(StringUtils.isNotBlank(cdMaterialExtendInfo.getMaterialCode())){
            criteria.andEqualTo("materialCode",cdMaterialExtendInfo.getMaterialCode());
        }
        if(StringUtils.isNotBlank(cdMaterialExtendInfo.getProductType())){
            criteria.andEqualTo("productType",cdMaterialExtendInfo.getProductType());
        }
        if(StringUtils.isNotBlank(cdMaterialExtendInfo.getLifeCycle())){
            criteria.andEqualTo("lifeCycle",cdMaterialExtendInfo.getLifeCycle());
        }
    }
    /**
     * 下载模板
     */
    @HasPermissions("sys:materialExtendInfo:downLoadTemplate")
    @GetMapping("downLoadTemplate")
    @ApiOperation(value = "下载模板")
    public R downLoadTemplate(){
        String fileName = "下载模板.xlsx";
        return EasyExcelUtilOSS.writePostilExcel(Arrays.asList(),fileName,fileName,new CdMaterialExtendInfoImportVo(),new MaterialExtendInfoWriteHandler());
    }

    /**
     * 导出
     */
    @HasPermissions("sys:materialExtendInfo:export")
    @GetMapping("export")
    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类别", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lifeCycle", value = "生命周期", required = false, paramType = "query", dataType = "String")
    })
    public R export(@ApiIgnore CdMaterialExtendInfo cdMaterialExtendInfo){
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdMaterialExtendInfo,criteria);
        String fileName = "成品物料信息.xlsx";
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(cdMaterialExtendInfoList,fileName,fileName,new CdMaterialExtendInfoExportVo());
    }

    /**
     * 导入
     */
    @HasPermissions("sys:materialExtendInfo:importMaterialExtendInfo")
    @PostMapping("importMaterialExtendInfo")
    @ApiOperation(value = "导入")
    public R importMaterialExtendInfo(@RequestParam("file") MultipartFile file)throws IOException {
        SysUser sysUser = getUserInfo(SysUser.class);
        return cdMaterialExtendInfoService.importMaterialExtendInfo(file,sysUser);
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
    @HasPermissions("sys:materialExtendInfo:update")
    @PostMapping("update")
    @OperLog(title = "修改保存物料扩展信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存物料扩展信息 ", response = R.class)
    public R editSave(@RequestBody CdMaterialExtendInfo cdMaterialExtendInfo) {
        SysUser sysUser = getUserInfo(SysUser.class);
        cdMaterialExtendInfo.setUpdateBy(sysUser.getLoginName());
        return toAjax(cdMaterialExtendInfoService.updateByPrimaryKeySelective(cdMaterialExtendInfo));
    }

    /**
     * 删除物料扩展信息
     */
    @HasPermissions("sys:materialExtendInfo:remove")
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
    @PostMapping("timeSycMaterialCode")
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

    /**
     * 模糊查询专用号
     */
    @GetMapping("selectByLikeCode")
    @ApiOperation(value = "模糊查询专用号", response = CdMaterialExtendInfo.class)
    public R selectByLikeCode(String materialCode){
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("materialCode",materialCode + "%");
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        List<String> list = cdMaterialExtendInfoList.stream().map(m ->m.getMaterialCode()).collect(Collectors.toList());
        return R.data(list);
    }
}
