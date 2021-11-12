package com.haoren.ioc.mapper;

import com.haoren.ioc.annotation.Component;
import com.haoren.ioc.entity.Account;

@Component
public interface AccountMapper {

    Account selectByCardNo(String id);

    void update(Account account);
}
