package com.cloud.common.core.controller;

import cn.hutool.json.JSONUtil;
import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.PageDomain;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.core.page.TableSupport;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.ServletUtils;
import com.cloud.common.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web层通用数据处理
 *
 * @author cloud
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    RedisUtils redis;

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (null != pageNum && null != pageSize) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 获取request
     */
    public HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    /**
     * 获取response
     */
    public HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    public long getCurrentUserId() {
        String currentId = getRequest().getHeader(Constants.CURRENT_ID);
        if (StringUtils.isNotBlank(currentId)) {
            return Long.valueOf(currentId);
        }
        return 0l;
    }

    public <T> T getUserInfo(Class<T> beanClass){
        String token = getRequest().getHeader("token");
        String userInfoStr = redis.get(Constants.ACCESS_TOKEN + token);
        T t = JSONUtil.toBean(userInfoStr,beanClass);
        return t;
    }

    public Map<String,Object> getUserInfoMap(){
        String token = getRequest().getHeader("token");
        String userInfoStr = redis.get(Constants.ACCESS_TOKEN + token);
        Map<String,Object> map = JSONUtil.parseObj(userInfoStr);
        return map;
    }

    /**
     * 根据用户id从redis取数据权限(工厂)
     * @param userId
     * @return
     */
    public String getUserFactoryScopes(Long userId) {
        String scocpes = redis.get(Constants.ACCESS_USERID_SCOPE_FACTORY + userId);
        return scocpes;
    }

    /**
     * 根据用户id从redis取数据权限(采购组)
     * @param userId
     * @return
     */
    public String getUserPurchaseScopes(Long userId) {
        String scocpes = redis.get(Constants.ACCESS_USERID_SCOPE_PURCHASE + userId);
        return scocpes;
    }

    public String getLoginName() {
        return getRequest().getHeader(Constants.CURRENT_USERNAME);
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        PageInfo<?> pageInfo = new PageInfo(list);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(pageInfo.getTotal());
        rspData.setPageNum(pageInfo.getPageNum());
        return rspData;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected R result(List<?> list) {
        PageInfo<?> pageInfo = new PageInfo(list);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rows", list);
        m.put("pageNum", pageInfo.getPageNum());
        m.put("total", pageInfo.getTotal());
        return R.ok(m);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected R toAjax(int rows) {
        return rows > 0 ? R.ok() : R.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected R toAjax(boolean result) {
        return result ? R.ok() : R.error();
    }
}
