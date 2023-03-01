package org.example.models;

import org.example.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assumptions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LifecycleTest {

    Account account;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando el test");
    }

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.account = new Account("Julio", new BigDecimal("1000.33"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el método con BeforeEach");
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método con AfterEach");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Nested
    @Tag("account")
    class AccountNameBalanceTest {

        @Test
        @DisplayName("Nombre de la cuenta corriente")
        void accountNameTest() {
            //Account account = new Account("Julio", new BigDecimal("1000.33"));
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("account"))
                System.out.println("Se encontró el tag account");
            String expectedName = "Julio";
            String currentName = account.getPerson();
            assertNotNull(currentName);
            assertEquals(expectedName, currentName, () -> "El nombre debería ser Julio");
            assertTrue(currentName.equalsIgnoreCase("Julio"));
        }

        @Test
        void balanceAccountTest() {
            account = new Account("Aaron", new BigDecimal("542.256"));
            assertNotNull(account.getBalance());
            assertEquals(542.256, account.getBalance().doubleValue());
            assertFalse(account.getBalance().compareTo(new BigDecimal(0)) < 0); //1
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0); //1
        }

        @Test
        void accountReferenceTest() {
            Account account1 = new Account("Julio Cesar", new BigDecimal("8900.5477"));
            Account account2 = new Account("Julio Cesar", new BigDecimal("8900.5477"));

            assertEquals(account1, account2);
        }

    }

    @Nested
    @DisplayName("Operaciones en cuenta")
    class AccountOperations {

        @Tag("account")
        @RepeatedTest(value = 5, name = "{displayName} - repeticion {currentRepetition} de {totalRepetitions}")
        @DisplayName("Debito en cuenta")
        void debitAccountTest(RepetitionInfo info) {
            if(info.getCurrentRepetition()==3) System.out.println("estamos en la repeticion 3");
            account = new Account("Julio Chacon", new BigDecimal("1890.562"));
            account.debit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(1790, account.getBalance().intValue());
            assertEquals("1790.562", account.getBalance().toPlainString());
        }

        @Test
        @DisplayName("Credito en cuenta")
        @Tag("account")
        void creditAccountTest() {
            account = new Account("Julio Chacon", new BigDecimal("1890.562"));
            account.credit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(1990, account.getBalance().intValue());
            assertEquals("1990.562", account.getBalance().toPlainString());
        }

        @Test
        @DisplayName("Transferencia entre cuentas")
        @Tag("account")
        @Tag("bank")
        void transferMoneyFromAccountTest() {

            Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
            Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

            Bank bank = new Bank();
            bank.setName("Scotiabank");
            bank.transfer(account1, account2, new BigDecimal(500));

            assertEquals("2000", account1.getBalance().toPlainString());
            assertEquals("3700.65", account2.getBalance().toPlainString());

        }

    }

    @Test
    void insufficientMoneyTest() {
        account = new Account("Julio Chacon", new BigDecimal("1890.562"));
        Exception exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal(1900));
        });
        String currentMessage = exception.getMessage();
        String expectedMessage = "Insufficient Money";
        assertEquals(currentMessage, expectedMessage);
    }

    @Test
    void relationBankAccountsTest() {

        Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
        Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

        Bank bank = new Bank();
        bank.setName("Scotiabank");
        bank.addAccount(account1);
        bank.addAccount(account2);

        bank.transfer(account1, account2, new BigDecimal(500));

        assertEquals("2000", account1.getBalance().toPlainString());
        assertEquals("3700.65", account2.getBalance().toPlainString());

        assertEquals(2, bank.getAccounts().size());

        assertEquals("Scotiabank", account1.getBank().getName());

        assertEquals("Rut Ludena", bank.getAccounts().stream()
                .filter(c -> c.getPerson().equals("Rut Ludena"))
                .findFirst().get().getPerson());

        assertTrue(bank.getAccounts().stream()
                .filter(c -> c.getPerson().equals("Rut Ludena"))
                .findFirst().isPresent());

        assertTrue(bank.getAccounts().stream()
                .anyMatch(c -> c.getPerson().equals("Rut Ludena")));
    }

    @Test
    void assertAllTest() {

        Account account1 = new Account("Julio Chacon", new BigDecimal("2500"));
        Account account2 = new Account("Rut Ludena", new BigDecimal("3200.65"));

        Bank bank = new Bank();
        bank.setName("Scotiabank");
        bank.addAccount(account1);
        bank.addAccount(account2);

        bank.transfer(account1, account2, new BigDecimal(500));

        assertAll(
                () -> assertEquals("2000", account1.getBalance().toPlainString()),
                () -> assertEquals("3700.65", account2.getBalance().toPlainString()),
                () -> assertEquals(2, bank.getAccounts().size()),
                () -> assertEquals("Scotiabank", account1.getBank().getName()),
                () -> assertEquals("Rut Ludena", bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Rut Ludena"))
                        .findFirst().get().getPerson()),
                () -> assertTrue(bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Rut Ludena"))
                        .findFirst().isPresent()),
                () -> assertTrue(bank.getAccounts().stream()
                        .anyMatch(c -> c.getPerson().equals("Rut Ludena")))
        );

    }

    @Nested
    class OperativeSystemTest {

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void onlyWindowsTest() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void notWindowsTest() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void onlyLinuxMacTest() {
        }

    }

    @Nested
    class JavaVersionTest {

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void onlyJdk11() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void onlyJdk8() {

        }

    }

    @Nested
    class EnvironmentVariableTest {

        @Test
        //@EnabledIfSystemProperty(named = "java.version", matches = "11.0.171")
        @EnabledIfSystemProperty(named = "java.version", matches = "11.*.*")
        void printSystemProperties() {

            Properties properties = System.getProperties();
            properties.forEach( (k,v) -> System.out.println(k + " : " + v));

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32")
        void only64Test() {

            Properties properties = System.getProperties();
            properties.forEach( (k,v) -> System.out.println(k + " : " + v));

        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void devEnvSystemPropertyTest() {

        }

        @Test
        void printEnvironmentVariables() {
            Map<String, String> environmentVariables = System.getenv();
            environmentVariables.forEach( (k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.17.*")
        void javaHomeTest() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "DEV")
        void devEnvEnvironmentVariableTest() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
        void prodEnvEnvironmentVariableTest() {
        }

    }

    @Test
    void balanceAccountTestDev() {

        boolean isDevEnvironment = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDevEnvironment);

        account = new Account("Aaron", new BigDecimal("542.256"));
        assertNotNull(account.getBalance());
        assertEquals(542.256, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(new BigDecimal(0)) < 0); //1
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0); //1

    }

    @Test
    void balanceAccountTestDev2() {

        boolean isDevEnvironment = "prod".equals(System.getProperty("ENV"));

        assumingThat(isDevEnvironment, () -> {
            account = new Account("Aaron", new BigDecimal("542.256"));
            assertNotNull(account.getBalance());
            assertEquals(542.256, account.getBalance().doubleValue());
        });

        System.out.println("Fuera del assumingThat");
        assertFalse(account.getBalance().compareTo(new BigDecimal(0)) < 0);
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);

    }

    @Nested
    @Tag("param")
    class ParametrizedTest {

        @DisplayName("Parameterized value source test") //falla cuando en lugar de 1000 se pone 1000.33
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100","200","300","500","700","1000"}) //puede ser doubles o ints también
        void parameterizedValueSourceTest(String amount) {
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Parameterized csv source test")
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000"})
        void parameterizedCsvSourceTest(String index, String amount) {
            System.out.println(index + " -> " + amount);
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Parameterized csv source test 2")
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,Julio,Julio","250,200,Rut,Rut","350,300,Aaron,Aaron","510,500,Julio,Julio", "750,700,Aaron,Aaron", "1001.12345, 1000.12345,Cesar,Cesar"})
        void parameterizedCsvSourceTest2(String balance, String amount, String expectedName, String currentName) {
            System.out.println(balance + " -> " + amount);
            account.setBalance(new BigDecimal(balance));
            account.debit(new BigDecimal(amount));
            account.setPerson(currentName);

            assertNotNull(account.getBalance());
            assertNotNull(account.getPerson());
            assertEquals(expectedName, currentName);

            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Parameterized csv file source test")
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void parameterizedCsvFileSourceTest(String amount) {
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Parameterized csv file source test 2")
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void parameterizedCsvFileSourceTest2(String balance, String amount, String expectedName, String currentName) {

            account.setBalance(new BigDecimal(balance));
            account.debit(new BigDecimal(amount));
            account.setPerson(currentName);

            assertNotNull(account.getBalance());
            assertNotNull(account.getPerson());
            assertEquals(expectedName, currentName);

            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }


    }

    @Tag("param")
    @DisplayName("Parameterized method source test")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("setAmountList")
    void parameterizedMethodSourceTest(String amount) {
        account.debit(new BigDecimal(amount));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    private static List<String> setAmountList() {
        return Arrays.asList("100","200","300","500","700","1000");
    }

    @Nested
    @Tag("timeout")
    class ExampleTimeoutTest {

        @Test
        @Timeout(2) //Por defecto es segundos
        void testTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void testTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }

}
