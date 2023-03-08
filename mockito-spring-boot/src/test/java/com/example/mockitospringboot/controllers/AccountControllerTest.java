package com.example.mockitospringboot.controllers;

import static com.example.mockitospringboot.Data.*;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.TransactionDto;
import com.example.mockitospringboot.services.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAccountById() throws Exception {
        //GIVEN
        when(accountService.findById(1L))
                .thenReturn(createAccount001()
                        .orElseThrow());

        //WHEN
        mvc.perform(MockMvcRequestBuilders.get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
        //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.person").value("Julio Chacon"))
                .andExpect(jsonPath("$.balance").value("1000"));

        verify(accountService).findById(1L);

    }

    @Test
    void transfer() throws Exception {

        //GIVEN
        TransactionDto dto = new TransactionDto();
        dto.setOriginAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setAmount(new BigDecimal("100"));
        dto.setBankId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Successful transfer!");
        response.put("transaction", dto);

        System.out.println(objectMapper.writeValueAsString(response));

        //WHEN
        mvc.perform(MockMvcRequestBuilders.post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Successful transfer!"))
                .andExpect(jsonPath("$.transaction.originAccountId").value(dto.getOriginAccountId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testList() throws Exception {
        List<Account> accounts = Arrays.asList(createAccount001().orElseThrow(),
                createAccount002().orElseThrow());

        when(accountService.findAll()).thenReturn(accounts);

        mvc.perform(MockMvcRequestBuilders.get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].person").value("Julio Chacon"))
                .andExpect(jsonPath("$[1].person").value("Aaron Chacon"))
                .andExpect(jsonPath("$[0].balance").value("1000"))
                .andExpect(jsonPath("$[1].balance").value("2000"))
                .andExpect(jsonPath("$", hasSize(2))) //Para evaluar la cantidad
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));

        verify(accountService).findAll();

    }

    @Test
    void testSave() throws Exception {
        Account account = new Account(null, "Ruti", new BigDecimal("3800"));
        when(accountService.save(any())).then(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(3L);
            return acc;
        });

        mvc.perform(MockMvcRequestBuilders.post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.person", is("Ruti")))
                .andExpect(jsonPath("$.balance", is(3800)));

        verify(accountService).save(any());

    }
}