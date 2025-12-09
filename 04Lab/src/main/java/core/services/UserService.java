package core.services;

import core.DTO.request.UserRequest;
import core.DTO.response.UserResponse;
import core.entity.UserEntity;
import core.mapper.UserMapper;
import core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        return createUser(request, false); // По умолчанию не админ
    }

    @Transactional
    public UserResponse createUser(UserRequest request, Boolean isAdmin) {
        if (userRepository.existsByName(request.getName())) {throw new RuntimeException("User with this name already exists");}

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()).getBytes());

        user.setIsAdmin(isAdmin != null ? isAdmin : false);

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        return updateUser(id, request, null); // isAdmin не меняется
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request, Boolean isAdmin) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {

            user.setPassword(passwordEncoder.encode(request.getPassword()).getBytes());
        }
        if (isAdmin != null) {
            user.setIsAdmin(isAdmin);
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse registerUser(UserRequest request) {
        return createUser(request, false);
    }


    @Transactional
    public UserResponse changeUserRole(Long id, Boolean isAdmin) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsAdmin(isAdmin);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }
    public UserResponse findByUsername(String username) {
        UserEntity user = userRepository.findByName(username).orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }
}
