package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.MaterialBO;
import com.i000.stock.user.api.entity.vo.MaterialVO;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Material;
import com.i000.stock.user.service.impl.external.material.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/material")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/find_main")
    public ResultEntity findMain(@RequestParam(defaultValue = "975, 927, 1121, 733, 551, 524, 711, 89") List<String> ids) {
        List<String> urls = new ArrayList<>(9);
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String beforeDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (String id : ids) {
            urls.add(String.format("http://www.100ppi.com/graph/%s-%s-%s-W490H300M30R0Y0Cp.png", id, beforeDate, nowDate));
        }
        return Results.newListResultEntity(urls);
    }

    @GetMapping("/search")
    public ResultEntity search(BaseSearchVo baseSearchVo, String filed, Boolean isAsc) {
        ValidationUtils.validate(baseSearchVo);
        PageResult<Material> materials = materialService.search(baseSearchVo, filed, isAsc);
        List<Material> list = materials.getList();
        if (CollectionUtils.isEmpty(list)) {
            return Results.newPageResultEntity(0L, new ArrayList<Material>(0));
        }
        List<MaterialBO> materialBOS = ConvertUtils.listConvert(list, MaterialBO.class);
        MaterialVO result = MaterialVO.builder().date(list.get(0).getDate())
                .materials(materialBOS).total(materials.getTotal()).build();
        return Results.newSingleResultEntity(result);
    }

}
