package SQL.DTO;

public record UserToServerDTO(long id, boolean is_admin, String name, String email, byte[] passwordHash)
        implements DTO{};

