package core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tabulatedfunctions")
@Data
@NoArgsConstructor
public class TabulatedFunctionsEntity {

    @Id
    @Column(name = "func_id")
    private Long id;
    //
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "func_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MathFunctionsEntity mathFunction;
    //
    @Column(name = "xvals", nullable = false, columnDefinition = "DOUBLE PRECISION[]")
    private Double[] xVals;
    //
    @Column(name = "yvals", nullable = false, columnDefinition = "DOUBLE PRECISION[]")
    private Double[] yVals;
}