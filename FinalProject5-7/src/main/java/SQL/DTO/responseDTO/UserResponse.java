package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

public record UserResponse
        (Long id, Boolean is_admin, String name, String email)
        implements DTO {}
