package com.i000.stock.user.service.impl.external.macro;

import com.i000.stock.user.dao.model.Cpi;
import com.i000.stock.user.dao.model.NewStockAccount;
import com.i000.stock.user.dao.model.Pmi;
import com.i000.stock.user.dao.model.Ppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MacroService {

    @Autowired
    private CpiService cpiService;

    @Autowired
    private NewStockAccountService newStockAccountService;

    @Autowired
    private PmiService pmiService;

    @Autowired
    private PpiService ppiService;

    public void updateData() {
        cpiService.save();
        newStockAccountService.save();
        pmiService.save();
        ppiService.save();
    }


    public List<Cpi> findCpi() {
        return cpiService.find();
    }

    public List<NewStockAccount> findNewStockAccount() {
        return newStockAccountService.find();
    }

    public List<Pmi> findPmi() {
        return pmiService.find();
    }


    public List<Ppi> findPpi() {
        return ppiService.find();
    }

}
