package core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.entity.AnalyticFunctionsEntity;
import core.entity.MathFunctionsEntity;
import core.entity.TabulatedFunctionsEntity;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import core.services.MultiSearchService;
import core.services.SingleSearchService;
import core.services.SortSearchService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test_10k")
public class SQL_SearchPerformanceTest {
    private static final int TEST_ITERATIONS = 10000;
    private static final int SEARCH_ITERATIONS = 100;
    private static final String CSV_FILE = "FW-sort-performance.csv";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    @Autowired
    private TabulatedFunctionsRepository tabulatedFunctionRepository;

    @Autowired
    private AnalyticFunctionsRepository analyticFunctionsRepository;

    @Autowired
    private SingleSearchService singleSearchService;

    @Autowired
    private MultiSearchService multiSearchService;

    @Autowired
    private SortSearchService sortSearchService;

    private Long testOwnerId;
    private List<String> testUserNames = new ArrayList<>();
    private List<String> testMathFunctionNames = new ArrayList<>();

    @BeforeAll
    static void setupCsv() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CSV_FILE))) {
            w.write("Метод,Среднее_время_мс,Общее_время_мс");
            w.newLine();
        }
    }

    private void appendResult(String method, double avgMs, double totalMs) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            w.write(String.format(Locale.US, "%s,%.2f,%.2f", method, avgMs, totalMs));
            w.newLine();
        } catch (IOException ignore) {}
    }

    @BeforeEach
    @Transactional
    void prepareTestData() {

        analyticFunctionsRepository.deleteAll();
        tabulatedFunctionRepository.deleteAll();
        mathFunctionsRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity owner = new UserEntity();
        owner.setName("TestOwner");
        owner.setEmail("owner@test.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(true);
        UserEntity savedOwner = userRepository.save(owner);
        testOwnerId = savedOwner.getId();



        testUserNames.clear();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            String userName = "SearchUser_" + i;
            user.setName(userName);
            user.setEmail("search" + i + "@test.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(i % 5 == 0);
            userRepository.save(user);
            testUserNames.add(userName);

            if (i % 100 == 0) System.out.print(".");
        }

        testMathFunctionNames.clear();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            String funcName = "SearchFunc_" + i;
            func.setName(funcName);

            if (i % 3 == 0) {
                func.setType("analytic");
            } else{
                func.setType("tabulated");
            }

            func.setOwner(savedOwner);
            mathFunctionsRepository.save(func);
            testMathFunctionNames.add(funcName);


            if ("analytic".equals(func.getType())) {
                AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
                analyticFunc.setMathFunction(func);
                analyticFunc.setFunctionExpression("x^2 + " + i);
                analyticFunctionsRepository.save(analyticFunc);
            } else if ("tabulated".equals(func.getType())) {
                TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
                tabulatedFunc.setMathFunction(func);
                tabulatedFunc.setXVals(new Double[]{1.0, 2.0, 3.0});
                tabulatedFunc.setYVals(new Double[]{1.0 + i, 4.0 + i, 9.0 + i});
                tabulatedFunctionRepository.save(tabulatedFunc);
            }

            if (i % 100 == 0) System.out.print(".");
        }
        userRepository.flush();
        mathFunctionsRepository.flush();
    }
    @Test
    @Transactional
    void singleSearch_findUserByName() {
        Assertions.assertFalse(testUserNames.isEmpty(), "Должны быть тестовые пользователи");

        Instant start = Instant.now();

        int foundCount = 0;
        for (int i = 0; i < SEARCH_ITERATIONS; i++) {
            String userName = testUserNames.get(i % testUserNames.size());
            Optional<UserEntity> user = singleSearchService.findUserByName(userName);
            if (user.isPresent()) {
                foundCount++;
            }
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" SingleSearch: User by Name", (double) ms / SEARCH_ITERATIONS, (double) ms);
        System.out.println("Found " + foundCount + "/" + SEARCH_ITERATIONS + " users by name");
        Assertions.assertTrue(foundCount > 0, "Должен найти хотя бы одного пользователя");
    }

    @Test
    @Transactional
    void singleSearch_findMathFunctionByName() {
        Assertions.assertFalse(testMathFunctionNames.isEmpty(), "Должны быть тестовые функции");

        Instant start = Instant.now();

        int foundCount = 0;
        for (int i = 0; i < SEARCH_ITERATIONS; i++) {
            String funcName = testMathFunctionNames.get(i % testMathFunctionNames.size());
            Optional<MathFunctionsEntity> func = singleSearchService.findMathFunctionByName(funcName);
            if (func.isPresent()) {
                foundCount++;
            }
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" SingleSearch: MathFunction by Name", (double) ms / SEARCH_ITERATIONS, (double) ms);

        System.out.println("Found " + foundCount + "/" + SEARCH_ITERATIONS + " math functions by name");
        Assertions.assertTrue(foundCount > 0, "Должен найти хотя бы одну функцию");
    }

    @Test
    @Transactional
    void multiSearch_findAllUsers() {
        long totalUsers = userRepository.count();
        System.out.println("Total users in DB: " + totalUsers);
        Instant start = Instant.now();
        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            List<UserEntity> allUsers = multiSearchService.findAllUsers();
            Assertions.assertNotNull(allUsers, "Список пользователей не должен быть null");

        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" MultiSearch: All Users", (double) ms / iterations, (double) ms);
    }

    @Test
    @Transactional
    void multiSearch_findAllAdmins() {
        Instant start = Instant.now();

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            List<UserEntity> admins = multiSearchService.findAllAdmins();
            Assertions.assertNotNull(admins, "Список админов не должен быть null");
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" MultiSearch: All Admins", (double) ms / iterations, (double) ms);
    }

    @Test
    @Transactional
    void multiSearch_findFunctionsByOwnerId() {
        Assertions.assertNotNull(testOwnerId, "testOwnerId должен быть установлен");

        Instant start = Instant.now();

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            List<MathFunctionsEntity> functions = multiSearchService.findFunctionsByOwnerId(testOwnerId);
            Assertions.assertNotNull(functions, "Список функций не должен быть null");
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" MultiSearch: Functions by Owner ID", (double) ms / iterations, (double) ms);
    }



    @Test
    @Transactional
    void sortSearch_findAllUsersSortedByNameAsc() {
        long userCount = userRepository.count();
        System.out.println("Users in DB before sort: " + userCount);

        if (userCount == 0) {
            UserEntity tempUser = new UserEntity();
            tempUser.setName("ZZZ_LastUser");
            tempUser.setEmail("last@test.com");
            tempUser.setPassword(new byte[]{1, 2, 3});
            tempUser.setIsAdmin(false);
            userRepository.save(tempUser);
            userRepository.flush();
            userCount = userRepository.count();
            System.out.println("Created temp user, now total: " + userCount);
        }

        Instant start = Instant.now();
        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
            List<UserEntity> users = sortSearchService.findAllUsersSorted(
                    Sort.Direction.ASC, "name");

            Assertions.assertNotNull(users, "Результат сортировки не должен быть null");
            if (i == 0) {
                System.out.println("Sorted users count: " + users.size());
                if (!users.isEmpty()) {
                    System.out.println("First user after sort ASC: " + users.get(0).getName());
                    System.out.println("Last user after sort ASC: " + users.get(users.size()-1).getName());
                }
            }
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" SortSearch: Users Sorted by Name ASC", (double) ms / iterations, (double) ms);
    }

    @Test
    @Transactional
    void sortSearch_findAllUsersSortedByNameDesc() {
        Instant start = Instant.now();

        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
            List<UserEntity> users = sortSearchService.findAllUsersSorted(
                    Sort.Direction.DESC, "name");

            Assertions.assertNotNull(users, "Результат сортировки не должен быть null");

            if (i == 0 && !users.isEmpty()) {
                System.out.println("First user after sort DESC: " + users.get(0).getName());
            }
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" SortSearch: Users Sorted by Name DESC", (double) ms / iterations, (double) ms);
    }
    @Test
    @Transactional
    void repository_existsByName() {
        Assertions.assertFalse(testUserNames.isEmpty(), "Должны быть тестовые пользователи");

        Instant start = Instant.now();

        int existsCount = 0;
        for (int i = 0; i < SEARCH_ITERATIONS; i++) {
            String userName = testUserNames.get(i % testUserNames.size());
            boolean exists = userRepository.existsByName(userName);
            if (exists) existsCount++;
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" Repository: existsByName", (double) ms / SEARCH_ITERATIONS, (double) ms);

        System.out.println("Exists by name: " + existsCount + "/" + SEARCH_ITERATIONS);
        Assertions.assertTrue(existsCount > 0, "Должен найти существующих пользователей");
    }

    @Test
    @Transactional
    void repository_mathExistsByName() {
        Assertions.assertFalse(testMathFunctionNames.isEmpty(), "Должны быть тестовые функции");

        Instant start = Instant.now();

        int existsCount = 0;
        for (int i = 0; i < SEARCH_ITERATIONS; i++) {
            String funcName = testMathFunctionNames.get(i % testMathFunctionNames.size());
            boolean exists = mathFunctionsRepository.existsByName(funcName);
            if (exists) existsCount++;
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult(" Repository: MathFunction existsByName", (double) ms / SEARCH_ITERATIONS, (double) ms);

        System.out.println("Math functions exist: " + existsCount + "/" + SEARCH_ITERATIONS);
        Assertions.assertTrue(existsCount > 0, "Должен найти существующие функции");
    }
    /*
    @Test
    @Transactional
    void repository_findAnalyticByMathFunctionName() {
        Instant start = Instant.now();

        int foundCount = 0;
        int searches = Math.min(50, SEARCH_ITERATIONS);
        for (int i = 0; i < searches; i++) {
            String funcName = "SearchFunc_" + (i * 3 % TEST_ITERATIONS);
            AnalyticFunctionsEntity analyticFunc = analyticFunctionsRepository.findByMathFunctionName(funcName);
            if (analyticFunc != null) {
                foundCount++;
            }
        }


        long ms = Duration.between(start, Instant.now()).toMillis();
        double avgTime = searches > 0 ? (double) ms / searches : 0;
        appendResult(" Repository: AnalyticFunction by Name", avgTime, (double) ms);

        System.out.println("Found analytic functions: " + foundCount + "/" + searches);
    }
*/
    @Test
    static void displayResults() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("=========================РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ ПОИСКА===============");
        System.out.println("=".repeat(80));

        try {
            List<String> lines = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get(CSV_FILE)
            );

            System.out.println("\n№  Тип теста                                   | Ср. время (мс) | Общее (мс)");
            System.out.println("-".repeat(80));

            int testNum = 1;
            for (String line : lines) {
                if (!line.startsWith("Метод")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String method = parts[0];
                        double avgTime = Double.parseDouble(parts[1]);
                        double totalTime = Double.parseDouble(parts[2]);

                        System.out.printf("%2d. %-40s | %14.2f | %10.0f%n",
                                testNum++, method, avgTime, totalTime);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка чтения CSV файла: " + e.getMessage());
        }

        System.out.println("=".repeat(80));
    }
}