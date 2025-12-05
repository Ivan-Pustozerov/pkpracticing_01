package core;

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
    @Test
    @Order(1)
    @Transactional
    void C_User() {
        Instant start = Instant.now();

        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("Anon" + i);
            user.setEmail("dungeon" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            userRepository.save(user);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("1. Create User", (double) ms / ITERATIONS, (double) ms);
    }

    @Test
    @Order(2)
    @Transactional
    void R_User() {
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("MachoMan" + i);
            user.setEmail("Macho" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity saved = userRepository.save(user);
            userIds.add(saved.getId());
        }
        userRepository.flush();

        Instant start = Instant.now();

        for (Long id : userIds) {
            userRepository.findById(id);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("2. Read User", (double) ms / ITERATIONS, (double) ms);
        for (Long id : userIds) {
            userRepository.deleteById(id);
        }
    }

    @Test
    @Order(3)
    @Transactional
    void U_User() {
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("OPTIMUSPRIME" + i);
            user.setEmail("ssau" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            users.add(userRepository.save(user));
        }
        userRepository.flush();
        Instant start = Instant.now();

        for (int i = 0; i < users.size(); i++) {
            UserEntity user = users.get(i);
            user.setName("NAPOLEON" + i);
            userRepository.save(user); // Это UPDATE, клянусь
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("3. Update User", (double) ms / ITERATIONS, (double) ms);
        for (UserEntity user : users) {
            userRepository.delete(user);
        }
    }

    @Test
    @Order(4)
    @Transactional
    void D_User() {
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("Lol" + i);
            user.setEmail("MAN" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity saved = userRepository.save(user);
            userIds.add(saved.getId());
        }
        userRepository.flush();
        Instant start = Instant.now();
        for (Long id : userIds) {
            userRepository.deleteById(id);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("4. Delete User", (double) ms / ITERATIONS, (double) ms);
    }
    @Test
    @Order(5)
    @Transactional
    void C_MF() {
        UserEntity owner = new UserEntity();
        owner.setName("mf_owner");
        owner.setEmail("owner@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);
        Instant start = Instant.now();

        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("GIMYMNy" + i);
            func.setType(i % 2 == 0 ? "analytic" : "tabulated");
            func.setOwner(savedOwner);
            mathFunctionsRepository.save(func);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("5. Create MathFunction", (double) ms / ITERATIONS, (double) ms);
        userRepository.delete(savedOwner);
    }

    @Test
    @Order(6)
    @Transactional
    void R_MF() {
        UserEntity owner = new UserEntity();
        owner.setName("DR_GASTER");
        owner.setEmail("WD@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<Long> mfIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("exper" + i);
            func.setType("analytic");
            func.setOwner(savedOwner);
            MathFunctionsEntity saved = mathFunctionsRepository.save(func);
            mfIds.add(saved.getId());
        }
        mathFunctionsRepository.flush();


        Instant start = Instant.now();
        for (Long id : mfIds) {
            mathFunctionsRepository.findById(id);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("6. Read MathFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(7)
    @Transactional
    void U_MF() {

        UserEntity owner = new UserEntity();
        owner.setName("mf_update_owner");
        owner.setEmail("update_owner@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<MathFunctionsEntity> functions = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("mf_for_update_" + i);
            func.setType("analytic");
            func.setOwner(savedOwner);
            functions.add(mathFunctionsRepository.save(func));
        }
        mathFunctionsRepository.flush();

        Instant start = Instant.now();

        for (int i = 0; i < functions.size(); i++) {
            MathFunctionsEntity func = functions.get(i);
            func.setName("updated_mf_name_" + i);
            mathFunctionsRepository.save(func);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("7. Update MathFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(8)
    @Transactional
    void D_MF() {
        UserEntity owner = new UserEntity();
        owner.setName("MR.Afton");
        owner.setEmail("FFP@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<Long> mfIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("Henry" + i);
            func.setType("analytic");
            func.setOwner(savedOwner);
            MathFunctionsEntity saved = mathFunctionsRepository.save(func);
            mfIds.add(saved.getId());
        }
        mathFunctionsRepository.flush();

        Instant start = Instant.now();
        for (Long id : mfIds) {
            mathFunctionsRepository.deleteById(id);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("8. Delete MathFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(9)
    @Transactional
    void C_AF() {
        UserEntity owner = new UserEntity();
        owner.setName("Megatron");
        owner.setEmail("Dec@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);


        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("Generator" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("x^" + (i % 10) + " + " + i);
            analyticFunctionsRepository.save(analyticFunc);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("9. Create AnalyticFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(10)
    @Transactional
    void R_AF() {
        UserEntity owner = new UserEntity();
        owner.setName("ILSSAU");
        owner.setEmail("PMI@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);
        List<Long> afIds = new ArrayList<>();

        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("MF" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);
            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("4+1+1" + i);
            AnalyticFunctionsEntity savedAf = analyticFunctionsRepository.save(analyticFunc);
            afIds.add(savedAf.getId());
        }
        analyticFunctionsRepository.flush();


        Instant start = Instant.now();
        for (Long id : afIds) {
            analyticFunctionsRepository.findById(id);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("10. Read AnalyticFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(11)
    @Transactional
    void U_AF() {

        UserEntity owner = new UserEntity();
        owner.setName("DarkWingDuck");
        owner.setEmail("DWD@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<AnalyticFunctionsEntity> afList = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("MF" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("YYY" + i);
            afList.add(analyticFunctionsRepository.save(analyticFunc));
        }
        analyticFunctionsRepository.flush();

        Instant start = Instant.now();

        for (int i = 0; i < afList.size(); i++) {
            AnalyticFunctionsEntity af = afList.get(i);
            af.setFunctionExpression("NEW" + i);
            analyticFunctionsRepository.save(af);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("11. Update AnalyticFunction", (double) ms / ITERATIONS, (double) ms);


        userRepository.delete(savedOwner);
    }

    @Test
    @Order(12)
    @Transactional
    void D_AF() {

        UserEntity owner = new UserEntity();
        owner.setName("Sans");
        owner.setEmail("JustSans@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<Long> afIds = new ArrayList<>();
        List<Long> mfIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("MF" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);
            mfIds.add(savedMathFunc.getId());

            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("none" + i);
            AnalyticFunctionsEntity savedAf = analyticFunctionsRepository.save(analyticFunc);
            afIds.add(savedAf.getId());
        }
        analyticFunctionsRepository.flush();

        Instant start = Instant.now();

        for (Long id : afIds) {
            analyticFunctionsRepository.deleteById(id);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("12. Delete AnalyticFunction", (double) ms / ITERATIONS, (double) ms);

        for (Long id : mfIds) {
            mathFunctionsRepository.deleteById(id);
        }
        userRepository.delete(savedOwner);
    }

    @Test
    @Order(13)
    @Transactional
    void C_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("Iforgot");
        owner.setEmail("XD@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);


        Instant start = Instant.now();

        for (int i = 0; i < ITERATIONS; i++) {

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("Chad" + i);
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
        appendResult("13. Create TabulatedFunction", (double) ms / ITERATIONS, (double) ms);

        userRepository.delete(savedOwner);
    }

    @Test
    @Order(14)
    @Transactional
    void U_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("UTF");
        owner.setEmail("UTF@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        TabulatedFunctionsEntity tf = null;
        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setName("UTF-8");
        mathFunc.setType("tabulated");
        mathFunc.setOwner(savedOwner);
        MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

        tf = new TabulatedFunctionsEntity();
        tf.setMathFunction(savedMathFunc);
        tf.setXVals(new Double[]{1.0, 2.0, 3.0});
        tf.setYVals(new Double[]{1.0, 4.0, 9.0});
        TabulatedFunctionsEntity savedTf = tabulatedFunctionRepository.save(tf);
        tabulatedFunctionRepository.flush();

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            savedTf.setXVals(new Double[]{1.0 + i, 2.0 + i, 3.0 + i});
            savedTf.setYVals(new Double[]{1.0 + i, 4.0 + i, 9.0 + i});
            tabulatedFunctionRepository.save(savedTf);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("14. Update TabulatedFunction", (double) ms / ITERATIONS, (double) ms);
        userRepository.delete(savedOwner);
    }

    @Test
    @Order(15)
    @Transactional
    void D_TF() {

        UserEntity owner = new UserEntity();
        owner.setName("Kuplinov");
        owner.setEmail("Best@example.com");
        owner.setPassword(new byte[]{1, 2, 3, 4});
        owner.setIsAdmin(false);
        UserEntity savedOwner = userRepository.save(owner);

        List<Long> tfIds = new ArrayList<>();
        List<Long> mfIds = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("MF" + i);
            mathFunc.setType("tabulated");
            mathFunc.setOwner(savedOwner);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);
            mfIds.add(savedMathFunc.getId());

            TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
            tabulatedFunc.setMathFunction(savedMathFunc);
            tabulatedFunc.setXVals(new Double[]{1.0, 2.0, 3.0});
            tabulatedFunc.setYVals(new Double[]{1.0, 4.0, 9.0});
            TabulatedFunctionsEntity savedTf = tabulatedFunctionRepository.save(tabulatedFunc);
            tfIds.add(savedTf.getId());
        }
        tabulatedFunctionRepository.flush();


        Instant start = Instant.now();

        for (Long id : tfIds) {
            tabulatedFunctionRepository.deleteById(id);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("15. Delete TabulatedFunction", (double) ms / ITERATIONS, (double) ms);
        for (Long id : mfIds) {
            mathFunctionsRepository.deleteById(id);
        }
        userRepository.delete(savedOwner);
    }

    @Test
    @Order(16)
    @Transactional
    void CREATOR() {
        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            UserEntity user = new UserEntity();
            user.setName("Tanks" + i);
            user.setEmail("T" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity savedUser = userRepository.save(user);

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("TankiX" + i);
            mathFunc.setType(i % 2 == 0 ? "analytic" : "tabulated");
            mathFunc.setOwner(savedUser);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

            if ("analytic".equals(savedMathFunc.getType())) {
                AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
                analyticFunc.setMathFunction(savedMathFunc);
                analyticFunc.setFunctionExpression("x^13 + " + i);
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
        appendResult("16. Complex Create", (double) ms / ITERATIONS, (double) ms);
        userRepository.deleteAll();
    }

    @Test
    @Order(17)
    @Transactional
    void THANOS() {
        List<Long> userIds = new ArrayList<>();

        for (int i = 0; i < ITERATIONS; i++) {
            // Создаем пользователя с функциями
            UserEntity user = new UserEntity();
            user.setName("Thanos" + i);
            user.setEmail("Ironman" + i + "@example.com");
            user.setPassword(new byte[]{1, 2, 3, 4});
            user.setIsAdmin(false);
            UserEntity savedUser = userRepository.save(user);

            MathFunctionsEntity mathFunc = new MathFunctionsEntity();
            mathFunc.setName("Thor" + i);
            mathFunc.setType("analytic");
            mathFunc.setOwner(savedUser);
            MathFunctionsEntity savedMathFunc = mathFunctionsRepository.save(mathFunc);

            AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
            analyticFunc.setMathFunction(savedMathFunc);
            analyticFunc.setFunctionExpression("x^0 + " + i);
            analyticFunctionsRepository.save(analyticFunc);
            userIds.add(savedUser.getId());
        }
        Instant start = Instant.now();
        for (Long id : userIds) {
            userRepository.deleteById(id);
        }
        long ms = Duration.between(start, Instant.now()).toMillis();
        appendResult("17. Cascade Delete", (double) ms / ITERATIONS, (double) ms);
    }

    @Test
    @Order(18)
    void WHEREISMYRESULTS() {
        System.out.println("\n============================ CRUD PERFORMANCE RESULTS ==========");

        try {
            List<String> lines = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get(CSV_FILE)
            );

            System.out.println("№  Операция                          | Среднее время (мс) | Общее время (мс)");
            System.out.println("----------------------------------------------------------------------------");

            for (String line : lines) {
                if (!line.startsWith("Метод")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String method = parts[0];
                        String avgTime = String.format("%10s", parts[1]);
                        String totalTime = String.format("%10s", parts[2]);

                        System.out.printf("%-35s | %18s | %16s%n",
                                method, avgTime, totalTime);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
    }
}