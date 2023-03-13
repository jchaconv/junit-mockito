package com.example.mockitospringboot.controllers;

import com.example.mockitospringboot.models.Account;
import com.example.mockitospringboot.models.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Tag("rt_integration")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {

        TransactionDto dto = new TransactionDto();
        dto.setAmount(new BigDecimal("100"));
        dto.setDestinationAccountId(2L);
        dto.setOriginAccountId(1L);
        dto.setBankId(1L);

        /* 1)
        ResponseEntity<String> response = client
                .postForEntity("/api/accounts/transfer", dto, String.class);*/

        /* 2)
        ResponseEntity<String> response = client
                .postForEntity("http://localhost:" + port + "/api/accounts/transfer", dto, String.class);*/

        ResponseEntity<String> response = client
                .postForEntity(createUri("/api/accounts/transfer"), dto, String.class);

        System.out.println(port);
        String json = response.getBody();
        System.out.println(json);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Successful transfer!"));
        assertTrue(json.contains("{\"originAccountId\":1,\"destinationAccountId\":2,\"amount\":100,\"bankId\":1},\"status\":\"OK\"}"));

        //Para poder navegar en los atributos
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Successful transfer!", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaction").path("amount").asText());
        assertEquals(1L, jsonNode.path("transaction").path("originAccountId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("message", "Successful transfer!");
        response2.put("transaction", dto);

        assertEquals(objectMapper.writeValueAsString(response2), json);

    }

    @Test
    @Order(2)
    void testFindById() {
        ResponseEntity<Account> response = client.getForEntity(createUri("/api/accounts/1"), Account.class);
        Account account = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("Julio", account.getPerson());
        assertEquals("900.00", account.getBalance().toPlainString());
        assertEquals(new Account(1L, "Julio", new BigDecimal("900.00")), account);

    }

    @Test
    @Order(3)
    void testList() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertNotNull(accounts);
        assertEquals(2, accounts.size());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("Julio", accounts.get(0).getPerson());
        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
        assertEquals(2L, accounts.get(1).getId());
        assertEquals("Aaron", accounts.get(1).getPerson());
        assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());

        //Para navegar entre elementos del json
        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals(1L, jsonNode.get(0).path("id").asLong());
        assertEquals("Julio", jsonNode.get(0).path("person").asText());
        assertEquals("900.0", jsonNode.get(0).path("balance").asText());
        assertEquals(2L, jsonNode.get(1).path("id").asLong());
        assertEquals("Aaron", jsonNode.get(1).path("person").asText());
        assertEquals("2100.0", jsonNode.get(1).path("balance").asText());

    }

    @Test
    @Order(4)
    void testSave() {

        Account account = new Account(null, "Ruti", new BigDecimal("3800"));
        ResponseEntity<Account> response = client.postForEntity(createUri("/api/accounts"), account, Account.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Account createdAccount = response.getBody();
        assertNotNull(createdAccount);
        assertEquals(3L, createdAccount.getId());
        assertEquals("Ruti", createdAccount.getPerson());
        assertEquals("3800", createdAccount.getBalance().toPlainString());

    }

    @Test
    @Order(5)
    void testDelete() {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());
        assertEquals(3, accounts.size());

        client.delete(createUri("/api/accounts/3"));

        response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        accounts = Arrays.asList(response.getBody());
        assertEquals(2, accounts.size());

        ResponseEntity<Account> response2 = client.getForEntity(createUri("/api/accounts/3"), Account.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertFalse(response2.hasBody());
    }

    @Test
    @Order(6)
    void testDeleteWithExchange() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());
        assertEquals(2, accounts.size());
        System.out.println(objectMapper.writeValueAsString(accounts));

        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 2L);

        ResponseEntity<Void> exchange = client.exchange(createUri("/api/accounts/{id}"),
                HttpMethod.DELETE, null, Void.class, pathVariables);
        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        accounts = Arrays.asList(response.getBody());
        assertEquals(1, accounts.size());

        ResponseEntity<Account> response2 = client.getForEntity(createUri("/api/accounts/2"), Account.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertFalse(response2.hasBody());
    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }


}