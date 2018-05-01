package com.i000.stock.user.web.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@Slf4j
@Component
public class ServletRequestHandlerEventListaner implements ApplicationListener<ServletRequestHandledEvent> {

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent servletRequestHandledEvent) {
        System.out.println(servletRequestHandledEvent.getClientAddress());
        System.out.println(servletRequestHandledEvent.getDescription());
    }
}
