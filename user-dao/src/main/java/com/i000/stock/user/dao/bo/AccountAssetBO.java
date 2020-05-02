package com.i000.stock.user.dao.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAssetBO {

    private String accountName;
    private LocalDate date;
    private Integer share;
    private Double total;
    private String country;
    private Double position;
}
