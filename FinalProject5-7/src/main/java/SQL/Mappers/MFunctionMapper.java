package SQL.Mappers;

import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;
import SQL.DTO.responseDTO.AnalyticFunctionResponse;
import SQL.DTO.responseDTO.MathFunctionDetailsResponse;
import SQL.DTO.responseDTO.MathFunctionInfoResponse;
import SQL.DTO.responseDTO.TabulatedFunctionResponse;

import java.util.ArrayList;
import java.util.Objects;

public class MFunctionMapper {
    public static ArrayList<MathFunctionDetailsResponse> translateDetailsResponseDTO(ArrayList<MathFunctionFromBdDTO> BDdto,
                                                                                     SmartConnection connection,
                                                                                     AnalyticFunctionsRepository Analytic,
                                                                                     TabulatedFunctionsRepository Tabulated)
            throws SmartConnectionException, SQLRepositoryException {

        ArrayList<MathFunctionDetailsResponse> result = new ArrayList<>();

        for (var serverDTO : BDdto) {
            long id = serverDTO.id();
            long owner_id = serverDTO.owner_id();
            String type = serverDTO.type();
            String name = serverDTO.name();


            if (Objects.equals(type, "analytic")) {
                var analytic_functions = Analytic.readAnalyticFunctionInfo(connection.getConnection(), new long[]{id});
                String func_expression = analytic_functions.get(0).function_expression();

                result.add(new AnalyticFunctionResponse(id, type, name, owner_id, func_expression));
            }
            else if (Objects.equals(type, "tabulated")) {
                var tabulated_functions = Tabulated.readTabulatedFunctionInfo(connection.getConnection(), new long[]{id});

                double[] xVals = tabulated_functions.get(0).xVals();
                double[] yVals = tabulated_functions.get(0).yVals();

                result.add(new TabulatedFunctionResponse(id, type, name, owner_id, xVals, yVals));
            }
            else {
                throw new RuntimeException("Convert_ERROR");
            }
        }
        return result;
    }

    public static ArrayList<MathFunctionInfoResponse> translateInfoResponseDTO(ArrayList<MathFunctionFromBdDTO> BDdto){
        ArrayList<MathFunctionInfoResponse> result = new ArrayList<>();
        for(var serverDTO : BDdto){
            long id = serverDTO.id();
            long owner_id = serverDTO.owner_id();
            String type = serverDTO.type();
            String name = serverDTO.name();
            result.add(new MathFunctionInfoResponse(id,type,name,owner_id));
        }
        return result;

    }
}
