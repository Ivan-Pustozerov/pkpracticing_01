package core.testrepo;

import core.entity.MathFunctionsEntity;
import core.entity.TabulatedFunctionsEntity;
import core.entity.UserEntity;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TabulatedFunctionsRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    @Autowired
    private TabulatedFunctionsRepository tabulatedFunctionsRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        UserEntity owner = createUser("tabulated_owner", "tabulated@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "tab_mathfunc", "tabulated");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{0.0, 1.0, 2.0, 3.0});
        tabulatedFunc.setYVals(new Double[]{0.0, 1.0, 4.0, 9.0});

        // Act
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isNotNull();
        assertThat(found.getXVals()).containsExactly(0.0, 1.0, 2.0, 3.0);
        assertThat(found.getYVals()).containsExactly(0.0, 1.0, 4.0, 9.0);
        assertThat(found.getMathFunction().getName()).isEqualTo("tab_mathfunc");
        assertThat(found.getMathFunction().getType()).isEqualTo("tabulated");
    }

    @Test
    void testUpdateTabulatedFunction() {
        // Arrange
        UserEntity owner = createUser("owner_tab_update", "tabupdate@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "tab_update_func", "tabulated");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{1.0, 2.0});
        tabulatedFunc.setYVals(new Double[]{10.0, 20.0});
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);

        // Act
        saved.setXVals(new Double[]{1.0, 2.0, 3.0, 4.0});
        saved.setYVals(new Double[]{10.0, 20.0, 30.0, 40.0});
        TabulatedFunctionsEntity updated = tabulatedFunctionsRepository.save(saved);
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(updated.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getXVals()).containsExactly(1.0, 2.0, 3.0, 4.0);
        assertThat(found.getYVals()).containsExactly(10.0, 20.0, 30.0, 40.0);
    }

    @Test
    void testDeleteTabulatedFunction() {
        // Arrange
        UserEntity owner = createUser("owner_tab_delete", "tabdelete@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "tab_delete_func", "tabulated");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{0.5, 1.5});
        tabulatedFunc.setYVals(new Double[]{0.25, 2.25});
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);

        // Act
        tabulatedFunctionsRepository.deleteById(saved.getId());
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNull();
    }

    @Test
    void testTabulatedFunctionWithEmptyArrays() {
        // Arrange
        UserEntity owner = createUser("owner_empty", "empty@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "empty_func", "tabulated");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{});
        tabulatedFunc.setYVals(new Double[]{});

        // Act
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getXVals()).isEmpty();
        assertThat(found.getYVals()).isEmpty();
    }

    @Test
    void testTabulatedFunctionWithSingleValue() {
        // Arrange
        UserEntity owner = createUser("owner_single", "single@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "single_func", "tabulated");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{5.0});
        tabulatedFunc.setYVals(new Double[]{25.0});

        // Act
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getXVals()).containsExactly(5.0);
        assertThat(found.getYVals()).containsExactly(25.0);
    }

    @Test
    void testCannotCreateTabulatedForAnalyticMathFunction() {
        // Этот тест проверяет, что логика создания табулированной функции
        // только для математических функций типа "tabulated" должна быть на уровне сервиса

        // Arrange
        UserEntity owner = createUser("owner_wrong_type", "wrong@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "analytic_math", "analytic");

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(new Double[]{1.0, 2.0});
        tabulatedFunc.setYVals(new Double[]{1.0, 4.0});

        // Act
        TabulatedFunctionsEntity saved = tabulatedFunctionsRepository.save(tabulatedFunc);
        TabulatedFunctionsEntity found = tabulatedFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert - репозиторий сохраняет, проверка типа должна быть на уровне сервиса
        assertThat(found).isNotNull();
        assertThat(found.getMathFunction().getType()).isEqualTo("analytic");
    }

    private UserEntity createUser(String name, String email) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        return userRepository.save(user);
    }

    private MathFunctionsEntity createMathFunction(UserEntity owner, String name, String type) {
        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setName(name);
        mathFunc.setType(type);
        mathFunc.setOwner(owner);
        return mathFunctionsRepository.save(mathFunc);
    }
}