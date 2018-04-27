package com.i000.stock.user.api.service;

import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:40 2018/4/27
 * @Modified By:
 */
public interface MailFetchService {

    /**
     * 用于获取邮件内容
     *
     * @return
     * @throws Exception
     */
    LocalDate initMail() throws Exception;

}
