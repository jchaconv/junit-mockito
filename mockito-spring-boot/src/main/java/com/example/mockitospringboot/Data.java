package com.example.mockitospringboot;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.Bank;

import java.math.BigDecimal;
import java.util.Optional;

public class Data {

    /*
    public static final Account ACCOUNT_001 = new Account(1L, "Julio Chacon", new BigDecimal("1000"));
    public static final Account ACCOUNT_002 = new Account(2L, "Aaron Chacon", new BigDecimal("2000"));
    //public static final Account ACCOUNT_003 = new Account(3L, "Rut Ludena", new BigDecimal("3000"));

    public static final Bank BANK = new Bank(1L, "Interbank", 0);
    */
    public static Optional<Account> createAccount001() {
        return Optional.of(new Account(1L, "Julio Chacon", new BigDecimal("1000")));
    }

    public static Optional<Account> createAccount002() {
        return Optional.of(new Account(2L, "Aaron Chacon", new BigDecimal("2000")));
    }

    public static Optional<Bank> createBank() {
        return Optional.of(new Bank(1L, "El banco financiero", 0));
    }


}
