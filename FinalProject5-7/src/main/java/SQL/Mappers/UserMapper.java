package SQL.Mappers;

import SQL.DTO.FromBD.UserFromBdDTO;
import SQL.DTO.ToClient.UserToClientAdminDTO;

import java.util.ArrayList;

public class UserMapper {
    public static ArrayList<UserToClientAdminDTO> translateToClientDTO(ArrayList<UserFromBdDTO> BDdto){
        ArrayList<UserToClientAdminDTO> result = new ArrayList<>();
        for (UserFromBdDTO userDTO : BDdto) {
            long id = userDTO.id();
            boolean is_admin = userDTO.is_admin();
            String name = userDTO.name();
            String email = userDTO.email();
            result.add(new UserToClientAdminDTO(id, is_admin, name, email));
        }
        return result;
    }
}
