package SQL.DTO.responseDTO;

public record AuthResponse
        (String token, UserResponse user)
        {
            public String type(){return "Bearer";}
        }
