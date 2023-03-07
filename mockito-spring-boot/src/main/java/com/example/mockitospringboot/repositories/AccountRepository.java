package com.example.mockitospringboot.repositories;

import com.example.mockitospringboot.models.Account;

import java.util.List;

public interface AccountRepository {

    List<Account> findAll();

    Account findById(Long id);

    void update(Account account);

}
