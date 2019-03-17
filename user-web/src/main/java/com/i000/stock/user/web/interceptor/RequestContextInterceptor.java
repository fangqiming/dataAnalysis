package com.i000.stock.user.web.interceptor;

import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.context.RequestValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        String sign = request.getHeader(RequestValue.HEADER_SIGN);
        String accountCode = request.getHeader(RequestValue.HEAD_Account_Code);
        accountCode = StringUtils.isEmpty(accountCode) ? null : URLDecoder.decode(accountCode, "utf-8");
        String amountShare = request.getHeader(RequestValue.HEAD_Amount_Share);
        String accessCode = request.getHeader(RequestValue.HEAD_ACCESS_CODE);
        new RequestContext.RequestContextBuild().accountCode(accountCode)
                .amountShare(amountShare).sign(sign).accessCode(accessCode).build();

    }

}
