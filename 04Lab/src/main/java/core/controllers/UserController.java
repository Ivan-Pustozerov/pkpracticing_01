package core.controllers;
import core.DTO.request.UserRequest;
import core.DTO.response.UserResponse;
import core.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import core.util.SecurityUtils;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == @securityUtils.getCurrentUserId()")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@RequestBody UserRequest request) {
        // Админ создает пользователя - по умолчанию не админ
        return userService.createUser(request, false);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createAdmin(@RequestBody UserRequest request) {
        // Только админ может создать другого админа
        return userService.createUser(request, true);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == @securityUtils.getCurrentUserId()")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest request) {

        if (securityUtils.isCurrentUserAdmin()) {
            return userService.updateUser(id, request, null);
        } else {
            return userService.updateUser(id, request);
        }
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeUserRole(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean isAdmin = request.get("isAdmin");
        if (isAdmin == null) {
            throw new RuntimeException("isAdmin field is required");
        }
        return userService.changeUserRole(id, isAdmin);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
