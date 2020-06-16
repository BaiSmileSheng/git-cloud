package com.cloud.settle.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.XmlUtil;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.domain.webServicePO.QryPaysSoapRequest;
import com.cloud.settle.domain.webServicePO.QryPaysSoapResponse;
import com.cloud.settle.enums.MonthSettleStatusEnum;
import com.cloud.settle.enums.QryPaysSoapStatusEnum;
import com.cloud.settle.service.IQryPaysSoapService;
import com.cloud.settle.service.ISmsMouthSettleService;
import com.cloud.settle.webService.fm.ErpPayoutReceiveServiceServiceLocator;
import com.cloud.settle.webService.fm.QryPaysSoapBindingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 报账单创建接口
 *
 * @author Lihongxia
 * @date 2020-06-09
 */
@Service
public class QryPaysSoapServiceImpl implements IQryPaysSoapService {

    private static Logger logger = LoggerFactory.getLogger(QryPaysSoapServiceImpl.class);

    @Autowired
    private ISmsMouthSettleService smsMouthSettleService;

    @Value("${webService.qryPaysSoap.urlClaim}")
    private String urlClaim;

    @Value("${webService.qryPaysSoap.namespaceURL}")
    private String namespaceURL;

    @Value("${webService.qryPaysSoap.localPart}")
    private String localPart;

    /**
     * 定时任务调用查询付款结果更新月度结算信息
     * @return
     */
    @Override
    public R updateKmsStatus() {

        R result = new R();
        //1.查询到待付款的月度结算单
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("settleStatus",MonthSettleStatusEnum.YD_SETTLE_STATUS_DFK.getCode());

        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        logger.info("查询到待付款的月度结算单数量 size:{}",smsMouthSettleList.size());
        if(!CollectionUtils.isEmpty(smsMouthSettleList)){
            //2.调用webservice接口 更新月度结算状态http://10.133.28.51:8080/fm/services/QryPays?wsdl
           callBackQueryBill(smsMouthSettleList);
        }

        return result;
    }

    /**
     * 获取 kms已处理的 月度结算单集合
     */
    private void callBackQueryBill(List<SmsMouthSettle> smsMouthSettleList){

        for(SmsMouthSettle smsMouthSettle : smsMouthSettleList){
            QryPaysSoapRequest qryPaysSoapRequest = new QryPaysSoapRequest();
            qryPaysSoapRequest.setDOC_NO(smsMouthSettle.getKmsNo());
            QryPaysSoapResponse qryPaysSoapResponse = queryBill(qryPaysSoapRequest);
            if(QryPaysSoapStatusEnum.FLAG_SUCCESS.getCode().equals(qryPaysSoapResponse.getFlag())){
                //支付成功的更新状态为结算完成
                if(QryPaysSoapStatusEnum.S_SUCCESS.getCode().equals(qryPaysSoapResponse.getStatus())){
                    SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
                    smsMouthSettleReq.setSettleStatus(MonthSettleStatusEnum.YD_SETTLE_STATUS_JSWC.getCode());
                    smsMouthSettleReq.setKmsStatus(qryPaysSoapResponse.getStatus());
                    Date kmsPayDate = DateUtils.parseDate(qryPaysSoapResponse.getTradedate());
                    smsMouthSettleReq.setKmsPayDate(kmsPayDate);
                    smsMouthSettleReq.setKmsNo(qryPaysSoapResponse.getKmsNo());
                    Example example = new Example(SmsMouthSettle.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("kmsNo",smsMouthSettleReq.getKmsNo());
                    smsMouthSettleService.updateByExampleSelective(smsMouthSettleReq,example);
                }else {
                    //暂不更新结算状态
                    SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
                    smsMouthSettleReq.setKmsStatus(qryPaysSoapResponse.getStatus());
                    smsMouthSettleReq.setKmsNo(qryPaysSoapResponse.getKmsNo());
                    Example example = new Example(SmsMouthSettle.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("kmsNo",smsMouthSettleReq.getKmsNo());
                    smsMouthSettleService.updateByExampleSelective(smsMouthSettleReq,example);
                }
            }
        }
    }

    /**
     * 查询付款结果
     * @param qryPaysSoapRequest 查询付款结果入参
     * @return 付款结果
     */
    @Override
    public QryPaysSoapResponse queryBill(QryPaysSoapRequest qryPaysSoapRequest) {
        String inXml = XmlUtil.convertToXml(qryPaysSoapRequest);
        QryPaysSoapResponse qryPaysSoapResponse = null;
        try{
            QName qName = new QName(namespaceURL, localPart);
            QryPaysSoapBindingStub generalMDMDataReleaseBindingStub =
                    (QryPaysSoapBindingStub) new ErpPayoutReceiveServiceServiceLocator(urlClaim,qName).getQryPays();
            logger.info("调用付款接口入参 req:{}",inXml);
            String outXml = generalMDMDataReleaseBindingStub.queryBill(inXml);
            logger.info("调用付款接口出参 res:{}",outXml);

            JAXBContext context = JAXBContext.newInstance(QryPaysSoapResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            qryPaysSoapResponse = (QryPaysSoapResponse)unmarshaller.unmarshal(new StringReader(outXml));
            //冗余kms单号
            qryPaysSoapResponse.setKmsNo(qryPaysSoapRequest.getDOC_NO());
        }catch (Exception e){
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "调用付款接口异常: {}", w.toString());
            throw new BusinessException("调用付款接口异常");
        }
        return qryPaysSoapResponse;
    }
}
