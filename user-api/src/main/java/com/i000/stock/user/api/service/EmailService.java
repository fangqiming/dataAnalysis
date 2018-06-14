package com.i000.stock.user.api.service;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:30 2018/6/14
 * @Modified By:
 */
public interface EmailService {

    /**
     * 发送邮件
     *
     * @param title
     * @param content
     */
    void sendMail(String title, String content);
}
