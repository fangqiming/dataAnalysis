package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:36 2018/4/25
 * @Modified By:
 */
@Data
public class IndexBo {

    private Long error_code;
    private List<IndexInfo> data;
    private String reason;
}
