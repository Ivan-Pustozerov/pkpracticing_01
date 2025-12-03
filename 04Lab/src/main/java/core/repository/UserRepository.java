package core.repository;
import core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    // Кастомный запрос с JPQL
    @Query("SELECT u FROM UserEntity u WHERE u.isAdmin = true")
    List<UserEntity> findAllAdmins();
}
