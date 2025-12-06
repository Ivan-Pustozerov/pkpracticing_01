package core.testrepo;

import core.entity.UserEntity;
import core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("testuser");
        user.setEmail("test@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);

        // Act
        UserEntity saved = userRepository.save(user);
        Optional<UserEntity> found = userRepository.findById(saved.getId());

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testFindByName() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("john");
        user.setEmail("john@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        userRepository.save(user);

        // Act
        Optional<UserEntity> found = userRepository.findByName("john");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("john");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByEmail() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("alice");
        user.setEmail("alice@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(true);
        userRepository.save(user);

        // Act
        Optional<UserEntity> found = userRepository.findByEmail("alice@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("alice");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(found.get().getIsAdmin()).isTrue();
    }

    @Test
    void testExistsByName() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("existing");
        user.setEmail("existing@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByName("existing")).isTrue();
        assertThat(userRepository.existsByName("nonexisting")).isFalse();
    }

    @Test
    void testExistsByEmail() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("user");
        user.setEmail("unique@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByEmail("unique@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notfound@example.com")).isFalse();
    }

    @Test
    void testFindAllAdmins() {
        // Arrange
        UserEntity admin1 = new UserEntity();
        admin1.setName("admin1");
        admin1.setEmail("admin1@example.com");
        admin1.setPassword(new byte[]{1, 2, 3, 4});
        admin1.setIsAdmin(true);
        userRepository.save(admin1);

        UserEntity admin2 = new UserEntity();
        admin2.setName("admin2");
        admin2.setEmail("admin2@example.com");
        admin2.setPassword(new byte[]{5, 6, 7, 8});
        admin2.setIsAdmin(true);
        userRepository.save(admin2);

        UserEntity regular = new UserEntity();
        regular.setName("regular");
        regular.setEmail("regular@example.com");
        regular.setPassword(new byte[]{9, 10, 11, 12});
        regular.setIsAdmin(false);
        userRepository.save(regular);

        // Act
        List<UserEntity> admins = userRepository.findAllAdmins();

        // Assert
        assertThat(admins).hasSize(2);
        assertThat(admins).extracting(UserEntity::getName)
                .containsExactlyInAnyOrder("admin1", "admin2");
        assertThat(admins).extracting(UserEntity::getIsAdmin)
                .containsOnly(true);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("original");
        user.setEmail("original@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        UserEntity saved = userRepository.save(user);

        // Act
        saved.setName("updated");
        saved.setEmail("updated@example.com");
        saved.setIsAdmin(true);
        UserEntity updated = userRepository.save(saved);
        Optional<UserEntity> found = userRepository.findById(updated.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("updated");
        assertThat(found.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(found.get().getIsAdmin()).isTrue();
    }

    @Test
    void testDeleteUser() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("todelete");
        user.setEmail("delete@example.com");
        user.setPassword(new byte[]{1, 2, 3, 4});
        user.setIsAdmin(false);
        UserEntity saved = userRepository.save(user);

        // Act
        userRepository.deleteById(saved.getId());
        Optional<UserEntity> found = userRepository.findById(saved.getId());

        // Assert
        assertThat(found).isEmpty();
        assertThat(userRepository.existsByName("todelete")).isFalse();
        assertThat(userRepository.existsByEmail("delete@example.com")).isFalse();
    }
}