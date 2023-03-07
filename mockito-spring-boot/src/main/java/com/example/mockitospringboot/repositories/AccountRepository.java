package com.example.mockitospringboot.repositories;

import com.example.mockitospringboot.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT c FROM Account c WHERE c.person=?1")
    Optional<Account> findByPerson(String person);

    /*List<Account> findAll();

    Account findById(Long id);

    void update(Account account);*/

}
