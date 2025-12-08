package core.DTO.response;
import lombok.Data;
@Data
public class MathFunctionResponse {
    private Long id;
    private String type;
    private String name;
    private Long ownerId;
}
