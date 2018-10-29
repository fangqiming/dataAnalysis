package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:18 2018/10/26
 * @Modified By:
 */
@Data
public class Holiday {

    @TableId
    private Long id;

    private LocalDate date;

    private Byte isWorking;
}
