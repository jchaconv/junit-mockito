package com.example.mockitospringboot.controllers;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.TransactionDto;
import com.example.mockitospringboot.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Account> listAccounts() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    //@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {

        Account account = null;

        try {
            account = accountService.findById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionDto dto) {
        accountService.transfer(dto.getOriginAccountId(),
                dto.getDestinationAccountId(),
                dto.getAmount(), dto.getBankId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Successful transfer!");
        response.put("transaction", dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.deleteById(id);
    }

}
