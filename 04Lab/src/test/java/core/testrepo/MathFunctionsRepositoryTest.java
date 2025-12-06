package core.testrepo;

import core.entity.MathFunctionsEntity;
import core.entity.UserEntity;
import core.repository.MathFunctionsRepository;
import core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MathFunctionsRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        UserEntity owner = createUser("owner1", "owner1@example.com");
        MathFunctionsEntity function = new MathFunctionsEntity();
        function.setName("testfunction");
        function.setType("analytic");
        function.setOwner(owner);

        // Act
        MathFunctionsEntity saved = mathFunctionsRepository.save(function);
        MathFunctionsEntity found = mathFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("testfunction");
        assertThat(found.getType()).isEqualTo("analytic");
        assertThat(found.getOwner().getName()).isEqualTo("owner1");
    }

    @Test
    void testExistsByName() {
        // Arrange
        UserEntity owner = createUser("owner2", "owner2@example.com");
        MathFunctionsEntity function = new MathFunctionsEntity();
        function.setName("existingfunc");
        function.setType("tabulated");
        function.setOwner(owner);
        mathFunctionsRepository.save(function);

        // Act & Assert
        assertThat(mathFunctionsRepository.existsByName("existingfunc")).isTrue();
        assertThat(mathFunctionsRepository.existsByName("nonexisting")).isFalse();
    }

    @Test
    void testFindByName() {
        // Arrange
        UserEntity owner = createUser("owner3", "owner3@example.com");
        MathFunctionsEntity function = new MathFunctionsEntity();
        function.setName("findme");
        function.setType("analytic");
        function.setOwner(owner);
        mathFunctionsRepository.save(function);

        // Act
        MathFunctionsEntity found = mathFunctionsRepository.findByName("findme");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("findme");
        assertThat(found.getType()).isEqualTo("analytic");
        assertThat(found.getOwner().getName()).isEqualTo("owner3");
    }

    @Test
    void testFindByOwnerId() {
        // Arrange
        UserEntity owner1 = createUser("owner_a", "ownera@example.com");
        UserEntity owner2 = createUser("owner_b", "ownerb@example.com");

        // Create 3 functions for owner1
        for (int i = 1; i <= 3; i++) {
            MathFunctionsEntity func = new MathFunctionsEntity();
            func.setName("func_a_" + i);
            func.setType("analytic");
            func.setOwner(owner1);
            mathFunctionsRepository.save(func);
        }

        // Create 1 function for owner2
        MathFunctionsEntity func = new MathFunctionsEntity();
        func.setName("func_b_1");
        func.setType("tabulated");
        func.setOwner(owner2);
        mathFunctionsRepository.save(func);

        // Act
        List<MathFunctionsEntity> owner1Functions = mathFunctionsRepository.findByOwnerId(owner1.getId());
        List<MathFunctionsEntity> owner2Functions = mathFunctionsRepository.findByOwnerId(owner2.getId());

        // Assert
        assertThat(owner1Functions).hasSize(3);
        assertThat(owner2Functions).hasSize(1);

        assertThat(owner1Functions)
                .extracting(MathFunctionsEntity::getName)
                .containsExactlyInAnyOrder("func_a_1", "func_a_2", "func_a_3");

        assertThat(owner2Functions.get(0).getName()).isEqualTo("func_b_1");
        assertThat(owner2Functions.get(0).getType()).isEqualTo("tabulated");
    }

    @Test
    void testUpdateMathFunction() {
        // Arrange
        UserEntity owner = createUser("owner_update", "ownerupdate@example.com");
        MathFunctionsEntity function = new MathFunctionsEntity();
        function.setName("original");
        function.setType("analytic");
        function.setOwner(owner);
        MathFunctionsEntity saved = mathFunctionsRepository.save(function);

        // Act
        saved.setName("updated");
        saved.setType("tabulated");
        MathFunctionsEntity updated = mathFunctionsRepository.save(saved);
        MathFunctionsEntity found = mathFunctionsRepository.findById(updated.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("updated");
        assertThat(found.getType()).isEqualTo("tabulated");
    }

    @Test
    void testDeleteMathFunction() {
        // Arrange
        UserEntity owner = createUser("owner_delete", "ownerdelete@example.com");
        MathFunctionsEntity function = new MathFunctionsEntity();
        function.setName("todelete");
        function.setType("analytic");
        function.setOwner(owner);
        MathFunctionsEntity saved = mathFunctionsRepository.save(function);

        // Act
        mathFunctionsRepository.deleteById(saved.getId());
        MathFunctionsEntity found = mathFunctionsRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNull();
        assertThat(mathFunctionsRepository.existsByName("todelete")).isFalse();
    }

    private UserEntity createUser(String name, String email) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        return userRepository.save(user);
    }
}