package com.cloud.order.controller;

import cn.hutool.core.collection.CollUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.service.IOmsInternalOrderResService;
import com.cloud.order.service.IOrderFromSap800InterfaceService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
/**
 * 内单PR/PO原  提供者
 *
 * @author ltq
 * @date 2020-06-05
 */
@RestController
@RequestMapping("demand")
public class OmsInternalOrderResController extends BaseController {

    @Autowired
    private IOmsInternalOrderResService omsInternalOrderResService;

    @Autowired
    private IOrderFromSap800InterfaceService orderFromSap800InterfaceService;

    /**
     * 查询内单PR/PO原
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询内单PR/PO原 ", response = OmsInternalOrderRes.class)
    public OmsInternalOrderRes get(Long id) {
        return omsInternalOrderResService.selectByPrimaryKey(id);

    }

    /**
     * 查询内单PR/PO原 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "内单PR/PO原 查询分页", response = OmsInternalOrderRes.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsInternalOrderRes omsInternalOrderRes) {
        Example example = new Example(OmsInternalOrderRes.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsInternalOrderRes> omsInternalOrderResList = omsInternalOrderResService.selectByExample(example);
        return getDataTable(omsInternalOrderResList);
    }


    /**
     * 新增保存内单PR/PO原
     */
    @PostMapping("save")
    @OperLog(title = "新增保存内单PR/PO原 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存内单PR/PO原 ", response = R.class)
    public R addSave(@RequestBody OmsInternalOrderRes omsInternalOrderRes) {
        omsInternalOrderResService.insertSelective(omsInternalOrderRes);
        return R.data(omsInternalOrderRes.getId());
    }

    /**
     * 修改保存内单PR/PO原
     */
    @PostMapping("update")
    @OperLog(title = "修改保存内单PR/PO原 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存内单PR/PO原 ", response = R.class)
    public R editSave(@RequestBody OmsInternalOrderRes omsInternalOrderRes) {
        return toAjax(omsInternalOrderResService.updateByPrimaryKeySelective(omsInternalOrderRes));
    }

    /**
     * 删除内单PR/PO原
     */
    @PostMapping("remove")
    @OperLog(title = "删除内单PR/PO原 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除内单PR/PO原 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsInternalOrderResService.deleteByIds(ids));
    }

    @GetMapping("queryAndInsertDemandPRFromSap800")
    @ApiOperation(value = "根据时间从800获取PR", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "开始时间", required = true,paramType = "query", dataType = "date"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", required = true,paramType = "query", dataType = "date")
    })
    public R queryAndInsertDemandPRFromSap800(Date startDate, Date endDate){
        //删除原有的PR数据
        omsInternalOrderResService.deleteByMarker("PR");

        //从SAP800获取PR数据
        R prR = orderFromSap800InterfaceService.queryDemandPRFromSap800(startDate,endDate);
        if (!prR.isSuccess()) {
            return prR;
        }
        List<OmsInternalOrderRes> list = (List<OmsInternalOrderRes>) prR.getObj("data");
        if (CollUtil.isEmpty(list)) {
            return R.error("未取到PR数据！");
        }
        //插入
        R rInsert = omsInternalOrderResService.insert800PR(list);
        return rInsert;
    }

}
