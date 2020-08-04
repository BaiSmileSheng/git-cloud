package com.cloud.common.utils;

import com.cloud.common.exception.BusinessException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 *
 * @author cloud
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String MM_dd = "MM/dd";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static final long DAY = 24 * 60 * 60 * 1000L;

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 将String类型转换为Date，根据自定义模式
     */
    public static Date string2Date(String date, String pattern) {
        DateFormat dataformat = new SimpleDateFormat(pattern);
        dataformat.setLenient(false);
        try {
            return dataformat.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String dateFormat(Date date, String format) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(format);
        sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));// 设置时区
        return sdf1.format(date);
    }

    /**
     * 获取当前时间的n天后时间
     */
    public static Timestamp getDaysTime(int date) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH, date);
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        String nowTime = df.format(now.getTime());
        Timestamp buydate = Timestamp.valueOf(nowTime);
        return buydate;
    }

    /**
     * 获取当前时间的n天后时间 YYYY_MM_DD
     */
    public static String getDaysTimeString(int date) {
        Timestamp timestampDate = getDaysTime(date);
        String dateString = new SimpleDateFormat(YYYY_MM_DD).format(timestampDate);
        return dateString;
    }

    /**
     * 时间转换, Date转换成 XMLGregorianCalendar
     * @param date
     * @return
     * @throws Exception
     */
    public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try{
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("日期转换异常");
        }

        return gc;
    }

    /**
     * XMLGregorianCalendar 转Date
     * @param cal
     * @return
     */
    public  static Date convertToDate(XMLGregorianCalendar cal){
        GregorianCalendar ca = null;
        if(cal!=null && cal.toGregorianCalendar()!=null){
            ca = cal.toGregorianCalendar();
            return ca.getTime();
        }
        return null;
    }
    /**
     * 时间差值 天数
     * @param date1
     * @param date2
     * @return
     */
    public static int dayDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / DAY);
    }

    /**
     * 时间差值 天数
     * @param dateStr1
     * @param dateStr2
     * @param pattern  时间类型
     * @return
     */
    public static int dayDiffSt(String dateStr1, String dateStr2,String pattern) {
        Date date1 = string2Date(dateStr1,pattern);
        Date date2 = string2Date(dateStr2,pattern);
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / DAY);
    }

    /**
     * 根据year年的第week周，查询week周的起止时间
     * @param year
     * @param week
     * @return startDate开始时间 endDate结束时间
     */
    public static Map<String,String> weekToDayFormate(int year, int week){
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        // ①.设置该年份的开始日期：第一个月的第一天
        calendar.set(year,0,1);
        // ②.计算出第一周还剩几天：+1是因为1号是1天
        int dayOfWeek = 7 - calendar.get(Calendar.DAY_OF_WEEK) + 1;
        // ③.周数减去第一周再减去要得到的周
        week = week - 2;
        // ④.计算起止日期
        calendar.add(Calendar.DAY_OF_YEAR,week * 7 + dayOfWeek);
        map.put("startDate",new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        map.put("endDate",new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        return map;
    }

    /**
     * 获取n个月后时间
     */
    public static Timestamp getMonthTime(int month) {
        Date dt = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(dt);
        now.add(Calendar.MONTH, month);
        Date threeMonthAgoDate = now.getTime();
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        String nowTime = df.format(threeMonthAgoDate);
        Timestamp buydate = Timestamp.valueOf(nowTime);
        return buydate;
    }

    /**
     * 返回指定天数位移后的日期
     */
    public static String dayOffset(String dateString, int offset,String paramFormat){
        Date date = string2Date(dateString,paramFormat);
        Date dateResult = dayOffset(date,offset);
        SimpleDateFormat sdf1 = new SimpleDateFormat(paramFormat);
        String dateResultString = sdf1.format(dateResult);
        return dateResultString;
    }


    /**
     * 返回指定天数位移后的日期
     */
    public static Date dayOffset(Date date, int offset) {

        return offsetDate(date, Calendar.DATE, offset);
    }

    /**
     * 返回指定日期相应位移后的日期
     *
     * @param date
     *            参考日期
     * @param field
     *            位移单位，见 {@link Calendar}
     * @param offset
     *            位移数量，正数表示之后的时间，负数表示之前的时间
     * @return 位移后的日期
     */
    public static Date offsetDate(Date date, int field, int offset) {
        Calendar calendar = convert(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    /**
     * 获取 Calendar 类型时间
     * @param date
     * @return
     */
    private static Calendar convert(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 获取n个月后第一天时间
     */
    public static String getMonthFirstTime(int month) {
        SimpleDateFormat format=new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = format.format(calendar.getTime())+" 00:00:00";
        return firstDay;
    }
}
