package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.RankMapper;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.service.impl.us.PatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class RankService {

    @Autowired
    private RankMapper rankMapper;

    public List<Rank> finaAll() {
        return rankMapper.selectList(null);
    }

    public void save(String content) throws IOException {
        List<Rank> ranks = parseToRank(content);
        if (!CollectionUtils.isEmpty(ranks)) {
            if (canSaveClearRank(ranks.get(0).getDate())) {
                for (Rank rank : ranks) {
                    rankMapper.insert(rank);
                }
            }
        }
    }

    private List<Rank> parseToRank(String content) throws IOException {
        if (!StringUtils.isEmpty(content)) {
            List<Rank> result = new ArrayList<>(3600);
            String[] items = content.split(PatternUtil.LINE.pattern());
            for (String item : items) {
                if (!StringUtils.isEmpty(item)) {
                    result.add(new Rank(item));
                }
            }
            return result;
        }
        return new ArrayList<>(0);
    }

    private boolean canSaveClearRank(LocalDate date) {
        EntityWrapper<Rank> ew = new EntityWrapper<>();
        ew.last("limit 1");
        List<Rank> ranks = rankMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(ranks)) {
            //当天的报告已经存在
            if (date.compareTo(ranks.get(0).getDate()) > 0) {
                rankMapper.truncate();
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public Rank getByCode(String code) {
        return rankMapper.getByCode(code);
    }

}
