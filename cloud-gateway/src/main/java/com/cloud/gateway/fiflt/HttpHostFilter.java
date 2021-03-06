package com.cloud.gateway.fiflt;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @auther cs
 * @date 2020/9/18 11:36
 * @description
 */
@SuppressWarnings("serial")
public class HttpHostFilter extends HttpServlet implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        StringBuffer url = request.getRequestURL();
        String s = request.getRequestURL().toString();
        // 头攻击检测  过滤主机名
        String requestHost = request.getHeader("host");
        if (requestHost != null && !checkBlankList(requestHost)) {
            response.setStatus(403);
            return;
        }
        filterChain.doFilter(request, response);
    }

    //判断主机是否存在白名单中
    private boolean checkBlankList(String host){
        if(host.contains("127.0.0.1")){//此处为自己网站的主机地址
            return true;
        }
        return true;
    }
}
