package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.constant.ExcelXmlConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductDifferenceMapper;
import com.cloud.order.domain.entity.OmsProductDifference;
import com.cloud.order.service.IOmsProductDifferenceService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 外单排产差异报表 Service业务层处理
 *
 * @author ltq
 * @date 2020-09-30
 */
@Service
@Slf4j
public class OmsProductDifferenceServiceImpl extends BaseServiceImpl<OmsProductDifference> implements IOmsProductDifferenceService {
    private static final String WEEK_TWO = "2";
    private static final String WEEK_THREE = "4";
    private static final String WEEK_SIX = "7";
    @Autowired
    private OmsProductDifferenceMapper omsProductDifferenceMapper;
    @Autowired
    private IOmsRealOrderService omsRealOrderService;
    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;
    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;

    /**
     * Description: 定时任务汇总外单排产差异报表 ，每周六执行
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/9/30
     */
    @Override
    @GlobalTransactional
    public R timeProductDiffTask() {
        //设置查询的开始日期：本周二
        Map<String,String> weekDaysMap = getTimeInterval(new Date());
        String weekTwo = weekDaysMap.get(WEEK_TWO);
        //计算当前日期的周数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.date());
        int weeks = calendar.get(Calendar.WEEK_OF_YEAR);
        //考虑定时任务执行失败，手动执行定时任务的情况，首先清除本周的数据
        Example exampleDiff = new Example(OmsProductDifference.class);
        Example.Criteria criteriaDiff = exampleDiff.createCriteria();
        criteriaDiff.andEqualTo("weeks",StrUtil.toString(weeks));
        omsProductDifferenceMapper.deleteByExample(exampleDiff);
        //1、汇总真单数据
        //生产工厂、专用号、创建时间（本周二-本周三）周二00:00 – 周三23:59
        //（1）设置获取数据的日期
        String weekThree = weekDaysMap.get(WEEK_THREE);
        //（2）按照查询日期（本周二-本周三）查询真单数据
        Example exampleReal = new Example(OmsRealOrder.class);
        Example.Criteria criteriaReal = exampleReal.createCriteria();
        criteriaReal.andGreaterThanOrEqualTo("createTime",weekTwo);
        criteriaReal.andLessThan("createTime",weekThree);
        criteriaReal.andEqualTo("orderFrom","2");
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(exampleReal);
        if (ObjectUtil.isEmpty(omsRealOrderList) && omsRealOrderList.size() <= 0) {
            log.info("无"+weekTwo+"至"+weekThree+"的外单真单数据！");
            return R.error("无"+weekTwo+"至"+weekThree+"的外单真单数据！");
        }
        //（3）根据生产工厂、专用号进行汇总
        Map<String, List<OmsRealOrder>> realGroupMap = omsRealOrderList.stream().collect(Collectors.groupingBy(o ->fetchGroupKeyReal(o)));

        //2、汇总排产订单数据
        //生产工厂、专用号、创建日期（本周二-本周六）周二00:00-周六06:00
        //（1）设置获取数据的日期
        String weekSix = weekDaysMap.get(WEEK_SIX);
        //（2）按照查询日期（本周二-本周六）查询真单数据
        Example exampleProd = new Example(OmsProductionOrder.class);
        Example.Criteria criteriaProd = exampleProd.createCriteria();
        criteriaProd.andGreaterThanOrEqualTo("createTime",weekTwo);
        criteriaReal.andLessThan("createTime",weekSix);
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(exampleProd);
        //（3）根据生产工厂、专用号进行汇总
        Map<String, List<OmsProductionOrder>> prodGroupMap = omsProductionOrderList.stream().collect(Collectors.groupingBy(o ->fetchGroupKeyProd(o)));

        //3、根据专用号查询产品类别
        List<String> materialList = omsRealOrderList.stream().map(OmsRealOrder::getProductMaterialCode).collect(Collectors.toList());
        R materialMap = remoteMaterialExtendInfoService.selectInfoInMaterialCodes(materialList);
        if (!materialMap.isSuccess()) {
           log.error("根据专用号查询物料扩展信息数据失败，原因："+materialMap.get("msg"));
           return R.error("根据专用号查询物料扩展信息数据失败，原因："+materialMap.get("msg"));
        }
        Map<String,CdMaterialExtendInfo> productTypeMap =
                materialMap.getCollectData(new TypeReference<Map<String, CdMaterialExtendInfo>>() {});

        //4、根据汇总后的真单数据和汇总后的排产订单数据进行匹配计算
        List<OmsProductDifference> omsProductDifferenceList = new ArrayList<>();
        realGroupMap.forEach((key,value) ->{
            OmsRealOrder omsRealOrder = value.get(0);
            //获取产品类别
            CdMaterialExtendInfo materialExtendInfo = productTypeMap.get(omsRealOrder.getProductMaterialCode());
            String productType = BeanUtil.isNotEmpty(materialExtendInfo) ? materialExtendInfo.getProductType() : "";
            //计算应排T+1周订单的数量
            BigDecimal realOrderNum = value.stream().map(OmsRealOrder::getOrderNum).reduce(BigDecimal.ZERO,BigDecimal::add);
            //计算实际排产数量
            List<OmsProductionOrder> omsProductionOrders = prodGroupMap.get(key);
            BigDecimal productNum = ObjectUtil.isNotEmpty(omsProductionOrders)
                    ? omsProductionOrders.stream().map(OmsProductionOrder::getProductNum)
                    .reduce(BigDecimal.ZERO,BigDecimal::add) : BigDecimal.ZERO;
            //报表创建人取排产员
            String createBy = ObjectUtil.isNotEmpty(omsProductionOrders)
                    ? omsProductionOrders.get(0).getCreateBy() : "";
            //计算差异量
            BigDecimal differenceNum = productNum.subtract(realOrderNum);
            //排产率
            String productivity = productNum.divide(realOrderNum,2,BigDecimal.ROUND_DOWN).multiply(new BigDecimal("100")) + "%";
            OmsProductDifference omsProductDifference = OmsProductDifference.builder()
                    .productFactoryCode(omsRealOrder.getProductFactoryCode())
                    .productMaterialCode(omsRealOrder.getProductMaterialCode())
                    .productType(StrUtil.isNotBlank(productType) ? productType : null)
                    .weeks(StrUtil.toString(weeks))
                    .realOrderNum(realOrderNum)
                    .productNum(productNum)
                    .differenceNum(differenceNum)
                    .productivity(productivity)
                    .delFlag("0")
                    .build();
            omsProductDifference.setCreateBy(createBy);
            omsProductDifference.setCreateTime(DateUtil.date());
            omsProductDifferenceList.add(omsProductDifference);
        });
        int insertCount = 0;
        if (ObjectUtil.isNotEmpty(omsProductDifferenceList) && omsProductDifferenceList.size() > 0) {
            insertCount = omsProductDifferenceMapper.insertList(omsProductDifferenceList);
        }
        return R.ok("新增条数："+insertCount);
    }
    /**
     *  手工导入组合key
     * @param omsRealOrder
     * @return
     */
    private static String fetchGroupKeyReal(OmsRealOrder omsRealOrder){
        return StrUtil.concat(true,
                omsRealOrder.getProductFactoryCode(),omsRealOrder.getProductMaterialCode());
    }
    /**
     *  手工导入组合key
     * @param omsProductionOrder
     * @return
     */
    private static String fetchGroupKeyProd(OmsProductionOrder omsProductionOrder){
        return StrUtil.concat(true,
                omsProductionOrder.getProductFactoryCode(),omsProductionOrder.getProductMaterialCode());
    }
    //根据当前日期获取本周
    public Map<String,String> getTimeInterval(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        String imptimeBegin = DateUtil.format(cal.getTime(), "yyyy-MM-dd");
        cal.add(Calendar.DATE, 6);
        String imptimeEnd = DateUtil.format(cal.getTime(), "yyyy-MM-dd");
        List<String> weekDays = getDays(imptimeBegin,imptimeEnd);
        Map<String,String> weekDaysMap = new HashMap<>();
        for(int i = 1; i <= weekDays.size();i++){
            weekDaysMap.put(StrUtil.toString(i),weekDays.get(i-1));
        }
        return weekDaysMap;
    }
    /**
     * Description:  获取一周的所有日期
     * Param: [startTime, endTime]
     * return: java.util.List<java.lang.String>
     * Author: ltq
     * Date: 2020/9/30
     */
    public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<>();
        Date start = DateUtil.parse(startTime);
        Date end = DateUtil.parse(endTime);

        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
        while (tempStart.before(tempEnd)) {
            days.add(DateUtil.format(tempStart.getTime(), "yyyy-MM-dd"));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        days.sort(Comparator.naturalOrder());
        return days;
    }
}
