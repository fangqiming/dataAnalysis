package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:08 2018/7/18
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataController {

    @Autowired
    private StockPledgeService stockPledgeService;

    @Autowired
    private CompanyService companyService;

    /**
     * 股权质押数据页面
     * 需要获取每天的交易记录
     */
    @GetMapping(path = "/search")
    public ResultEntity searchTrade(BaseSearchVo baseSearchVo, String code, String name) {
        ValidationUtils.validate(baseSearchVo);
        Page<StockPledgeVo> result = stockPledgeService.search(baseSearchVo, code, name);
        return CollectionUtils.isEmpty(result.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(result.getTotal(), result.getList());
    }

    /**
     * 根据公司名称获取到全部的股票代码
     */
    @GetMapping(path = "/find_stock_code")
    public ResultEntity findStockCode() {
        String names = "艾华集团,爱尔眼科,安琪酵母,奥佳华,白云机场,白云山,保利地产,北新建材,博雅生物,沧州大化,潮宏基,晨光文具,承德露露,城市传媒,大北农,大豪科技,大秦铁路,大族激光,鼎龙股份,东阿阿胶,东港股份,东莞控股,东睦股份,东易日盛,二三四五,法拉电子,方大炭素,方大特钢,飞科电器,菲利华,分众传媒,烽火科技,凤凰传媒,涪陵电力,涪陵榨菜,福安药业,福建高速,福星股份,福耀玻璃,富安娜,赣锋锂业,歌尔股份,歌华有线,格力电器,古井贡酒,光大嘉宝,光明地产,光明乳业,光迅科技,广汽集团,广信股份,贵州茅台,桂冠电力,国城矿业,国星光电,海大集团,海航基础,海康威视,海澜之家,海利得,海螺水泥,海天味业,海峡股份,瀚蓝环境,杭萧钢构,航民股份,航天科技,豪迈科技,亨通光电,恒瑞医药,恒顺醋业,横店东磁,鸿达兴业,湖北能源,沪电股份,花园生物,华大基金,华帝股份,华东医药,华峰超纤,华兰生物,华鲁恒升,华润双鹤,华夏幸福,华新水泥,华业资本,黄山旅游,煌上煌,尖峰集团,金达威,金禾实业,金证股份,劲嘉股份,京东方A,精锻科技,九鼎投资,巨星科技,绝味食品,君正集团,科达洁能,科大讯飞,老板电器,老凤祥,利尔化学,联美控股,良信电器,龙蟒佰利,隆平高科,泸州老窖,罗莱生活,罗牛山,洛阳钼业,迈瑞医疗,美的集团,美亚光电,南京高科,宁波华翔,鹏博士,鹏起科技,片仔癀,祁连山,青岛海尔,轻纺城,闰土股份,三花智控,三七互娱,三一重工,三友化工,森马服饰,山大华特,山东高速,山东药玻,山西汾酒,山鹰纸业,陕西煤业,上峰水泥,上港集团,上海机场,上海石化,舍得酒业,深天马A,深物业A,深圳燃气,生物股份,生益科技,世纪华通,世荣兆业,双汇发展,双鹭药业,水井坊,顺鑫农业,宋城演艺,苏泊尔,索菲亚,塔牌集团,太阳纸业,汤臣倍健,桃李面包,特变电工,天康生物,天齐锂业,天坛生物,通策医疗,通威股份,同花顺,同仁堂,兔宝宝,万华化学,万科,万年青,万润股份,万业企业,网宿科技,威孚高科,潍柴动力,伟明环保,伟星股份,伟星新材,五粮液,西藏天路,先导智能,小商品城,小天鹅A,新大陆,新和成,新开源,星网锐捷,星宇股份,兴业矿业,亚太科技,扬农化工,阳光照明,伊力特,伊利股份,亿帆医药,鱼跃医疗,宇通客车,粤高速A,韵达股份,长江电力,长鹰信质,招商银行,浙江鼎力,浙江美大,浙数文化,中国电影,中国国旅,中国国贸,中国核电,中国巨石,中国平安,中国汽研,中国神华,中环股份,中金环境,中炬高新,中粮地产,中牧股份,中南传媒,中山公用,中顺洁柔,中天能源,中文传媒,中信证券,中兴通讯,中原内配,重庆啤酒,紫光股份,滨化股份,兴蓉环境";
        String[] name = names.split(",");
        List<String> codes = new ArrayList<>(name.length);
        List<String> no = new ArrayList<>(name.length);
        Map<String, List<Company>> nameToCompany = companyService.findAll().stream().collect(Collectors.groupingBy(Company::getName));
        for (String nameTemp : name) {
            if (nameToCompany.containsKey(nameTemp)) {
                codes.add(nameToCompany.get(nameTemp).get(0).getCode());
            } else {
                no.add(nameTemp);
            }
        }
        System.out.println(codes);
        System.out.println(no);
        return Results.newListResultEntity(codes);
    }

}
