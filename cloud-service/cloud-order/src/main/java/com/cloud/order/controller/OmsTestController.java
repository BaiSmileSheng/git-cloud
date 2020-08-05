package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsTest;
import com.cloud.order.service.IOmsTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 订单类测试 提供者
 *
 * @author cloud
 * @date 2020-05-02
 */
@RestController
@RequestMapping("test")
@Api(tags = "测试seata")
public class OmsTestController extends BaseController {

    @Autowired
    private IOmsTestService omsTestService;

    @GetMapping("seata")
    @ApiOperation(value = "测试seata")
    @OperLog(title = "测试seata", businessType = BusinessType.INSERT)
    public void seata() {
        omsTestService.updateTest();
    }

    /**
     * 查询${tableComment}
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询${tableComment}", response = OmsTest.class)
    public OmsTest get(Long id) {
        return omsTestService.selectByPrimaryKey(id);

    }

    /**
     * 查询订单类测试列表
     */
    @GetMapping("list")
    @ApiOperation(value = "${tableComment}查询分页", response = OmsTest.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsTest omsTest) {
        Example example = new Example(OmsTest.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsTest> omsTestList = omsTestService.selectByExample(example);
        return getDataTable(omsTestList);
    }


    /**
     * 新增保存订单类测试
     */
    @PostMapping("save")
    @OperLog(title = "新增保存订单类测试", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存订单类测试", response = OmsTest.class)
    public R addSave(@RequestBody OmsTest omsTest) {
        return toAjax(omsTestService.insertUseGeneratedKeys(omsTest));
    }

    /**
     * 修改保存订单类测试
     */
    @PostMapping("update")
    @OperLog(title = "修改保存订单类测试", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存订单类测试", response = OmsTest.class)
    public R editSave(@RequestBody OmsTest omsTest) {
        return toAjax(omsTestService.updateByPrimaryKeySelective(omsTest));
    }

    /**
     * 删除${tableComment}
     */
    @PostMapping("remove")
    @OperLog(title = "删除${tableComment}", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除${tableComment}", response = OmsTest.class)
    public R remove(String ids) {
        return toAjax(omsTestService.deleteByIds(ids));
    }

}
