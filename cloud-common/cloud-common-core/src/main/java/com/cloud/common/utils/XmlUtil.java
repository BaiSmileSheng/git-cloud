package com.cloud.common.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * Jaxb2工具类
 */
public class XmlUtil {
    public static final Log LOG = LogFactory.getLog(XmlUtil.class);
    /**
     * JavaBean转换成xml
     * 默认编码UTF-8
     * @param objs
     * @return
     */
    public static String convertToXml(Object[] objs) {
        String encoding = "UTF-8";
        if(objs==null){
            return "";
        }
        return convertToXml(objs, encoding);
    }
    /**
     * JavaBean转换成xml
     * @param objs
     * @param encoding
     * @return
     */
    public static String convertToXml(Object[] objs, String encoding) {
        if(objs==null){
            return "";
        }
        String xml = "";
        if(objs.length>0){
            for(int i=0;i<objs.length;i++){
                Boolean value = isBasicType(objs[i]);
                if(value==null){
                    LOG.info("value不为空时执行");
                }else if(value.booleanValue()){
                    xml += objs[i].toString()+"\n";
                }else{
                    xml += convertToXml(objs[i], encoding);
                }
            }
        }
        return xml;
    }
    private static Boolean isBasicType(Object param){
        if(param == null){
            return null;
        }else if (param instanceof Integer){
//			int value = ((Integer) param).intValue();
            return Boolean.valueOf(true);
        } else if (param instanceof String) {
//			String s = (String) param;
            return Boolean.valueOf(true);
        } else if (param instanceof Double) {
//			double d = ((Double) param).doubleValue();
            return Boolean.valueOf(true);
        } else if (param instanceof Float) {
//			float f = ((Float) param).floatValue();
            return Boolean.valueOf(true);
        } else if (param instanceof Long) {
//			long l = ((Long) param).longValue();
            return Boolean.valueOf(true);
        } else if (param instanceof Boolean) {
//			boolean b = ((Boolean) param).booleanValue();
            return Boolean.valueOf(true);
        } else if (param instanceof Date) {
//			Date d = (Date) param;
            return Boolean.valueOf(true);
        }else{
            return Boolean.valueOf(false);
        }
    }
    /**
     * JavaBean转换成xml
     * 默认编码UTF-8
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        if(obj==null){
            return "";
        }
        return convertToXml(obj, "UTF-8");
    }
    /**
     * JavaBean转换成xml
     * @param obj
     * @param encoding
     * @return
     */
    public static String convertToXml(Object obj, String encoding) {
        if(obj==null){
            return "";
        }
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
//			e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
        return result;
    }
    /**
     * xml转换成JavaBean
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
//			e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
        return t;
    }
    /**
     * Javabean嵌套List转换为XML
     * @param value		JavaBean对象
     * @param rootName	XML Root Tag Name
     * @param rootCls	Root JavaBean Class
     * @param elemName	XML List Element Tag Name
     * @param propName	JavaBean List Property Name
     * @param beanCls	JavaBean List Element Class
     * @return	返回XML
     */
    public static String listBeanToXML(Object value, String rootName, Class rootCls, String[] elemName, String[] propName, Class[] beanCls){
        String xml=null;
        if(rootName!=null&&rootCls!=null){
            XStream xs = new XStream(new DomDriver());
            xs.alias(rootName, rootCls);
            if (elemName != null && propName != null && beanCls != null && elemName.length == propName.length && elemName.length == beanCls.length) {
                for (int i = 0; i < elemName.length; i++) {
                    xs.alias(elemName[i], beanCls[i]);
                    xs.aliasField(elemName[i], beanCls[i], propName[i]);
                }
            }
            xml = xs.toXML(value);
            LOG.info("javabean转成xml为:\n"+xml);
        }else{
            xml=convertToXml(value);
        }
        return xml;
    }
    /**
     * XML转换为嵌套List的Javabean
     * @param xml	xml数据
     * @param rootName	XML Root Tag Name
     * @param rootCls	Root JavaBean Class
     * @param elemName	XML List Element Tag Name
     * @param propName	JavaBean List Property Name
     * @param beanCls	JavaBean List Element Class
     * @return	返回JavaBean
     */
    public static Object xmlToListBean(String xml, String rootName, Class rootCls, String[] elemName, String[] propName, Class[] beanCls){
        XStream xs = new XStream(new DomDriver());
        xs.allowTypesByRegExp(new String[]{".*"});
        xs.alias(rootName, rootCls);
        if (elemName != null && propName != null && beanCls != null && elemName.length == propName.length && elemName.length == beanCls.length) {
            for (int i = 0; i < elemName.length; i++) {
                xs.alias(elemName[i], beanCls[i]);
                xs.aliasField(elemName[i], beanCls[i], propName[i]);
            }
        }
        Object value = xs.fromXML(xml);
        return value;
    }
}
