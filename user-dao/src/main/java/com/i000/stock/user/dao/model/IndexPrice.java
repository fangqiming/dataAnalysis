package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:57 2018/6/14
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexPrice {

    @TableId
    private Long id;
    private LocalDate date;
    private String content;
}
