package SQL.DTO;

public record UserToClientDTO(long id, String name, String email)
        implements DTO{}
