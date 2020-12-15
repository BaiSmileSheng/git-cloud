package com.cloud.settle.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @auther cs  分布式生成订单号
 * @date 2020/9/11 9:43
 * @description
 */
@Slf4j
public class OrderNoGenerateUtil {

    /** 订单号生成 **/
    private static ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final AtomicInteger SEQ = new AtomicInteger(1000);
    private static final DateTimeFormatter DF_FMT_PREFIX = DateTimeFormatter.ofPattern("yyMMddHHmmssSS");
    public static String generateOrderNo(String suffix){
        LocalDateTime dataTime = LocalDateTime.now(ZONE_ID);
        if(SEQ.intValue()>9990){
            SEQ.getAndSet(1000);
        }
        return  suffix+dataTime.format(DF_FMT_PREFIX)+ getLocalIpSuffix()+SEQ.getAndIncrement();
    }

    private volatile static String IP_SUFFIX = null;
    private static String getLocalIpSuffix (){
        if(null != IP_SUFFIX){
            return IP_SUFFIX;
        }
        try {
            synchronized (OrderNoGenerateUtil.class){
                if(null != IP_SUFFIX){
                    return IP_SUFFIX;
                }
                InetAddress addr = InetAddress.getLocalHost();
                //  取服务器IP 分布式服务器ip不同
                String hostAddress = addr.getHostAddress();
                if (null != hostAddress && hostAddress.length() > 4) {
                    String ipSuffix = hostAddress.trim().split("\\.")[3];
                    if (ipSuffix.length() == 2) {
                        IP_SUFFIX = ipSuffix;
                        return IP_SUFFIX;
                    }
                    ipSuffix = "0" + ipSuffix;
                    IP_SUFFIX = ipSuffix.substring(ipSuffix.length() - 2);
                    return IP_SUFFIX;
                }
                IP_SUFFIX = RandomUtils.nextInt(10, 20) + "";
                return IP_SUFFIX;
            }
        }catch (Exception e){
            log.info("OrderNoGenerate_getLocalIpSuffix_e获取IP失败:{}", e);
            IP_SUFFIX =  RandomUtils.nextInt(10,20)+"";
            return IP_SUFFIX;
        }
    }

    /**
     * @param size 个数
     * @param suffix 前缀
     * @return
     */
    public static List<String> getOrderNos(int size,String suffix){
        List<String> orderNos = Collections.synchronizedList(new ArrayList<String>());
        IntStream.range(0,size).parallel().forEach(i->{
            orderNos.add(generateOrderNo(suffix));
        });
        List<String> filterOrderNos = orderNos.stream().distinct().collect(Collectors.toList());
        return filterOrderNos;
    }
}
