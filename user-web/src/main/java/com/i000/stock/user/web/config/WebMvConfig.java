package com.i000.stock.user.web.config;

import com.i000.stock.user.core.resolver.JsonParameterResolver;
import com.i000.stock.user.web.converter.Long2LocalDateTimeConverter;
import com.i000.stock.user.web.converter.String2LocalDateConverter;
import com.i000.stock.user.web.interceptor.RequestContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Bean
    public Converter<String, LocalDateTime> Long2LocalDateTimeConverter() {
        return new Long2LocalDateTimeConverter();
    }

    @Bean
    public Converter<String, LocalDate> String2LocalDateConverter() {
        return new String2LocalDateConverter();
    }

}
