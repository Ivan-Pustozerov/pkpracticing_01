package SQL.Mappers;

import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import SQL.DTO.FunctionData;
import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;
import functions.classes.AnalyticFunction;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;

import java.util.ArrayList;
import java.util.Objects;

public class MFunctionMapper {
    public static ArrayList<MathFunctionToClientAdminDTO> translateToClientDTO(ArrayList<MathFunctionFromBdDTO> BDdto,
                                                                               SmartConnection connection,
                                                                               AnalyticFunctionsRepository Analytic,
                                                                               TabulatedFunctionsRepository Tabulated,
                                                                               TabulatedFunctionFactory factory)
            throws SmartConnectionException, SQLRepositoryException {

        ArrayList<MathFunctionToClientAdminDTO> result = new ArrayList<>();

        for (var serverDTO : BDdto) {
            long id = serverDTO.id();
            long owner_id = serverDTO.owner_id();
            String type = serverDTO.type();
            String name = serverDTO.name();

            if (Objects.equals(type, "analytic")) {
                var analytic_functions = Analytic.readAnalyticFunctionInfo(connection.getConnection(), new long[]{id});

                String func_expression = analytic_functions.get(0).function_expression();
                AnalyticFunction func = new AnalyticFunction(func_expression);

                FunctionData data = new FunctionData(null, func);

                result.add(new MathFunctionToClientAdminDTO(id, type, name, data, owner_id));
            }
            else if (Objects.equals(type, "tabulated")) {
                var tabulated_functions = Tabulated.readTabulatedFunctionInfo(connection.getConnection(), new long[]{id});

                double[] xVals = tabulated_functions.get(0).xVals();
                double[] yVals = tabulated_functions.get(0).yVals();
                TabulatedFunction func = factory.create(xVals, yVals);

                FunctionData data = new FunctionData(func, null);

                result.add(new MathFunctionToClientAdminDTO(id, type, name, data, owner_id));
            }
            else {
                throw new RuntimeException("Convert_ERROR");
            }
        }
        return result;
    }

}
