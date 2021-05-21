package com.i000.stock.user.web.service.account;

import com.i000.stock.user.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PictureParseHelp {

    @Resource
    private Ib ib;

    @Resource
    private Cats cats;

    @Resource
    private Schwab schwab;

    @Resource
    private Xc xc;

    private Map<String, IPicture> MAP_HELP = new HashMap<>();

    @PostConstruct
    public void init() {
        MAP_HELP.put("lqjj", cats);     //刘桥基金
        MAP_HELP.put("jhai", cats);     //君合AI
        MAP_HELP.put("lhzq", ib);       //老虎证券
        MAP_HELP.put("xyzq", ib);       //雪盈证券
        MAP_HELP.put("jxzq", schwab);   //嘉信证券
        MAP_HELP.put("jxira", schwab);  //嘉信IRA
        MAP_HELP.put("yczh", xc);       //湘财账户
    }

    public IPicture get(String url) {
        Set<String> keys = MAP_HELP.keySet();
        for (String key : keys) {
            if (url.contains(key)) {
                return MAP_HELP.get(key);
            }
        }
        throw new ServiceException(1234567L, "无法找到对应的图片解析类" + url);
    }
}
