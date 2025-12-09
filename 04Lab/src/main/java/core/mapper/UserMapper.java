package core.mapper;

import core.DTO.request.UserRequest;
import core.DTO.response.UserResponse;
import core.entity.UserEntity;
import org.springframework.stereotype.Component;
@Component
public class UserMapper {
    public UserResponse toResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setIsAdmin(entity.getIsAdmin());
        return response;
    }
    /**
     * Есть вещи ,которые не понимает вселенная
     * Это одна из них
     * Никто не знает зачем, а главное для какой цели
     */
    public void updateEntity(UserEntity entity, UserRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            entity.setPassword(request.getPassword().getBytes());
        }
    }
}
