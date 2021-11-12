package com.haoren.ioc.service.impl;

import com.haoren.ioc.annotation.Autowired;
import com.haoren.ioc.annotation.Service;
import com.haoren.ioc.entity.Account;
import com.haoren.ioc.mapper.AccountMapper;
import com.haoren.ioc.service.TransferService;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    AccountMapper accountMapper;

    @Override
    public void transfer(String fromCardNo, String toCardNo, BigDecimal money) {
        Account from = accountMapper.selectByCardNo(fromCardNo);
        Account to = accountMapper.selectByCardNo(toCardNo);

        from.setMonty(from.getMonty().subtract(money));
        to.setMonty(to.getMonty().add(money));

        accountMapper.update(from);
        accountMapper.update(to);
    }
}
