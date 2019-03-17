package com.i000.stock.user.service.impl.external.material;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.MaterialMapper;
import com.i000.stock.user.dao.model.DiagnosisFlush;
import com.i000.stock.user.dao.model.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 直接追加，不删除
 */
@Service
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    public LocalDate getMaxDate() {
        return materialMapper.getMaxDate();
    }

    public void save(Material material) {
        materialMapper.insert(material);
    }

    public PageResult<Material> search(BaseSearchVo baseSearchVo, String filed, Boolean isAsc) {
        EntityWrapper<Material> ew = new EntityWrapper();
        LocalDate maxDate = materialMapper.getMaxDate();
        ew.where("date={0}", maxDate);
        ew.orderBy(filed, isAsc);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<Material> ranks = materialMapper.selectPage(page, ew);
        BigDecimal count = materialMapper.getCount(maxDate);
        PageResult<Material> result = new PageResult<>();
        result.setTotal(count.longValue());
        result.setList(ranks);
        return result;
    }

}
