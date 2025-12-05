package core;

import static org.junit.jupiter.api.Assertions.*;

import core.entity.AnalyticFunctionsEntity;
import core.entity.MathFunctionsEntity;
import core.entity.TabulatedFunctionsEntity;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
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

@SpringBootTest
@ActiveProfiles("test_10k")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQL_BasePerformanceTest {
    private static final int ITERATIONS = 10000;
    private static final String CSV_FILE = "FW-test-performance.csv";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    @Autowired
    private TabulatedFunctionsRepository tabulatedFunctionRepository;

    @Autowired
    private AnalyticFunctionsRepository analyticFunctionsRepository;

    @BeforeAll
    static void setupCsv() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CSV_FILE))) {
            w.write("Метод,\"Время, мс\",\"Cкорость, мс\"");
            w.newLine();
        }
    }

    private void appendResult(String method, double speed,double theworld) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            w.write(String.format(Locale.US, "%s,%.2f,%.2f", method, theworld,speed));
            w.newLine();
        } catch (IOException ignore) {}
    }


    @Test
    @Order(1)
    @Transactional
    void C_User() {
        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("crud_user_" + i);
            user.setEmail("crud_" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            userRepository.save(user);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Create User", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(2)
    @Transactional
    void U_user() {

        Instant start = Instant.now();
        UserEntity user = new UserEntity();
        user.setName("update_test_user");
        user.setEmail("update@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        UserEntity savedUser = userRepository.save(user);

        for (int i = 0; i < ITERATIONS; i++) {
            savedUser.setName("updated_name_" + i);
            userRepository.save(savedUser);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Update User", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(3)
    @Transactional
    void D_user() {
        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            UserEntity user = new UserEntity();
            user.setName("delete_user_" + i);
            user.setEmail("delete_" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity saved = userRepository.save(user);
            userRepository.delete(saved);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Delete User", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(4)
    @Transactional
    void C_MF() {

        UserEntity owner = new UserEntity();
        owner.setName("mathfunc_owner");
        owner.setEmail("owner@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("crud_math_func_" + i);
            func.setType(i % 2 == 0 ? "analytic" : "tabulated");
            func.setOwner(savedOwner);
            mathFunctionsRepository.save(func);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Create MathFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(5)
    @Transactional
    void U_MF() {

        UserEntity owner = new UserEntity();
        owner.setName("update_func_owner");
        owner.setEmail("update_func@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        MathFunctionsEntity func = new MathFunctionsEntity();
        func.setName("func_to_update");
        func.setType("analytic");
        func.setOwner(savedOwner);
        MathFunctionsEntity savedFunc = mathFunctionsRepository.save(func);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            savedFunc.setName("updated_func_name_" + i);
            mathFunctionsRepository.save(savedFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Update MathFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(6)
    @Transactional
    void D_MF() {

        UserEntity owner = new UserEntity();
        owner.setName("delete_func_owner");
        owner.setEmail("delete_func@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("delete_func_" + i);
            func.setType("analytic");
            func.setOwner(savedOwner);
            MathFunctionsEntity saved = mathFunctionsRepository.save(func);
            mathFunctionsRepository.delete(saved);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Delete MathFunction", (double) ms / ITERATIONS,(double) ms);
    }



    @Test
    @Order(7)
    @Transactional
    void C_AF() {


        UserEntity owner = new UserEntity();
        owner.setName("analytic_owner");
        owner.setEmail("analytic@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("AMF" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("x^" + (i % 10) + " + " + i);
            analyticFunctionsRepository.save(analyticFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Create AnalyticFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(8)
    @Transactional
    void U_AF() {

        UserEntity owner = new UserEntity();
        owner.setName("update_analytic_owner");
        owner.setEmail("update_analytic@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setName("amfuOBD");
        mathFunc.setType("analytic");
        mathFunc.setOwner(savedOwner);
        MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(savedMathFunc);
        analyticFunc.setFunctionExpression("initial");
        AnalyticFunctionsEntity savedAnalytic = analyticFunctionsRepository.save(analyticFunc);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            savedAnalytic.setFunctionExpression("updated_expr_" + i);
            analyticFunctionsRepository.save(savedAnalytic);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Update AnalyticFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(9)
    @Transactional
    void D_AF() {

        UserEntity owner = new UserEntity();
        owner.setName("delete_analytic_owner");
        owner.setEmail("delete_analytic@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("DELAF" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("x^2 + " + i);
            AnalyticFunctionsEntity savedAnalytic = analyticFunctionsRepository.save(analyticFunc);

            analyticFunctionsRepository.delete(savedAnalytic);
            mathFunctionsRepository.delete(savedMathFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Delete AnalyticFunction", (double) ms / ITERATIONS,(double) ms);
    }
    @Test
    @Order(10)
    @Transactional
    void C_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("tabulated_owner");
        owner.setEmail("tabulated@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("TBMF" + i);
            mathFunc.setType("tabulated");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
            tabulatedFunc.setMathFunction(savedMathFunc);
            tabulatedFunc.setXVals(new Double[]{1.0 + i, 2.0 + i, 3.0 + i});
            tabulatedFunc.setYVals(new Double[]{1.0 + i, 4.0 + i, 9.0 + i});
            tabulatedFunctionRepository.save(tabulatedFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Create TabulatedFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(11)
    @Transactional
    void U_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("update_tabulated_owner");
        owner.setEmail("update_tabulated@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setName("TFtUP");
        mathFunc.setType("tabulated");
        mathFunc.setOwner(savedOwner);
        MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(savedMathFunc);
        tabulatedFunc.setXVals(new Double[]{1.0, 2.0, 3.0});
        tabulatedFunc.setYVals(new Double[]{1.0, 4.0, 9.0});
        TabulatedFunctionsEntity savedTabulated = tabulatedFunctionRepository.save(tabulatedFunc);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            savedTabulated.setXVals(new Double[]{1.0 + i, 2.0 + i, 3.0 + i});
            savedTabulated.setYVals(new Double[]{1.0 + i, 4.0 + i, 9.0 + i});
            tabulatedFunctionRepository.save(savedTabulated);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Update TabulatedFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(12)
    @Transactional
    void D_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("delete_tabulated_owner");
        owner.setEmail("delete_tabulated@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("DelTF" + i);
            mathFunc.setType("tabulated");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
            tabulatedFunc.setMathFunction(savedMathFunc);
            tabulatedFunc.setXVals(new Double[]{1.0, 2.0, 3.0});
            tabulatedFunc.setYVals(new Double[]{1.0, 4.0, 9.0});
            TabulatedFunctionsEntity savedTabulated = tabulatedFunctionRepository.save(tabulatedFunc);

            tabulatedFunctionRepository.delete(savedTabulated);
            mathFunctionsRepository.delete(savedMathFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Delete TabulatedFunction", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(13)
    @Transactional
    void CREATOR() {
        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            UserEntity user = new UserEntity();
            user.setName("complex_user_" + i);
            user.setEmail("complex_" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity savedUser = userRepository.save(user);


            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("complex_func_" + i);
            mathFunc.setType(i % 2 == 0 ? "analytic" : "tabulated");
            mathFunc.setOwner(savedUser);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            if ("analytic".equals(savedMathFunc.getType())) {
                AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
                analyticFunc.setMathFunction(savedMathFunc);
                analyticFunc.setFunctionExpression("x^2 + " + i);
                analyticFunctionsRepository.save(analyticFunc);
            } else {
                TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
                tabulatedFunc.setMathFunction(savedMathFunc);
                tabulatedFunc.setXVals(new Double[]{1.0, 2.0, 3.0});
                tabulatedFunc.setYVals(new Double[]{1.0, 4.0, 9.0});
                tabulatedFunctionRepository.save(tabulatedFunc);
            }
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("Create all:", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(14)
    @Transactional
    void THANOS() {

        UserEntity user = new UserEntity();
        user.setName("cascade_user");
        user.setEmail("cascade@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        UserEntity savedUser = userRepository.save(user);

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("cascade_func_" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedUser);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);


            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("x^2 + " + i);
            analyticFunctionsRepository.save(analyticFunc);


            userRepository.delete(savedUser);


            user = new UserEntity();
            user.setName("cascade_user_" + i);
            user.setEmail("cascade_" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            savedUser = userRepository.save(user);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("DeleteCascade", (double) ms / ITERATIONS,(double) ms);
    }

    @Test
    @Order(15)
    void WHEREISMYRESULTS() {
        System.out.println("\n========== CRUD PERFORMANCE RESULTS ==========");
        try {
            List<String> lines = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get(CSV_FILE)
            );
            System.out.println("CRUD операции (время в мс на операцию):");
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
    }
}