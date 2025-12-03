package core.repository;
import core.entity.AnalyticFunctionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticFunctionsRepository extends JpaRepository<AnalyticFunctionsEntity, Long> {
    // Найти аналитическую функцию по имени связанной математической функции
    AnalyticFunctionsEntity findByMathFunctionName(String name);
}
