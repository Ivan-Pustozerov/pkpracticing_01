package SQL.DTO.ToClient;

import SQL.DTO.DTO;

public record UserToClientAdminDTO
        (long id, boolean is_admin, String name, String email)
        implements DTO {}
