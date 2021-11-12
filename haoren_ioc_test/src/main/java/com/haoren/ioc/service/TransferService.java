package com.haoren.ioc.service;

import java.math.BigDecimal;

public interface TransferService {

    void transfer(String fromCardNo, String toCardNo, BigDecimal money);
}
