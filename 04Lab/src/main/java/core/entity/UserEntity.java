package core.entity;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import lombok.*;
import operations.Integral.ThreadWorker;
import operations.Integral.TrapezoidDefineIntegralTaskXY;
import operations.Integral.TrapezoidDefineIntegralXY;
import org.hibernate.annotations.BatchSize;

import java.util.Objects;
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private byte[] password;
    /** Про @BatchSize
     * Кратко: эта аннотация позволяет как бы дробить запросы и немного спасать от n+1 moment:
     */
    @OneToMany(mappedBy = "owner")
    @BatchSize(size = 20)
    @ToString.Exclude
    private List<MathFunctionsEntity> functions = new ArrayList<>();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(getId(), that.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
