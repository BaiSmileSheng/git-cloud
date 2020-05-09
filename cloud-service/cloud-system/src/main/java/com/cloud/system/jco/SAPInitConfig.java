package com.cloud.system.jco;

import com.cloud.system.config.SAPConnConfig;
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
    private static final String ABAP_AS_SAP600 = "ABAP_AS_SAP600";


    @Autowired
    private SAPConnConfig sapConnConfig;

    public void initConfig() {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConnConfig.getAshost());//服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  sapConnConfig.getSysnr());        //系统编号
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapConnConfig.getClient());
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, sapConnConfig.getUser());
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConnConfig.getPasswd());
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, sapConnConfig.getLang());
//		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大连接数
//		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     //最大连接线程

        createDataFile(ABAP_AS_SAP600, "jcoDestination", connectProperties);
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
