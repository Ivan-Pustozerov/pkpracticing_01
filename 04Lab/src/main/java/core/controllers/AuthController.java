package core.controllers;

import java.util.HashMap;
import java.util.Map;

import core.DTO.auth.AuthResponse;
import core.DTO.auth.LoginRequest;
import core.DTO.request.UserRequest;
import core.DTO.response.UserResponse;
import core.security.jwt.*;
import core.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>  login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("===  LOGIN DEBUG ===");
        System.out.println("Request username: '" + loginRequest.getUsername() + "'");

        // Аутентифицируем
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ВАЖНО: Используйте имя из Authentication, а не из запроса!
        String authenticatedUsername = authentication.getName();
        System.out.println("Authenticated username: '" + authenticatedUsername + "'");

        // Проверяем совпадение
        if (!authenticatedUsername.equals(loginRequest.getUsername())) {
            System.out.println("WARNING: Username mismatch!");
            System.out.println("  Request: " + loginRequest.getUsername());
            System.out.println("  Auth: " + authenticatedUsername);
        }

        // Создаем токен с правильным именем
        String jwt = jwtTokenProvider.createToken(authenticatedUsername);

        System.out.println("Token created for: '" + authenticatedUsername + "'");

        // Получаем информацию о пользователе по АУТЕНТИФИЦИРОВАННОМУ имени
        UserResponse userResponse = userService.findByUsername(authenticatedUsername);

        return ResponseEntity.ok(new AuthResponse(jwt, userResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest request) {

        UserResponse userResponse = userService.registerUser(request);


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getName(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(request.getName());

        return ResponseEntity.ok(new AuthResponse(jwt, userResponse));
    }
}