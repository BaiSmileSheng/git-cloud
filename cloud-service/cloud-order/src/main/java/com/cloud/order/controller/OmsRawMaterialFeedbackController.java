package com.cloud.order.controller;
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
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 原材料反馈信息  提供者
 *
 * @author ltq
 * @date 2020-06-22
 */
@RestController
@RequestMapping("feedback")
public class OmsRawMaterialFeedbackController extends BaseController {

    @Autowired
    private IOmsRawMaterialFeedbackService omsRawMaterialFeedbackService;

    /**
     * 查询原材料反馈信息 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询原材料反馈信息 ", response = OmsRawMaterialFeedback.class)
    public OmsRawMaterialFeedback get(Long id) {
        return omsRawMaterialFeedbackService.selectByPrimaryKey(id);

    }

    /**
     * 查询原材料反馈信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "原材料反馈信息 查询分页", response = OmsRawMaterialFeedback.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsRawMaterialFeedback omsRawMaterialFeedback) {
        Example example = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsRawMaterialFeedback> omsRawMaterialFeedbackList = omsRawMaterialFeedbackService.selectByExample(example);
        return getDataTable(omsRawMaterialFeedbackList);
    }


    /**
     * 新增保存原材料反馈信息 
     */
    @PostMapping("save")
    @OperLog(title = "新增保存原材料反馈信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存原材料反馈信息 ", response = R.class)
    public R addSave(@RequestBody OmsRawMaterialFeedback omsRawMaterialFeedback) {
        omsRawMaterialFeedbackService.insertSelective(omsRawMaterialFeedback);
        return R.data(omsRawMaterialFeedback.getId());
    }

    /**
     * 修改保存原材料反馈信息 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存原材料反馈信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存原材料反馈信息 ", response = R.class)
    public R editSave(@RequestBody OmsRawMaterialFeedback omsRawMaterialFeedback) {
        return toAjax(omsRawMaterialFeedbackService.updateByPrimaryKeySelective(omsRawMaterialFeedback));
    }

    /**
     * 删除原材料反馈信息 
     */
    @PostMapping("remove")
    @OperLog(title = "删除原材料反馈信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除原材料反馈信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsRawMaterialFeedbackService.deleteByIds(ids));
    }

}
