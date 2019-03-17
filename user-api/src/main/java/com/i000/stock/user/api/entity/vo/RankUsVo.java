package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RankUsVo {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private List<RankVo> ranks;

    private Long total;
}
