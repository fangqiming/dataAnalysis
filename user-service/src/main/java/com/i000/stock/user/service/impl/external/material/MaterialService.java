package com.i000.stock.user.service.impl.external.material;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.api.entity.bo.KVBo;
import com.i000.stock.user.api.entity.vo.MaterialCompanyVO;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.MaterialMapper;
import com.i000.stock.user.dao.model.IndustryChain;
import com.i000.stock.user.dao.model.Material;
import com.i000.stock.user.dao.model.MaterialCompany;
import com.i000.stock.user.service.impl.IndustryChainService;
import com.i000.stock.user.service.impl.MaterialCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 直接追加，不删除
 */
@Service
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private IndustryChainService industryChainService;

    @Autowired
    private MaterialCompanyService materialCompanyService;

    public LocalDate getMaxDate() {
        return materialMapper.getMaxDate();
    }

    public void save(Material material) {
        materialMapper.insert(material);
    }

    public PageResult<Material> search(BaseSearchVo baseSearchVo, String filed, Boolean isAsc, String name) {
        EntityWrapper<Material> ew = new EntityWrapper();
        LocalDate maxDate = materialMapper.getMaxDate();
        ew.where("date={0}", maxDate);
        ew.like("name", name);
        if ("name".equals(filed)) {
            filed = "CONVERT(`name` USING gbk)";
        }
        ew.orderBy(filed, isAsc);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<Material> ranks = materialMapper.selectPage(page, ew);
        BigDecimal count = materialMapper.getCount(maxDate, name);
        PageResult<Material> result = new PageResult<>();
        result.setTotal(count.longValue());
        result.setList(ranks);
        return result;
    }

    public MaterialCompanyVO getDetail(String name) {
        MaterialCompanyVO result = new MaterialCompanyVO();
        String id = getIdByName(name);
        List<KVBo> producer;
        List<KVBo> user;
        if (!StringUtils.isEmpty(id)) {
            IndustryChain industryChain = industryChainService.getByName(name);
            MaterialCompany materialCompany = materialCompanyService.getByName(name);
            if (Objects.nonNull(materialCompany)) {
                producer = createKV(materialCompany.getCompanyCode(), materialCompany.getCompanyName());
                user = createKV(materialCompany.getDownstreamCode(), materialCompany.getDownstreamName());
                result.setProducer(producer);
                result.setUser(user);
            }
            result.setPriceUrl(createPriceLine(id, 12));
            if (Objects.nonNull(industryChain)) {
                result.setIndustryUrl(createIndustryChain(industryChain.getUrl()));
            }
        }
        return result;
    }

    private List<KVBo> createKV(String code, String name) {
        List<KVBo> result = new ArrayList<>();
        if (!StringUtils.isEmpty(code)) {
            String[] codes = code.split(",");
            String[] names = name.split(",");
            for (int i = 0; i < codes.length; i++) {
                if (codes[i].contains("HK") || codes[i].length() != 6) {
                    continue;
                }
                String url;
                if (codes[i].startsWith("6")) {
                    url = String.format("http://image.sinajs.cn/newchart/daily/n/%s%s.gif", "sh", codes[i]);
                } else {
                    url = String.format("http://image.sinajs.cn/newchart/daily/n/%s%s.gif", "sz", codes[i]);
                }
                result.add(KVBo.builder().k(names[i]).v(url).build());
            }
        }
        return result;
    }

    public String createPriceLine(String id, Integer month) {
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String beforeDate = LocalDate.now().minusMonths(month).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("http://www.100ppi.com/graph/%s-%s-%s-W490H300M30R0Y0Cp.png", id, beforeDate, nowDate);
    }

    public String createIndustryChain(String id) {
        return String.format("http://www.100ppi.com/graph/product/ppi_q_change/%s---630.png", id);
    }

    public String getIdByName(String name) {
        return materialMapper.getIdByName(name);
    }

    public List<Material> findMaterialByCode(String code) {
        LocalDate date = getMaxDate();
        List<MaterialCompany> materialCompanies = materialCompanyService.getMaterialByCode(code);
        List<String> material = materialCompanies.stream().map(a -> a.getName()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(material)) {
            return new ArrayList<>(0);
        }
        EntityWrapper<Material> ew = new EntityWrapper<>();
        ew.where("date = {0}", date);
        ew.in("name", material);
        return materialMapper.selectList(ew);
    }

}
