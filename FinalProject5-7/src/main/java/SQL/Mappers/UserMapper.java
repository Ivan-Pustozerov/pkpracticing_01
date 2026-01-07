package SQL.Mappers;

import SQL.DTO.FromBD.UserFromBdDTO;
import SQL.DTO.responseDTO.UserInfoResponseDTO;

import java.util.ArrayList;

public class UserMapper {
    public static ArrayList<UserInfoResponseDTO> translateToClientDTO(ArrayList<UserFromBdDTO> BDdto){
        ArrayList<UserInfoResponseDTO> result = new ArrayList<>();
        for (UserFromBdDTO userDTO : BDdto) {
            long id = userDTO.id();
            boolean is_admin = userDTO.is_admin();
            String name = userDTO.name();
            String email = userDTO.email();
            result.add(new UserInfoResponseDTO(id, is_admin, name, email));
        }
        return result;
    }
}
