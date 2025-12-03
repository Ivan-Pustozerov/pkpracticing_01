package core.repository;

import core.entity.MathFunctionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MathFunctionsRepository extends JpaRepository<MathFunctionsEntity, Long> {
    boolean existsByName(String name);
    MathFunctionsEntity findByName(String name);
    List<MathFunctionsEntity> findByOwnerId(Long ownerId);
}