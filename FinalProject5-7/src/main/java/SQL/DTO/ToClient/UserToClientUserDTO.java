package SQL.DTO.ToClient;

import SQL.DTO.DTO;

public record UserToClientUserDTO
        (boolean is_admin, String name, String email)
        implements DTO {}
