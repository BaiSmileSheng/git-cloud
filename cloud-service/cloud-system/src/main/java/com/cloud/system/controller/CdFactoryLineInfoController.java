package com.cloud.system.controller;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.service.ICdFactoryLineInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 工厂线体关系  提供者
 *
 * @author cs
 * @date 2020-06-01
 */
@RestController
@RequestMapping("factoryLine")
@Api(tags = "工厂线体关系")
public class CdFactoryLineInfoController extends BaseController {

    @Autowired
    private ICdFactoryLineInfoService cdFactoryLineInfoService;

    /**
     * 查询工厂线体关系
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询工厂线体关系 ", response = CdFactoryLineInfo.class)
    public CdFactoryLineInfo get(Long id) {
        return cdFactoryLineInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询工厂线体关系 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "工厂线体关系 查询分页", response = CdFactoryLineInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierCode", value = "加工承揽商", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "produceLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchOffice", value = "主管", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "monitor", value = "班长", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "attribute", value = "属性", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdFactoryLineInfo cdFactoryLineInfo) {
        Example example = new Example(CdFactoryLineInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdFactoryLineInfo, criteria);
        startPage();
        example.orderBy("createTime").desc();
        List<CdFactoryLineInfo> cdFactoryLineInfoList = cdFactoryLineInfoService.selectByExample(example);
        return getDataTable(cdFactoryLineInfoList);
    }


    /**
     * 查询工厂线体关系 列表
     */
    @PostMapping("listByExample")
    @ApiOperation(value = "工厂线体关系", response = CdFactoryLineInfo.class)
    public R listByExample(CdFactoryLineInfo cdFactoryLineInfo) {
        Example example = new Example(CdFactoryLineInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdFactoryLineInfo, criteria);
        example.orderBy("createTime").desc();
        List<CdFactoryLineInfo> cdFactoryLineInfoList = cdFactoryLineInfoService.selectByExample(example);
        return R.data(cdFactoryLineInfoList);
    }

    /**
     * 分页查询条件
     * @param cdFactoryLineInfo
     * @param criteria
     */
    void listCondition(CdFactoryLineInfo cdFactoryLineInfo, Example.Criteria criteria) {
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", cdFactoryLineInfo.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getProductFactoryDesc())) {
            criteria.andLike("productFactoryDesc", "%"+cdFactoryLineInfo.getProductFactoryDesc()+"%");
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", cdFactoryLineInfo.getSupplierCode());
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getSupplierDesc())) {
            criteria.andLike("supplierDesc", "%"+cdFactoryLineInfo.getSupplierDesc()+"%");
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getProduceLineCode())) {
            criteria.andEqualTo("produceLineCode", cdFactoryLineInfo.getProduceLineCode());
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getProduceLineDesc())) {
            criteria.andLike("produceLineDesc", "%"+cdFactoryLineInfo.getProduceLineDesc()+"%");
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getBranchOffice())) {
            criteria.andLike("branchOffice", "%"+cdFactoryLineInfo.getBranchOffice()+"%");
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getMonitor())) {
            criteria.andLike("monitor", "%"+cdFactoryLineInfo.getMonitor()+"%");
        }
        if (StringUtils.isNotBlank(cdFactoryLineInfo.getAttribute())) {
            criteria.andEqualTo("attribute", cdFactoryLineInfo.getAttribute());
        }
    }

    /**
     * 新增保存工厂线体关系
     */
    @PostMapping("save")
    @OperLog(title = "新增保存工厂线体关系 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存工厂线体关系 ", response = R.class)
    public R addSave(@RequestBody CdFactoryLineInfo cdFactoryLineInfo) {
        cdFactoryLineInfoService.insertSelective(cdFactoryLineInfo);
        return R.data(cdFactoryLineInfo.getId());
    }

    /**
     * 修改保存工厂线体关系
     */
    @PostMapping("update")
    @OperLog(title = "修改保存工厂线体关系 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存工厂线体关系 ", response = R.class)
    public R editSave(@RequestBody CdFactoryLineInfo cdFactoryLineInfo) {
        return toAjax(cdFactoryLineInfoService.updateByPrimaryKeySelective(cdFactoryLineInfo));
    }

    /**
     * 删除工厂线体关系
     */
    @PostMapping("remove")
    @OperLog(title = "删除工厂线体关系 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除工厂线体关系 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdFactoryLineInfoService.deleteByIds(ids));
    }

    /**
     * 根据供应商编号查询线体
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    @PostMapping("selectLineCodeBySupplierCode")
    @ApiOperation(value = "根据供应商编号查询线体", response = CdFactoryLineInfo.class)
    public R selectLineCodeBySupplierCode(String supplierCode) {
        return cdFactoryLineInfoService.selectLineCodeBySupplierCode(supplierCode);
    }

    /**
     * 根据线体查询信息
     * @param produceLineCode
     * @param factoryCode
     * @return CdFactoryLineInfo
     */
    @PostMapping("selectInfoByCodeLineCode")
    @ApiOperation(value = "根据线体查询信息", response = CdFactoryLineInfo.class)
    public R selectInfoByCodeLineCode(String produceLineCode,String factoryCode) {
        return R.dataWithPrefix(cdFactoryLineInfoService.selectInfoByCodeLineCode(produceLineCode,factoryCode),"工厂线体关系");
    }
    /**
     * @Description: 获取SAP系统工厂线体关系数据，保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @PostMapping("saveFactoryLineInfo")
    @ApiOperation(value = "获取SAP系统工厂线体关系数据，保存", response = R.class)
    public R saveFactoryLineInfo(){
        return  cdFactoryLineInfoService.saveFactoryLineInfo();
    }

    /**
     * Description:  根据List<Map<String,String>>工厂、线体查询线体信息
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @PostMapping("selectListByMapList")
    @ApiOperation(value = "根据List<Map<String,String>>工厂、线体查询线体信息", response = R.class)
    public R selectListByMapList(@RequestBody List<Dict> list){
        return cdFactoryLineInfoService.selectListByMapList(list);
    }
}
