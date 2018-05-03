package com.i000.stock.user.web.interceptor;

import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.context.RequestValue;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:58 2018/5/3
 * @Modified By:
 */
@Component
public class RequestContextInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initRequestContext(request);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        RequestContext.clean();
        super.postHandle(request, response, handler, modelAndView);
    }

    private void initRequestContext(HttpServletRequest request) {

        String sign = request.getHeader(RequestValue.HEADER_SIGN);
        String accessCode = request.getHeader(RequestValue.HEAD_Account_Code);
        String deviceId = request.getHeader(RequestValue.HEAD_DEVICE_ID);
        new RequestContext.RequestContextBuild().accountCode(accessCode).deviceId(deviceId).sign(sign).build();
    }


}
