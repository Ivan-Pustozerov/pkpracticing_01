package core.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
@Entity
@Table(name = "mathfunctions")
@Data
@NoArgsConstructor
public class MathFunctionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type", nullable = false, length = 10)
    private FunctionType type; // "analytic" или "tabulated"

    @Column(name = "name", nullable = false, unique = true, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;
}

