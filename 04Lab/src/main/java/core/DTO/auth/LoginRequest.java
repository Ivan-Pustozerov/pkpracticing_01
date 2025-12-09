package core.DTO.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Запрос на аутентификацию (логин).
 * Используется в AuthController для получения JWT токена.
 */
@Data
public class LoginRequest {
    @JsonProperty("name")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}