package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.mapper.PlanUsMapper;
import com.i000.stock.user.dao.model.PlanUs;
import com.i000.stock.user.service.impl.us.PatternUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PlanParse implements Parse {

    @Autowired
    private PlanUsMapper planUsMapper;

    /**
     * 2017/03/30      LONG2   BSBR    SELL    TBD     TBD     1 unit  35.17   Banco Santander Braz | BANKING - Foreign Regional Banks
     * 2017/03/30      SHORT   FSLR    SHORT   TBD     TBD     1 unit  0.22    First Solar Inc | ELECTRONICS - Semiconductor - Speci
     *
     * @param content
     */
    @Override
    public void save(String[] content, LocalDate date) {
        boolean hasPlan = false;
        for (String line : content) {
            if (PatternUtil.DATE.matcher(line).find()) {
                String[] items = line.split(PatternUtil.TAB.pattern());
                if (items.length < 5) {
                    items = line.split(PatternUtil.TWO_BLANK.pattern());
                }
                String name = items[8].split("\\|")[0];
                PlanUs planUs = PlanUs.builder().date(LocalDate.parse(items[0], PatternUtil.DF_SLANT))
                        .type(items[1])
                        .code(items[2])
                        .action(items[3])
                        .name(name)
                        .note(items[8]).build();
                hasPlan = true;
                planUsMapper.insert(planUs);
            }
        }
        if (!hasPlan) {
            PlanUs planUs = PlanUs.builder().date(date).build();
            planUsMapper.insert(planUs);
        }
    }
}
