package core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tabulated_functions")
@Data
@NoArgsConstructor
public class TabulatedFunctionsEntity {

    @Id
    @Column(name = "func_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "func_id")
    private MathFunctionsEntity mathFunction;

    @Column(name = "xvals", nullable = false, columnDefinition = "DOUBLE PRECISION[]")
    private Double[] xVals;

    @Column(name = "yvals", nullable = false, columnDefinition = "DOUBLE PRECISION[]")
    private Double[] yVals;
}