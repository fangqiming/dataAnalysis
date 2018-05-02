package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:49 2018/5/2
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldNow {

    @TableId
    private Long id;
    private String name;
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    private BigDecimal oldRank;
    private LocalDate newDate;
    private BigDecimal newPrice;
    private BigDecimal newRank;
    private Integer holdDay;
    private BigDecimal gain;
    private String type;
    //持股数量
    private Integer amount;
    private String userCode;
    private String action;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HoldNow) {
            HoldNow hold = (HoldNow) obj;
            return this.name.equals(hold.getName())
                    && this.newDate.equals(hold.getNewDate())
                    && this.type.equals(hold.getType())
                    && this.userCode.equals(hold.getUserCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, newDate, type, userCode);
    }
}
