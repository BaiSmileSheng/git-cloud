package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.settle.webService.gems.IfBaseClaimService;
import com.cloud.system.feign.RemoteDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Map;

/**
 * 报账单创建接口  提供者
 *
 * @author Lihongxia
 * @date 2020-06-01
 */
@RestController
@RequestMapping("baseMultiItemClaimController")
@Api(tags = "报账单创建接口  提供者")
public class BaseMultiItemClaimController extends BaseController {

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
     */
    @PostMapping("createMultiItemClaim")
    @ApiOperation(value = "单据创建接口（支持多明细） ", response = BaseClaimResponse.class)
    public BaseClaimResponse createMultiItemClaim(@RequestBody BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest) throws Exception{


        /** url：webservice 服务端提供的服务地址，结尾必须加 "?wsdl"*/
        URL url = new URL(urlClaim);
        /** QName 表示 XML 规范中定义的限定名称,QName 的值包含名称空间 URI、本地部分和前缀 */
        QName qName = new QName(namespaceURL, localPart);
        Service service = Service.create(url, qName);
        IfBaseClaimService ifBaseClaimService = service.getPort(IfBaseClaimService.class);

        BindingProvider bindingProvider = (BindingProvider) ifBaseClaimService;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put("com.sun.xml.internal.ws.connection.timeout", 10 * 1000);//建立连接的超时时间为10秒
        requestContext.put("com.sun.xml.internal.ws.request.timeout", 15 * 1000);


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
