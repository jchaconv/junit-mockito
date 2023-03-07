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
        return accountRepository.findById(id);
    }

    @Override
    public int reviewTotalTransfers(Long idBank) {
        Bank bank = bankRepository.findById(idBank);
        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal reviewBalance(Long accountId) {
        Account account = accountRepository.findById(accountId);
        return account.getBalance();
    }

    @Override
    public void transfer(Long accountNumberOrigin, Long accountNumberDestination, BigDecimal amount, Long idBank) {

        Account originAccount = accountRepository.findById(accountNumberOrigin);
        originAccount.debit(amount);
        accountRepository.update(originAccount);

        Account destinationAccount = accountRepository.findById(accountNumberDestination);
        destinationAccount.credit(amount);
        accountRepository.update(destinationAccount);

        Bank bank = bankRepository.findById(idBank);
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.update(bank);

    }
}
