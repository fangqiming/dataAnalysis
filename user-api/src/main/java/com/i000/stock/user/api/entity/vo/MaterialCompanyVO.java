package com.i000.stock.user.api.entity.vo;

import com.i000.stock.user.api.entity.bo.KVBo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCompanyVO {
    private String priceUrl;
    private List<KVBo> producer;
    private List<KVBo> user;
    private String industryUrl;
}
