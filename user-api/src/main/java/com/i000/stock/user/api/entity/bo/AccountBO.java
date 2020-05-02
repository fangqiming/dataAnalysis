package com.i000.stock.user.api.entity.bo;

import com.i000.stock.user.dao.model.AccountAsset;
import com.i000.stock.user.dao.model.AccountHold;
import lombok.Data;

import java.util.List;

@Data
public class AccountBO {

    private AccountAsset accountAsset;

    private List<AccountHold> accountHoldList;
}
