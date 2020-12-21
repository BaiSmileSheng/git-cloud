package com.cloud.settle.controller;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.service.ISmsQualityScrapOrderLogService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 质量部报废申诉 提供者
 *
 * @author ltq
 * @date 2020-12-18
 */
@RestController
@RequestMapping("qualityScrapOrderLog")
@Api(tags = "质量部报废申诉")
public class SmsQualityScrapOrderLogController extends BaseController {

    @Autowired
    private ISmsQualityScrapOrderLogService smsQualityScrapOrderLogService;

    /**
     * 查询质量部报废申诉
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询质量部报废申诉", response = SmsQualityScrapOrderLog.class)
    public R get(Long id) {
        return R.data(smsQualityScrapOrderLogService.selectByPrimaryKey(id));

    }

    /**
     * 查询质量部报废申诉列表
     */
    @GetMapping("list")
    @ApiOperation(value = "质量部报废申诉查询分页", response = SmsQualityScrapOrderLog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsQualityScrapOrderLog smsQualityScrapOrderLog) {
        Example example = new Example(SmsQualityScrapOrderLog.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SmsQualityScrapOrderLog> smsQualityScrapOrderLogList = smsQualityScrapOrderLogService.selectByExample(example);
        return getDataTable(smsQualityScrapOrderLogList);
    }


    /**
     * 新增保存质量部报废申诉
     */
    @PostMapping("save")
    @OperLog(title = "新增保存质量部报废申诉", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存质量部报废申诉", response = R.class)
    public R addSave(@RequestBody SmsQualityScrapOrderLog smsQualityScrapOrderLog) {
        smsQualityScrapOrderLogService.insertSelective(smsQualityScrapOrderLog);
        return R.data(smsQualityScrapOrderLog.getId());
    }

    /**
     * 修改保存质量部报废申诉
     */
    @PostMapping("update")
    @OperLog(title = "修改保存质量部报废申诉", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存质量部报废申诉", response = R.class)
    public R editSave(@RequestBody SmsQualityScrapOrderLog smsQualityScrapOrderLog) {
        return toAjax(smsQualityScrapOrderLogService.updateByPrimaryKeySelective(smsQualityScrapOrderLog));
    }

    /**
     * 删除质量部报废申诉
     */
    @PostMapping("remove")
    @OperLog(title = "删除质量部报废申诉", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除质量部报废申诉", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsQualityScrapOrderLogService.deleteByIds(ids));
    }
    /**
     * 根据报废ID查询质量部报废申诉记录
     */
    @GetMapping("getByQualityId")
    @ApiOperation(value = "根据报废ID查询质量部报废申诉", response = SmsQualityScrapOrderLog.class)
    public R getByQualityId(@RequestParam(value = "qualityId") Long qualityId){
        Example example = new Example(SmsQualityScrapOrderLog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qualityId",qualityId);
        example.orderBy("procNo").asc();
        return R.data(smsQualityScrapOrderLogService.selectByExample(example));

    }

}
