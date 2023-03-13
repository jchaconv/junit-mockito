package com.example.mockitospringboot.services;

import com.example.mockitospringboot.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<Account> findAll();

    Account save(Account account);

    void deleteById(Long id);

    Account findById(Long id);

    int reviewTotalTransfers(Long idBank);

    BigDecimal reviewBalance(Long accountId);

    void transfer(Long accountNumberOrigin, Long accountNumberDestination, BigDecimal amount, Long idBank);



}
