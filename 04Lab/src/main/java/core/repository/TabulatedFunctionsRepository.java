package core.repository;

import core.entity.TabulatedFunctionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TabulatedFunctionsRepository extends JpaRepository<TabulatedFunctionsEntity, Long> {
    TabulatedFunctionsEntity findByMathFunctionName(String name);
}