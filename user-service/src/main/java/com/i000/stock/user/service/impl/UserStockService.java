package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.vo.RankVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.mapper.UserStockMapper;
import com.i000.stock.user.dao.model.Company;
import com.i000.stock.user.dao.model.DiagnosisFlush;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.dao.model.UserStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用来处理用户自选股的服务
 */
@Service
public class UserStockService {

    @Autowired
    private UserStockMapper userStockMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DiagnosisFlushService diagnosisFlushService;

    @Autowired
    private RankService rankService;

    /**
     * 添加自选股
     *
     * @param userStock
     */
    public void saveStock(UserStock userStock) {
        checkStock(userStock);
        userStockMapper.insert(userStock);
    }

    /**
     * 删除自选股
     *
     * @param code
     */
    public void deleteStock(String code, String user) {
        EntityWrapper<UserStock> ew = new EntityWrapper<>();
        ew.where("user={0}", user).and("code={0}", code);
        userStockMapper.delete(ew);
    }

    /**
     * 查找用户自选股
     *
     * @param user
     * @return
     */
    public List<RankVo> findStockByUser(String user) {
        List<UserStock> userStocks = userStockMapper.findByUser(user);
        List<RankVo> rankVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userStocks)) {
            List<String> codes = userStocks.stream().map(a -> a.getCode()).collect(Collectors.toList());
            List<DiagnosisFlush> diagnosis = diagnosisFlushService.findByCodes(codes);
            if (!CollectionUtils.isEmpty(diagnosis)) {
                List<Company> companies = companyService.findByCodes(codes);
                Map<String, List<Company>> companyMap = companies.stream().collect(Collectors.groupingBy(Company::getCode));
                rankVos = ConvertUtils.listConvert(diagnosis, RankVo.class, (d, s) -> {
                    d.setName(companyMap.get(s.getCode()).get(0).getName());
                });
            }
        }
        return rankVos;
    }

    /**
     * 验证用户该自选股能否添加
     *
     * @param userStock
     */
    private void checkStock(UserStock userStock) {
        UserStock stock = userStockMapper.getByUserAndCode(userStock.getUser(), userStock.getCode());
        //AI是否对该股票进行了打分
        Rank rank = rankService.getByCode(userStock.getCode());
        if (Objects.isNull(rank)) {
            throw new ServiceException(ApplicationErrorMessage.NO_AI_SCORE);
        }
        if (Objects.nonNull(stock)) {
            throw new ServiceException(ApplicationErrorMessage.USER_HAS_STOCK);
        }
        Long stockCount = userStockMapper.getStockCountByUser(userStock.getUser());
        if (stockCount >= 10) {
            throw new ServiceException(ApplicationErrorMessage.USER_STOCK_OVER);
        }
    }
}
