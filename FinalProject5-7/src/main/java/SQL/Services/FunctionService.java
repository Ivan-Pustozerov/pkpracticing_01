package SQL.Services;

import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.MathFunctionsRepository;
import SQL.repositories.SQLRepositoryException;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;

import java.util.ArrayList;


public class FunctionService {

    private final SmartConnection connection;
    private final MathFunctionsRepository Math = new MathFunctionsRepository();
    private final AnalyticFunctionsRepository Analytic = new AnalyticFunctionsRepository();
    private final TabulatedFunctionsRepository Tabulated = new TabulatedFunctionsRepository();


    public FunctionService(String url, String username, String password)
            throws SmartConnectionException {
        connection = new SmartConnection(url,username,password);
    }



}



