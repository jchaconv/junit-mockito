package org.example.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private List<Account> accounts;
    private String name;

    public Bank() {
        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setBank(this);
    }

    public void transfer(Account originAccount,
                         Account destinationAccount,
                         BigDecimal amount) {
        originAccount.debit(amount);
        destinationAccount.credit(amount);
    }

}
