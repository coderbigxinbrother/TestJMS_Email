package com.yc.web.filters;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.jws.soap.InitParam;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 过滤字符编码
 * @author wx
 * 
 *
 */
@WebFilter("/*")
//@WebInitParam(name="filterPage",value="admin.jsp, news_add.jsp, topic_add.jsp")
public class EncodingFilter implements Filter {
    public EncodingFilter() {
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);  //将请求转发给过滤器链上下一个对象
    }
    @Override
    public void destroy() {
    }
}
