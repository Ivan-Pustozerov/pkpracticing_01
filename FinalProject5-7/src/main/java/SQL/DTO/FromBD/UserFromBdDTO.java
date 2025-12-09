package SQL.DTO.FromBD;

import SQL.DTO.DTO;

public record UserFromBdDTO(long id, boolean is_admin, String name, String email, byte[] passwordHash)
        implements DTO {};

