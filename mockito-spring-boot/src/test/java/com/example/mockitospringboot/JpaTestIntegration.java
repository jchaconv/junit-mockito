package com.example.mockitospringboot;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class JpaTestIntegration {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void findById() {
        Optional<Account> account = accountRepository.findById(1L);
        assertTrue(account.isPresent());
        assertEquals("Julio", account.orElseThrow().getPerson());
    }

    @Test
    void findByPerson() {
        Optional<Account> account = accountRepository.findByPerson("Julio");
        assertTrue(account.isPresent());
        assertEquals("Julio", account.orElseThrow().getPerson());
        assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
    }

    @Test
    void findByPersonThrowException() {
        Optional<Account> account = accountRepository.findByPerson("NoExistsUser");

        /*assertThrows(NoSuchElementException.class, () -> {
           account.orElseThrow();
        });*/
        assertThrows(NoSuchElementException.class, account::orElseThrow);

        assertFalse(account.isPresent());
    }

    @Test
    void findAll() {
        List<Account> accounts = accountRepository.findAll();
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }

    @Test
    void testSave() {
        //GIVEN
        Account account = new Account(null, "Ruti", new BigDecimal("3000"));
        accountRepository.save(account);

        //WHEN
        accountRepository.findByPerson("Ruti").orElseThrow();
        //Account savedAccount = accountRepository.save(account);

        //THEN
        assertEquals("Ruti", account.getPerson());
        assertEquals("3000", account.getBalance().toPlainString());
        //assertEquals("3", account.getId());

    }

    @Test
    void testUpdate() {
        //GIVEN
        Account account = new Account(null, "Ruti", new BigDecimal("3000"));

        //WHEN
        Account savedAccount = accountRepository.save(account);

        //THEN
        assertEquals("Ruti", account.getPerson());
        assertEquals("3000", account.getBalance().toPlainString());

        //WHEN
        savedAccount.setBalance(new BigDecimal("3800"));
        savedAccount.setPerson("Ruti Ludena");
        Account updatedAccount = accountRepository.save(savedAccount);

        //THEN
        assertEquals("Ruti Ludena", account.getPerson());
        assertEquals("3800", account.getBalance().toPlainString());

    }

    @Test
    void testDelete() {
        Account account = accountRepository.findById(2L).orElseThrow();
        assertEquals("Aaron", account.getPerson());

        accountRepository.delete(account);

        assertThrows(NoSuchElementException.class, () -> {
            accountRepository.findById(2L).orElseThrow();
        });

        assertEquals(1, accountRepository.findAll().size());
    }
}
