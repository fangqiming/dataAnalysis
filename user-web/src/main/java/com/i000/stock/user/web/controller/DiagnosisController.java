package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.buiness.UserLoginService;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Company;
import com.i000.stock.user.dao.model.UserStock;
import com.i000.stock.user.service.impl.DiagnosisFlushService;
import com.i000.stock.user.service.impl.FinancialService;
import com.i000.stock.user.service.impl.RankExplainService;
import com.i000.stock.user.service.impl.UserStockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/diagnosis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DiagnosisController {

    @Autowired
    private RankExplainService rankExplainService;

    @Autowired
    private FinancialService financialService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserStockService userStockService;

    /**
     * 根据得分的高低分页查询股票打分，仅仅针对内部人员开放
     *
     * @param baseSearchVo
     * @return
     */
    @GetMapping(path = "/search_rank")
    public ResultEntity searchRank(BaseSearchVo baseSearchVo, String filed, Boolean isAsc) {
        String accessCode = getAccessCode();
//        userLoginService.checkAuth(accessCode, AuthEnum.A_RANK);
        ValidationUtils.validate(baseSearchVo);
        PageResult<RankVo> rankPage = rankExplainService.searchRankVo(baseSearchVo, filed, isAsc);
        RankUsVo result = new RankUsVo();
        result.setTotal(rankPage.getTotal());
        result.setRanks(rankPage.getList());
        LocalDate date = CollectionUtils.isEmpty(rankPage.getList()) ? null : rankPage.getList().get(0).getDate();
        result.setDate(date);
        return Results.newSingleResultEntity(result);
    }

    /**
     * 根据股票代码获取股票的技术形态
     *
     * @param code
     * @return
     */
    @GetMapping(path = "/find_technology")
    public ResultEntity findTechIndex(@RequestParam String code) {
        ValidationUtils.validateStringParameter(code, "股票代码不能为空");
        List<TechnologyVo> technologyVos = financialService.findTechnologyByCode(code);
        return Results.newListResultEntity(technologyVos);
    }

    /**
     * 根据股票代码或者公司名称获取诊断结果
     *
     * @param code
     * @return
     */
    @GetMapping(path = "/get")
    public ResultEntity getDiagnosis(@RequestParam String code) {
        String accessCode = getAccessCode();
//        userLoginService.checkAuth(accessCode, AuthEnum.A_DIAGNOSIS);
        String stockCode = getCodeByName(code);
        ValidationUtils.validateStringParameter(stockCode, "股票代码或公司名称不能为空");
        DiagnosisVo diagnosisResult = financialService.getDiagnosisResult(stockCode);
        return Results.newSingleResultEntity(diagnosisResult);
    }

    /**
     * 获取该股票近5年的财务数据
     *
     * @param code
     * @return
     */
    @GetMapping(path = "/find_financial")
    public ResultEntity findFinancial(@RequestParam String code) {
        String accessCode = getAccessCode();
//        userLoginService.checkAuth(accessCode, AuthEnum.A_DIAGNOSIS);
        String stockCode = getCodeByName(code);
        ValidationUtils.validateStringParameter(stockCode, "股票代码不能为空或公司名称错误");
        FinancialVo financials = financialService.findFinancialByCode(stockCode);
        return Results.newSingleResultEntity(financials);
    }

    @GetMapping(path = "/find_stock")
    public ResultEntity findUserStock() {
        String user = getAccountCode();
        List<RankVo> stockUser = userStockService.findStockByUser(user);
        return Results.newListResultEntity(stockUser);
    }

    @GetMapping(path = "/add_stock")
    public ResultEntity addStock(@Param("stock") String stock) {
        String code = getCodeByName(stock);
        ValidationUtils.validateStringParameter(code, "股票代码不能为空");
        String user = getAccountCode();
        UserStock userStock = UserStock.builder().user(user).code(code).build();
        userStockService.saveStock(userStock);
        return Results.newEmptyResultEntity();
    }

    @GetMapping(path = "/delete_stock")
    public ResultEntity deleteStock(@Param("code") String code) {
        String user = getAccountCode();
        ValidationUtils.validateStringParameter(code, "股票代码错误");
        userStockService.deleteStock(code, user);
        return Results.newEmptyResultEntity();
    }


    private String getAccessCode() {
        RequestContext instance = RequestContext.getInstance();
        if (Objects.nonNull(instance)) {
            String accessCode = instance.getAccessCode();
            return StringUtils.isEmpty(accessCode) ? "NOT" : accessCode;
        }
        return "NOT";
    }

    private String getAccountCode() {
        RequestContext instance = RequestContext.getInstance();
        if (Objects.nonNull(instance)) {
            String accountCode = instance.getAccountCode();
            if (!"echo_gou".equals(accountCode)) {
                return accountCode;
            }
        }
        throw new ServiceException(ApplicationErrorMessage.NO_LOGIN);
    }


    private String getCodeByName(String code) {
        String codeName;
        if (code.matches("(6|0|3)[0-9]{5}")) {
            codeName = companyService.getNameByCode(code);
            if (StringUtils.isEmpty(codeName)) {
                return null;
            }
        } else {
            List<Company> company = companyService.findByName(code);
            if (CollectionUtils.isEmpty(company)) {
                return null;
            }
            codeName = company.get(0).getName();
            code = company.get(0).getCode();
        }
        return code;
    }

    @Autowired
    private DiagnosisFlushService diagnosisFlushService;

    @GetMapping(path = "/test")
    public ResultEntity test() {
        diagnosisFlushService.refreshDiagnosisFlush();
        return Results.newEmptyResultEntity();
    }


}
