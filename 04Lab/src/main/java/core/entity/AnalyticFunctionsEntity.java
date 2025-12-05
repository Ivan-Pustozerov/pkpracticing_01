package core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "analyticfunctions")
@Data
@NoArgsConstructor
public class AnalyticFunctionsEntity {

    @Id
    @Column(name = "func_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "func_id")
    private MathFunctionsEntity mathFunction;

    @Column(name = "function_expression", nullable = false, columnDefinition = "TEXT")
    private String functionExpression;
}