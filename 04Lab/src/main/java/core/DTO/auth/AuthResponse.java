package core.DTO.auth;

import core.DTO.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Ответ после успешной аутентификации.
 * Содержит JWT токен и информацию о пользователе.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private UserResponse user;  // Информация о пользователе

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
}