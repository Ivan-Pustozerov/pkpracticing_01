package SQL.repositories.tools;

import SQL.DTO.FromBD.DTO;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLResultSetFunction<T extends DTO> {
    T apply(ResultSet set) throws SQLException;
}
