package com.example.mockitospringboot.repositories;

import com.example.mockitospringboot.models.Bank;

import java.util.List;

public interface BankRepository {

    List<Bank> findAll();

    Bank findById(Long id);

    void update(Bank bank);

}
