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
     * @param needSend
     */
    void sendMail(String title, String content, boolean needSend);

    /**
     * 发送带附件的邮件
     *
     * @param title
     * @param content
     * @param needSend
     */
    void sendFilMail(String title, String content, boolean needSend);
}
