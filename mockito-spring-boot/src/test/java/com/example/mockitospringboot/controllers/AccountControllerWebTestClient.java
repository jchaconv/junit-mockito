package com.example.mockitospringboot.controllers;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerWebTestClient {

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setOriginAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setBankId(1L);
        dto.setAmount(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Successful transfer!");
        response.put("transaction", dto);

        //client.post().uri("http://localhost:8080/api/accounts/transfer")
        client.post().uri("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(resp -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(resp.getResponseBody());
                        assertEquals("Successful transfer!", jsonNode.path("message").asText());
                        assertEquals(1L, jsonNode.path("transaction").path("originAccountId").asLong());
                        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
                        assertEquals("100", jsonNode.path("transaction").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Successful transfer!"))
                .jsonPath("$.message").value( value -> assertEquals("Successful transfer!", value))
                .jsonPath("$.message").isEqualTo("Successful transfer!")
                .jsonPath("$.transaction.originAccountId").isEqualTo(dto.getOriginAccountId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));

    }

    @Test
    @Order(2)
    void testDetail() throws JsonProcessingException {

        Account account = new Account(1L, "Julio", new BigDecimal("900"));

        client.get().uri("/api/accounts/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Julio")
                .jsonPath("$.balance").isEqualTo(900)
                .json(objectMapper.writeValueAsString(account));
                //Esta última línea se agregó para comparar el json entero de la respuesta
    }

    @Test
    @Order(3)
    void testDetail2() {

        client.get().uri("/api/accounts/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account account = response.getResponseBody();
                    assertEquals("Aaron", account.getPerson());
                    assertEquals("2100.00", account.getBalance().toPlainString());
                });
    }

    @Test
    @Order(4)
    void listTest() {

        client.get().uri("/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("[0].person").isEqualTo("Julio")
                .jsonPath("[0].id").isEqualTo(1)
                .jsonPath("[0].balance").isEqualTo(900)
                .jsonPath("[1].person").isEqualTo("Aaron")
                .jsonPath("[1].id").isEqualTo(2)
                .jsonPath("[1].balance").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));

    }

    @Test
    @Order(5)
    void listTest2() {

        client.get().uri("/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(response -> {
                    List<Account> accounts = response.getResponseBody();
                    assertNotNull(accounts);
                    assertEquals(2, accounts.size());
                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("Julio", accounts.get(0).getPerson());
                    assertEquals(900, accounts.get(0).getBalance().intValue());
                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("Aaron", accounts.get(1).getPerson());
                    assertEquals(2100, accounts.get(1).getBalance().intValue());
                })
                .hasSize(2)
                .value(hasSize(2));
    }


    @Test
    @Order(6)
    void testSave() {

        Account account = new Account(null, "Aaron", new BigDecimal("3000"));

        client.post().uri("api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.person").isEqualTo("Aaron")
                .jsonPath("$.person").value(is("Aaron"))
                .jsonPath("$.balance").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void testSave2() {

        Account account = new Account(null, "Minato", new BigDecimal("3200"));

        client.post().uri("api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account acc = response.getResponseBody();
                    assertNotNull(acc);
                    assertEquals(4L, acc.getId());
                    assertEquals("Minato", acc.getPerson());
                    assertEquals("3200", acc.getBalance().toPlainString());
                });
    }

    @Test
    @Order(8)
    void testDelete() {

        client.get().uri("/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                        .hasSize(4);

        client.delete().uri("/api/accounts/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(3);

        client.get().uri("/api/accounts/3")
                .exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound() //Para que valide que retorne 400
                .expectBody().isEmpty();

    }



}
