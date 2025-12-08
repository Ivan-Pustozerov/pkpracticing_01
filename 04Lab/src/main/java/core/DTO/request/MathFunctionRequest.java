package core.DTO.request;
import lombok.Data;
@Data
 class MathFunctionRequest {
    private Long userId;
    private String type;
    private String name;
}
