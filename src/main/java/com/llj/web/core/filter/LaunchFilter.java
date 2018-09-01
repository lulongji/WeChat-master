package com.llj.web.core.filter;

import com.llj.web.server.RuntimeData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import java.io.IOException;


/**
 * 启动加载过滤器
 *
 * @author LongJi.LU (lulongji2011@163.com)
 * @version 2016年4月28日 下午6:44:35
 */
public class LaunchFilter implements Filter {

    @Autowired
    private RuntimeData runtimedata;
    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(LaunchFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            System.out.println("启动过滤器！");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }

}
