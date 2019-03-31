package com.i000.stock.user.core.constant.enums;


import lombok.Getter;

/**
 * @Author:qmfang
 * @Description: 异常信息枚举
 * @Date:Created in 10:44 2018/4/23
 * @Modified By:
 */
public enum ApplicationErrorMessage implements BaseEnum {

    NO_PERMISSION(11090001L, "无权限"),
    SERVER_ERROR(11090002L, "系统内部异常"),
    INVALID_PARAMETER(11090003L, "缺少参数或参数错误"),
    NOT_EXISTS(11090004L, "请求的数据不存在"),
    PASSWORD_ERROR(11090005L, "密码错误"),
    USER_HAS_EXIST(11090006L, "手机号码已经存在"),
    US_PARSE_ERROR(11090007L, "美股报告解析错误"),

    US_REPORT_HAD_PARSE(11090008L, "美股报告已经解析"),
    NET_DATA_GET_ERROR(11090009L, "网络数据获取失败"),


    ACCESS_CODE_IS_INVALID(11090010L, "无效的访问码"),
    ACCESS_CODE_TIME_OUT(11090011L, "访问码超时，请重新登录"),
    NO_AUTH(11090012L, "无权限，请联系管理员"),
    TIME_FORMATE_ERROR(11090013L, "时间格式错误"),

    USER_HAS_STOCK(11090013L, "已经关注该股票"),
    USER_STOCK_OVER(11090014L, "用户关注的股票已经超过了上限"),
    NO_AI_SCORE(11090015L, "AI未对该股票进行评分"),

    NO_LOGIN(11090016L, "未登录"),
    ACTUAL_DISC_DATA_ERROR(11090017L, "实盘数据错误"),;

    @Getter
    private Long code;

    @Getter
    private String msg;

    ApplicationErrorMessage(Long code, String msg) {
        this.msg = msg;
        this.code = code;
    }
}
