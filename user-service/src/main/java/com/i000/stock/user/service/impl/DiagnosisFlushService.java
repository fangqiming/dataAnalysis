package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.DiagnosisFlushMapper;
import com.i000.stock.user.dao.model.DiagnosisFlush;
import com.i000.stock.user.dao.model.Rank;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用来获取同花顺的诊断数据。
 */
@Slf4j
@Service
public class DiagnosisFlushService {

    private static final String URL = "http://doctor.10jqka.com.cn/%s/";

    @Autowired
    private DiagnosisFlushMapper diagnosisFlushMapper;

    @Autowired
    private RankService rankService;

    public void refreshDiagnosisFlush() {
        cleanDiagnosisFlush();
        List<Rank> allRank = rankService.finaAll();
        for (Rank rank : allRank) {
            BigDecimal score = getScoreFromNet(rank.getCode());
            BigDecimal flushScore = score.multiply(new BigDecimal(10));
            BigDecimal aiScore = new BigDecimal(100).subtract(rank.getScore());
            DiagnosisFlush diagnosisFlush = DiagnosisFlush.builder()
                    .code(rank.getCode()).date(LocalDate.now())
                    .flushScore(flushScore)
                    .aiScore(aiScore)
                    .totalScore(flushScore.add(aiScore))
                    .build();
            System.out.println(diagnosisFlush);
            diagnosisFlushMapper.insert(diagnosisFlush);
        }
    }

    public BigDecimal getScoreFromNet(String code) {
        try {
            String url = String.format(URL, code);
            Document doc;
            doc = Jsoup.connect(url).get();
            String bigNumber = doc.getElementsByClass("bignum").get(0).html();
            String smallNumber = doc.getElementsByClass("smallnum").get(0).html();
            //延迟两秒钟，防止被同花顺
            Thread.sleep(2000);
            return new BigDecimal(bigNumber + smallNumber);
        } catch (Exception e) {
            log.warn(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 从同花顺获取 " + code + " 的诊断分数时访问失败");
            return BigDecimal.ZERO;
        }
    }

    public List<DiagnosisFlush> findByCodes(List<String> codes) {
        EntityWrapper<DiagnosisFlush> ew = new EntityWrapper<>();
        ew.in("code", codes);
        return diagnosisFlushMapper.selectList(ew);
    }

    private void cleanDiagnosisFlush() {
        diagnosisFlushMapper.clean();
    }

}
