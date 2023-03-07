package com.example.mockitospringboot.repositories;

import com.example.mockitospringboot.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {

    /*List<Bank> findAll();

    Bank findById(Long id);

    void update(Bank bank);*/

}
