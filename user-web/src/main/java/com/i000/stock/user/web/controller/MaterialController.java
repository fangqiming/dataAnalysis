package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.NoticeBO;
import com.i000.stock.user.api.entity.vo.MaterialBO;
import com.i000.stock.user.api.entity.vo.MaterialCompanyVO;
import com.i000.stock.user.api.entity.vo.MaterialVO;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Material;
import com.i000.stock.user.service.impl.StockPoolService;
import com.i000.stock.user.service.impl.external.NoticeService;
import com.i000.stock.user.service.impl.external.material.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
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

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private StockPoolService stockPoolService;

    @GetMapping("/find_main")
    public ResultEntity findMain(@RequestParam(defaultValue = "975, 927, 1121, 733, 551, 524, 711, 89") List<String> ids) {
        List<String> urls = new ArrayList<>(9);
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String beforeDate = LocalDate.now().minusMonths(12).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (String id : ids) {
            urls.add(String.format("http://www.100ppi.com/graph/%s-%s-%s-W490H300M30R0Y0Cp.png", id, beforeDate, nowDate));
        }
        return Results.newListResultEntity(urls);
    }

    @GetMapping("/search")
    public ResultEntity search(BaseSearchVo baseSearchVo, @RequestParam(defaultValue = "name") String filed,
                               Boolean isAsc, @RequestParam(defaultValue = "") String name) {
        ValidationUtils.validate(baseSearchVo);
        PageResult<Material> materials = materialService.search(baseSearchVo, filed, isAsc, name);
        List<Material> list = materials.getList();
        List<MaterialBO> materialBOS = ConvertUtils.listConvert(list, MaterialBO.class);
        MaterialVO result = MaterialVO.builder().materials(materialBOS).total(materials.getTotal()).build();
        if (CollectionUtils.isEmpty(list)) {
            return Results.newSingleResultEntity(result);
        }
        result.setDate(list.get(0).getDate());
        return Results.newSingleResultEntity(result);
    }

    @GetMapping("/get_detail")
    public ResultEntity getDetail(@Param("name") String name) {
        ValidationUtils.validateStringParameter(name, "物料名称不能为空");
        MaterialCompanyVO detail = materialService.getDetail(name);
        return Results.newSingleResultEntity(detail);
    }

    @GetMapping("/find_material_by_code")
    public ResultEntity findMaterialByCode(@RequestParam("code") String code) {
        ValidationUtils.validateStringParameter(code, "股票代码不能为空");
        List<Material> material = materialService.findMaterialByCode(code);
        List<MaterialBO> list = ConvertUtils.listConvert(material, MaterialBO.class);
        return Results.newListResultEntity(list);
    }

    @GetMapping(path = "/find_notice_by_code")
    public ResultEntity findNotice(@Param("code") String code) {
        List<NoticeBO> notice = noticeService.getNoticeByCode(code, 7);
        return Results.newListResultEntity(notice);
    }

    @GetMapping(path = "/find_stock_pool")
    public ResultEntity findStockPoolNotice() {
        List<NoticeBO> notices = noticeService.findByCodes();
        return Results.newListResultEntity(notices);
    }

}
