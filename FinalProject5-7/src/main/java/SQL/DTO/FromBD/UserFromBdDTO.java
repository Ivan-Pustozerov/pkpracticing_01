package SQL.DTO.FromBD;

public record UserFromBdDTO(long id, boolean is_admin, String name, String email, byte[] passwordHash)
        implements DTO {};

