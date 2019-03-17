package com.i000.stock.user.service.impl.us.parse;

import java.time.LocalDate;

public interface Parse {


    /**
     * 将字符串形式的内容转化为Java对象并保存到相应的数据表中
     *
     * @param content
     * @param date
     */
    void save(String[] content, LocalDate date);
}
