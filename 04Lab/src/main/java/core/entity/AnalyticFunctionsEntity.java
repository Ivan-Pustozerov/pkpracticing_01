package core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analytic_functions")
@Data
@NoArgsConstructor
public class AnalyticFunctionsEntity {

    @Id
    @Column(name = "func_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "func_id")
    private MathFunctionsEntity mathFunction;

    @Column(name = "function_expression", nullable = false, columnDefinition = "TEXT")
    private String functionExpression;
}