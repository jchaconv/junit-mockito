package com.example.mockitospringboot.services;

import com.example.mockitospringboot.models.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account findById(Long id);

    int reviewTotalTransfers(Long idBank);

    BigDecimal reviewBalance(Long accountId);

    void transfer(Long accountNumberOrigin, Long accountNumberDestination, BigDecimal amount, Long idBank);



}
