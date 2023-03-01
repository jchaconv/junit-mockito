package org.example.models;

import org.example.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {

    @Test
    @DisplayName("Nombre de la cuenta corriente")
    void accountNameTest() {

        /* To test getters y setters
        Account account = new Account();
        account.setPerson("Julio");*/

        Account account = new Account("Julio", new BigDecimal("1000.33"));

        String expectedName = "Julio";
        String currentName = account.getPerson();
        assertNotNull(currentName);
        //Assertions.assertEquals(expectedName, currentName);
        //assertEquals(expectedName, currentName, "El nombre debería ser Julio");
        assertEquals(expectedName, currentName, () -> "El nombre debería ser Julio");
        assertTrue(currentName.equalsIgnoreCase("Julio"));

    }

    @Test
    void balanceAccountTest() {
        Account account = new Account("Aaron", new BigDecimal("542.256"));
        assertNotNull(account.getBalance());
        assertEquals(542.256, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(new BigDecimal(0)) < 0); //1
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0); //1
    }

    @Test
    void accountReferenceTest() {
        Account account1 = new Account("Julio Cesar", new BigDecimal("8900.5477"));
        Account account2 = new Account("Julio Cesar", new BigDecimal("8900.5477"));

        assertEquals(account1, account2);
    }

    @Test
    void debitAccountTest() {
        Account account = new Account("Julio Chacon", new BigDecimal("1890.562"));
        account.debit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(1790, account.getBalance().intValue());
        assertEquals("1790.562", account.getBalance().toPlainString());
    }

    @Test
    void creditAccountTest() {
        Account account = new Account("Julio Chacon", new BigDecimal("1890.562"));
        account.credit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(1990, account.getBalance().intValue());
        assertEquals("1990.562", account.getBalance().toPlainString());
    }

    @Test
    void insufficientMoneyTest() {
        Account account = new Account("Julio Chacon", new BigDecimal("1890.562"));
        Exception exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal(1900));
        });
        String currentMessage = exception.getMessage();
        String expectedMessage = "Insufficient Money";
        assertEquals(currentMessage, expectedMessage);
    }

    @Test
    void transferMoneyFromAccountTest() {

        Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
        Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

        Bank bank = new Bank();
        bank.setName("Scotiabank");
        bank.transfer(account1, account2, new BigDecimal(500));

        assertEquals("2000", account1.getBalance().toPlainString());
        assertEquals("3700.65", account2.getBalance().toPlainString());

    }

    @Test
    void relationBankAccountsTest() {

        Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
        Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

        Bank bank = new Bank();
        bank.setName("Scotiabank");
        bank.addAccount(account1);
        bank.addAccount(account2);

        bank.transfer(account1, account2, new BigDecimal(500));

        assertEquals("2000", account1.getBalance().toPlainString());
        assertEquals("3700.65", account2.getBalance().toPlainString());

        assertEquals(2, bank.getAccounts().size());

        assertEquals("Scotiabank", account1.getBank().getName());

        assertEquals("Rut Ludena", bank.getAccounts().stream()
                .filter(c -> c.getPerson().equals("Rut Ludena"))
                .findFirst().get().getPerson());

        assertTrue(bank.getAccounts().stream()
                .filter(c -> c.getPerson().equals("Rut Ludena"))
                .findFirst().isPresent());

        assertTrue(bank.getAccounts().stream()
                .anyMatch(c -> c.getPerson().equals("Rut Ludena")));
    }

    @Test
    @Disabled
    void assertAllTest() {

        fail();

        Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
        Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

        Bank bank = new Bank();
        bank.setName("Scotiabank");
        bank.addAccount(account1);
        bank.addAccount(account2);

        bank.transfer(account1, account2, new BigDecimal(500));

        assertAll(
                () -> assertEquals("2000", account1.getBalance().toPlainString()),
                () -> assertEquals("3700.65", account2.getBalance().toPlainString()),
                () -> assertEquals(2, bank.getAccounts().size()),
                () -> assertEquals("Scotiabank", account1.getBank().getName()),
                () -> assertEquals("Rut Ludena", bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Rut Ludena"))
                        .findFirst().get().getPerson()),
                () -> assertTrue(bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Rut Ludena"))
                        .findFirst().isPresent()),
                () -> assertTrue(bank.getAccounts().stream()
                        .anyMatch(c -> c.getPerson().equals("Rut Ludena")))
        );

    }





}