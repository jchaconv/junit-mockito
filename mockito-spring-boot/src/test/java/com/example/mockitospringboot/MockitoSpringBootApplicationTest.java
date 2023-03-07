package com.example.mockitospringboot;

import com.example.mockitospringboot.exceptions.InsufficientMoneyException;
import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.Bank;
import com.example.mockitospringboot.repositories.AccountRepository;
import com.example.mockitospringboot.repositories.BankRepository;
import com.example.mockitospringboot.services.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static com.example.mockitospringboot.Data.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class MockitoSpringBootApplicationTest {

    @MockBean
    AccountRepository accountRepository;

    @MockBean
    BankRepository bankRepository;

    @Autowired
    AccountService accountService;

    @Test
    void contextLoads() {
        when(accountRepository.findById(1L)).thenReturn(createAccount001());
        when(accountRepository.findById(2L)).thenReturn(createAccount002());
        when(bankRepository.findById(1L)).thenReturn(createBank());

        BigDecimal originBalance = accountService.reviewBalance(1L);
        BigDecimal destinationBalance = accountService.reviewBalance(2L);
        assertEquals("1000", originBalance.toPlainString());
        assertEquals("2000", destinationBalance.toPlainString());

        accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);

        originBalance = accountService.reviewBalance(1L);
        destinationBalance = accountService.reviewBalance(2L);

        assertEquals("900", originBalance.toPlainString());
        assertEquals("2100", destinationBalance.toPlainString());

        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(3)).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));

        int total = accountService.reviewTotalTransfers(1L);
        assertEquals(1, total);

        verify(bankRepository, times(2)).findById(1L);
        verify(bankRepository).save(any(Bank.class));

        verify(accountRepository, times(6)).findById(anyLong());
        verify(accountRepository, never()).findAll();

    }

    @Test
    void contextLoads2() {
        when(accountRepository.findById(1L)).thenReturn(createAccount001());
        when(accountRepository.findById(2L)).thenReturn(createAccount002());
        when(bankRepository.findById(1L)).thenReturn(createBank());

        BigDecimal originBalance = accountService.reviewBalance(1L);
        BigDecimal destinationBalance = accountService.reviewBalance(2L);
        assertEquals("1000", originBalance.toPlainString());
        assertEquals("2000", destinationBalance.toPlainString());

        assertThrows(InsufficientMoneyException.class, () -> {
            accountService.transfer(1L, 2L, new BigDecimal("1200"), 1L);
        });

        originBalance = accountService.reviewBalance(1L);
        destinationBalance = accountService.reviewBalance(2L);

        assertEquals("1000", originBalance.toPlainString());
        assertEquals("2000", destinationBalance.toPlainString());

        int total = accountService.reviewTotalTransfers(1L);
        assertEquals(0, total);

        verify(accountRepository, times(3)).findById(1L);
        verify(accountRepository, times(2)).findById(2L);
        verify(accountRepository, never()).save(any(Account.class));

        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, never()).save(any(Bank.class));

    }

    @Test
    void contextLoads3() {

        when(accountRepository.findById(1L)).thenReturn(createAccount001());

        Account account1 = accountService.findById(1L);
        Account account2 = accountService.findById(1L);

        assertSame(account2, account1);

        assertEquals("Julio Chacon", account1.getPerson());
        assertEquals("Julio Chacon", account2.getPerson());

        verify(accountRepository, times(2)).findById(1L);

    }

}

