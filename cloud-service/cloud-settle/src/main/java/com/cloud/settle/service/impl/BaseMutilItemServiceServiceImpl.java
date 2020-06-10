package com.cloud.settle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.settle.service.IBaseMutilItemService;
import com.cloud.settle.webService.gems.IfBaseClaimService;
import com.cloud.system.feign.RemoteDictDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Map;

/**
 * 报账单创建接口
 *
 * @author Lihongxia
 * @date 2020-06-09
 */
@Service
public class BaseMutilItemServiceServiceImpl implements IBaseMutilItemService {

    private static Logger logger = LoggerFactory.getLogger(BaseMutilItemServiceServiceImpl.class);

    @Autowired
    private RemoteDictDataService remoteDictDataService;

    @Value("${webService.baseMultiItemClaim.urlClaim}")
    private String urlClaim;

    @Value("${webService.baseMultiItemClaim.namespaceURL}")
    private String namespaceURL;

    @Value("${webService.baseMultiItemClaim.localPart}")
    private String localPart;

    @Value("${webService.baseMultiItemClaim.dictType}")
    private String dictType;

    /**
     * 单据创建接口（支持多明细）
     * @param baseMultiItemClaimSaveRequest 报账单信息
     * @return
     */
    @Override
    public BaseClaimResponse createMultiItemClaim(BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest){
        /** url：webservice 服务端提供的服务地址，结尾必须加 "?wsdl"*/
        URL url = null;
        try {
            url = new URL(urlClaim);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("获取url异常");
        }
        /** QName 表示 XML 规范中定义的限定名称,QName 的值包含名称空间 URI、本地部分和前缀 */
        QName qName = new QName(namespaceURL, localPart);
        javax.xml.ws.Service service = javax.xml.ws.Service.create(url, qName);
        IfBaseClaimService ifBaseClaimService = service.getPort(IfBaseClaimService.class);

        baseMultiItemClaimSaveRequest.setApplyDate(DateUtils.getDate());//申请日期
        baseMultiItemClaimSaveRequest.setPaybleDate(DateUtils.getDate());//计划付款日期
        //设置申请人
        String companyCode = baseMultiItemClaimSaveRequest.getCompanyCode();
        String userNo = remoteDictDataService.getLabel(dictType,companyCode);
        if(StringUtils.isBlank(userNo)){
            logger.error("在字典表中获取申请人信息失败 dictType:{},companyCode:{},res:{}",dictType,companyCode,userNo);
            throw new BusinessException("在字典表中获取申请人信息失败");
        }
        baseMultiItemClaimSaveRequest.setUserNo(userNo);
        logger.info("单据创建接口（支持多明细）req:{}", JSONObject.toJSONString(baseMultiItemClaimSaveRequest));
        BaseClaimResponse result = ifBaseClaimService.createMultiItemClaim(baseMultiItemClaimSaveRequest);
        logger.info("单据创建接口（支持多明细）res:{}", JSONObject.toJSONString(result));
        return result;
    }
}
