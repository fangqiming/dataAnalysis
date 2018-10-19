package com.i000.stock.user.web.interceptor;

import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.context.RequestValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:58 2018/5/3
 * @Modified By:
 */
@Slf4j
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

    private void initRequestContext(HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            String sign = request.getHeader(RequestValue.HEADER_SIGN);
            String accessCode = URLDecoder.decode(request.getHeader(RequestValue.HEAD_Account_Code), "utf-8");
            System.out.println(URLDecoder.decode(request.getHeader(RequestValue.HEAD_Account_Code), "utf-8"));
            String amountShare = request.getHeader(RequestValue.HEAD_Amount_Share);
            new RequestContext.RequestContextBuild().accountCode(accessCode).amountShare(amountShare).sign(sign).build();
        } catch (Exception e) {
            log.warn("请求头消息设置失败");
        }
    }


}
