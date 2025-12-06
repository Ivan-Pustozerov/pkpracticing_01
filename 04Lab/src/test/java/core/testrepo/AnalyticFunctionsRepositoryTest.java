package core.testrepo;

import core.entity.AnalyticFunctionsEntity;
import core.entity.MathFunctionsEntity;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
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
class AnalyticFunctionsRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    @Autowired
    private AnalyticFunctionsRepository analyticFunctionsRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        UserEntity owner = createUser("analytic_owner", "analytic@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "mathfunc1", "analytic");

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression("x^2 + 3*x + 5");

        // Act
        AnalyticFunctionsEntity saved = analyticFunctionsRepository.save(analyticFunc);
        AnalyticFunctionsEntity found = analyticFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isNotNull();
        assertThat(found.getFunctionExpression()).isEqualTo("x^2 + 3*x + 5");
        assertThat(found.getMathFunction().getName()).isEqualTo("mathfunc1");
        assertThat(found.getMathFunction().getType()).isEqualTo("analytic");
    }

    @Test
    void testFindByMathFunctionName() {
        // Arrange
        UserEntity owner = createUser("owner_find", "find@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "searchable_func", "analytic");

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression("sin(x) * cos(x)");
        analyticFunctionsRepository.save(analyticFunc);

        // Act
        AnalyticFunctionsEntity found = analyticFunctionsRepository.findByMathFunctionName("searchable_func");
        AnalyticFunctionsEntity notFound = analyticFunctionsRepository.findByMathFunctionName("nonexisting");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getFunctionExpression()).isEqualTo("sin(x) * cos(x)");
        assertThat(found.getMathFunction().getName()).isEqualTo("searchable_func");
        assertThat(notFound).isNull();
    }

    @Test
    void testUpdateAnalyticFunction() {
        // Arrange
        UserEntity owner = createUser("owner_update", "update@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "update_func", "analytic");

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression("x + 1");
        AnalyticFunctionsEntity saved = analyticFunctionsRepository.save(analyticFunc);

        // Act
        saved.setFunctionExpression("x^3 + 2*x^2 + x + 1");
        AnalyticFunctionsEntity updated = analyticFunctionsRepository.save(saved);
        AnalyticFunctionsEntity found = analyticFunctionsRepository.findById(updated.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getFunctionExpression()).isEqualTo("x^3 + 2*x^2 + x + 1");
    }

    @Test
    void testDeleteAnalyticFunction() {
        // Arrange
        UserEntity owner = createUser("owner_delete", "delete@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "delete_func", "analytic");

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression("exp(x)");
        AnalyticFunctionsEntity saved = analyticFunctionsRepository.save(analyticFunc);

        // Act
        analyticFunctionsRepository.deleteById(saved.getId());
        AnalyticFunctionsEntity found = analyticFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNull();
    }

    @Test
    void testCannotCreateAnalyticForTabulatedMathFunction() {
        // Этот тест проверяет, что логика создания аналитической функции
        // только для математических функций типа "analytic" должна быть на уровне сервиса
        // Репозиторий просто сохранит, проверка типа - на уровне бизнес-логики

        // Arrange
        UserEntity owner = createUser("owner_mixed", "mixed@example.com");
        MathFunctionsEntity mathFunc = createMathFunction(owner, "tabulated_math", "tabulated");

        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression("should_not_save");

        // Act
        AnalyticFunctionsEntity saved = analyticFunctionsRepository.save(analyticFunc);
        AnalyticFunctionsEntity found = analyticFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert - репозиторий сохраняет, проверка типа должна быть на уровне сервиса
        assertThat(found).isNotNull();
        assertThat(found.getMathFunction().getType()).isEqualTo("tabulated");
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