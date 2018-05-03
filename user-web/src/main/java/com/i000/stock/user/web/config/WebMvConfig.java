package com.i000.stock.user.web.config;

import com.i000.stock.user.core.resolver.JsonParameterResolver;
import com.i000.stock.user.web.interceptor.RequestContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:34 2018/4/23
 * @Modified By:
 */
@Configuration
public class WebMvConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private RequestContextInterceptor requestContextInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new JsonParameterResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestContextInterceptor).addPathPatterns("/**");
    }
}
