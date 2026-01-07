package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

public record UserInfoResponseDTO
        (long id, boolean is_admin, String name, String email)
        implements DTO {}
