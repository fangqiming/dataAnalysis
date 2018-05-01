package com.i000.stock.user.api.entity.bo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties(prefix="business.init")
public class AssetInitBo {

    private BigDecimal amount;

    private BigDecimal quantity;
}
