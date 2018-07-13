package com.i000.stock.user.api.service.util;

/**
 * @Author:qmfang
 * @Description: 用于发送邮箱服务
 * @Date:Created in 15:30 2018/6/14
 * @Modified By:
 */
public interface EmailService {

    /**
     * 发送邮件
     *
     * @param title
     * @param content
     * @param needSend
     */
    void sendMail(String title, String content, boolean needSend);
}
