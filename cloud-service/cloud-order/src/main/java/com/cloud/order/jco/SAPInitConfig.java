package com.cloud.order.jco;


import com.cloud.order.config.SAP600ConnConfig;
import com.cloud.order.config.SAP800ConnConfig;
import com.sap.conn.jco.ext.DestinationDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;


/**
 * @auther cs
 * @date 2020/5/9 9:31
 * @description 项目启动时初始化SAP配置  生成配置文件
 */
@Slf4j
@Component
public class SAPInitConfig implements CommandLineRunner {
    private static final String ABAP_AS_SAP800 = "ABAP_AS_SAP800";
    private static final String ABAP_AS_SAP600 = "ABAP_AS_SAP600";


    @Autowired
    private SAP800ConnConfig sap800ConnConfig;
    @Autowired
    private SAP600ConnConfig sap600ConnConfig;

    public void initConfig() {
        Properties connectProperties800 = new Properties();
        connectProperties800.setProperty(DestinationDataProvider.JCO_ASHOST, sap800ConnConfig.getAshost());//服务器
        connectProperties800.setProperty(DestinationDataProvider.JCO_SYSNR,  sap800ConnConfig.getSysnr());        //系统编号
        connectProperties800.setProperty(DestinationDataProvider.JCO_CLIENT, sap800ConnConfig.getClient());
        connectProperties800.setProperty(DestinationDataProvider.JCO_USER, sap800ConnConfig.getUser());
        connectProperties800.setProperty(DestinationDataProvider.JCO_PASSWD, sap800ConnConfig.getPasswd());
        connectProperties800.setProperty(DestinationDataProvider.JCO_LANG, sap800ConnConfig.getLang());

        Properties connectProperties600 = new Properties();
        connectProperties600.setProperty(DestinationDataProvider.JCO_ASHOST, sap600ConnConfig.getAshost());//服务器
        connectProperties600.setProperty(DestinationDataProvider.JCO_SYSNR,  sap600ConnConfig.getSysnr());        //系统编号
        connectProperties600.setProperty(DestinationDataProvider.JCO_CLIENT, sap600ConnConfig.getClient());
        connectProperties600.setProperty(DestinationDataProvider.JCO_USER, sap600ConnConfig.getUser());
        connectProperties600.setProperty(DestinationDataProvider.JCO_PASSWD, sap600ConnConfig.getPasswd());
        connectProperties600.setProperty(DestinationDataProvider.JCO_LANG, sap600ConnConfig.getLang());
//		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大连接数
//		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     //最大连接线程

        createDataFile(ABAP_AS_SAP800, "jcoDestination", connectProperties800);
        createDataFile(ABAP_AS_SAP600,"jcoDestination",connectProperties600);
    }

    /**
     * 创建SAP接口属性文件。
     * @param name	ABAP管道名称
     * @param suffix	属性文件后缀
     * @param properties	属性文件内容
     */
    private static void createDataFile(String name, String suffix, Properties properties){
        File cfg = new File(name+"."+suffix);
        if(cfg.exists()){
			cfg.deleteOnExit();
        }
        try{
            FileOutputStream fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for connection !");
            fos.close();
        }catch (Exception e){
            log.error("Create Data file fault, error msg: " + e.toString());
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        initConfig();
    }
}
