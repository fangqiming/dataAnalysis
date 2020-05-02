package com.i000.stock.user.core.result;


import lombok.Getter;

import java.util.List;

class TimeResult<T> extends ListResult<T> {
    private static final long serialVersionUID = -1891369940301562357L;

    @Getter
    private String date;

    public TimeResult() {

    }

    TimeResult(String date, List<T> entities) {
        super(entities);
        this.date = date;
    }

}
