package com.i000.stock.user.core.util;

import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.constant.enums.BaseEnum;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.exception.WebApiException;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:02 2018/1/9
 * @Modified By:
 */
public class CodeEnumUtil {
    public static <E extends Enum<?> & BaseEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code){
                return e;
            }
        }
        throw new ServiceException(ApplicationErrorMessage.INVALID_PARAMETER.getCode(),"int->enum 转化异常");
    }

    public static <T extends Enum>  T  transformationStr2Enum(String str, Class<T> klass) {
        try {
            return (T) Enum.valueOf(klass, str);
        } catch (IllegalArgumentException e) {
            throw new WebApiException(ApplicationErrorMessage.INVALID_PARAMETER.getCode(), str + "参数错误");
        }
    }
}
