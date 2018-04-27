package com.i000.stock.user.api.constant;

import com.i000.stock.user.core.constant.enums.BaseEnum;
import lombok.Getter;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:00 2018/4/27
 * @Modified By:
 */
@Getter
public enum MailPeronType implements BaseEnum {

    RECEIPT(0L, "收件人", "TO"),
    COPY(1L, "抄送人", "CC"),
    SECRET_SEND(2L, "密送人", "BCC");


    private Long code;
    private String msg;
    private String value;

    MailPeronType(Long code, String msg, String value) {
        this.code = code;
        this.msg = msg;
        this.value = value;
    }
}
