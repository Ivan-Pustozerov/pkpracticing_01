package SQL.Mappers;

import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import SQL.DTO.FunctionData;
import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.SQLRepositoryException;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MathFunctionMapperDTO {
    public static ArrayList<MathFunctionToClientAdminDTO> translateToClientDTO(ArrayList<MathFunctionFromBdDTO> BDdto,
                                                                               SmartConnection connection,
                                                                               AnalyticFunctionsRepository Analytic,
                                                                               TabulatedFunctionsRepository Tabulated)
            throws SmartConnectionException, SQLRepositoryException {

        ArrayList<MathFunctionToClientAdminDTO> result = new ArrayList<>();

        for (var serverDTO : BDdto) {
            long id = serverDTO.id();
            long owner_id = serverDTO.owner_id();
            String type = serverDTO.type();
            String name = serverDTO.name();

            if (type == "analytic") {
                var analytic_functions = Analytic.readAnalyticFunctionInfo(connection.getConnection(), new long[]{id});
                String func_expression = analytic_functions.get(0).function_expression();
                FunctionData data = new FunctionData(func_expression, null, null);

                result.add(new MathFunctionToClientAdminDTO(id, type, name, data, owner_id));
            }
            else if (type == "tabulated") {
                var tabulated_functions = Tabulated.readTabulatedFunctionInfo(connection.getConnection(), new long[]{id});
                double[] xVals = tabulated_functions.get(0).xVals();
                double[] yVals = tabulated_functions.get(0).yVals();
                FunctionData data = new FunctionData(null, xVals, yVals);

                result.add(new MathFunctionToClientAdminDTO(id, type, name, data, owner_id));
            }
            else {
                throw new RuntimeException("Convert_ERROR");
            }
        }
        return result;
    }

    public static ArrayList<MathFunctionToClientAdminDTO> whoIsMissing(long[] idRequest,
                                                                       ArrayList<MathFunctionToClientAdminDTO> serverResponse){

        int missing_size = idRequest.length - serverResponse.size();
        if(missing_size == 0) return serverResponse;

        Long[] found = new Long[serverResponse.size()];

        int i=0;
        for(var elem : serverResponse){
            found[i++] = elem.owner_id();
        }
        Set<Long> foundId = new HashSet<>(Arrays.asList(found));


        for(Long id : idRequest){
            if(!foundId.contains(id)){
                serverResponse.add(new MathFunctionToClientAdminDTO(null,null,null,null,id));
            }
        }
        return serverResponse;
    }

}
