package com.example.mockitospringboot.services;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.Bank;
import com.example.mockitospringboot.repositories.AccountRepository;
import com.example.mockitospringboot.repositories.BankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public Account findById(Long id) {
        //return accountRepository.findById(id);
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    public int reviewTotalTransfers(Long idBank) {
        Bank bank = bankRepository.findById(idBank).orElseThrow();
        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal reviewBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getBalance();
    }

    @Override
    public void transfer(Long accountNumberOrigin, Long accountNumberDestination, BigDecimal amount, Long idBank) {

        Account originAccount = accountRepository.findById(accountNumberOrigin).orElseThrow();
        originAccount.debit(amount);
        accountRepository.save(originAccount);

        Account destinationAccount = accountRepository.findById(accountNumberDestination).orElseThrow();
        destinationAccount.credit(amount);
        accountRepository.save(destinationAccount);

        Bank bank = bankRepository.findById(idBank).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);
    }

}
