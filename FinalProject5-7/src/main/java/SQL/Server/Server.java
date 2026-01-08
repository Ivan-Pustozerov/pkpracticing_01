package SQL.Server;

import SQL.DTO.PointDTO;
import SQL.DTO.requestDTO.*;
import SQL.DTO.responseDTO.*;
import SQL.Server.exception.*;
import SQL.Services.ErrorService;
import SQL.Services.FunctionService;
import SQL.Services.ServiceArgumentsException;
import SQL.Services.UserService;
import SQL.repositories.UserStatisticRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnectionException;

import java.util.ArrayList;

import static SQL.Mappers.Converter.todoubleArray;

public class Server {
    private final String bd_url="jdbc:postgresql://localhost:5432/Final";
    private final String bd_username="postgres";
    private final String bd_pswrd="lkroot";

    private final UserService userService;
    private final FunctionService functionService;
    private final ErrorService errorService;

    public Server() {
        try {
            userService = new UserService(bd_url, bd_username, bd_pswrd);
            functionService = new FunctionService(bd_url, bd_username, bd_pswrd);
            errorService = new ErrorService(bd_url, bd_username, bd_pswrd);
        } catch (SmartConnectionException | SQLRepositoryException e) {
            throw new RuntimeException("INIT_ERROR!");
        }
    }

    private void checkAuth(String token) throws ServerError {
        if(!userService.validateToken(token)){
            throw errorHandler(new Unauthorized401());
        }
    }
    private void checkAdmin(long id) throws ServerError {
        try {
            boolean is_admin = userService.checkIsAdmin(id);
            if (!is_admin) {
                throw errorHandler(new Forbidden403());
            }
        }catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }
    private void checkFuncBelongs(String token,long id) throws ServerError {
        checkFuncExist(id);
        try{
            long owner_id = userService.getUserIdFromToken(token);
            if(!functionService.checkMFunctionBelongs(owner_id,id)){
                throw errorHandler(new Forbidden403());
            }
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }
    private void checkFuncExist(long id) throws ServerError {
        try {
            if(!functionService.checkMFunctionsExist(new long[]{id})){
                throw errorHandler(new NotFound404());
            }
        } catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }

    private ServerError errorHandler(ServerError Throw){
        errorService.logError(Throw.code(),Throw.getMessage());
        return Throw;
    }


    /// =====================================AUTH========================================
    public AuthResponse registerUserAdmin(RegisterRequest registerRequest)
            throws ServerError {
        String name = registerRequest.name();
        String email = registerRequest.email();
        String password = registerRequest.password();

        AuthResponse response;

        try {
            userService.addUser(true,name,email,password);

            String token = userService.authenticateUser(name,password);
            UserResponse user = userService.readUserInfo(new String[]{name},"-","-").get(0);

            response = new AuthResponse(token,user);
            return response;

        } catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new Conflict409());
        }
    }
    public AuthResponse registerUser(RegisterRequest registerRequest)
            throws ServerError {
        String name = registerRequest.name();
        String email = registerRequest.email();
        String password = registerRequest.password();

        AuthResponse response;

        try {
            userService.addUser(false,name,email,password);

            String token = userService.authenticateUser(name,password);
            UserResponse user = userService.readUserInfo(new String[]{name},"-","-").get(0);

            response = new AuthResponse(token,user);
            return response;

        } catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new Conflict409());
        }
    }
    public AuthResponse loginUser(LoginRequest loginRequest)
            throws ServerError {
        String name = loginRequest.name();
        String password = loginRequest.password();

        if(name == null || password == null){
            throw errorHandler(new BadRequest400());
        }

        AuthResponse response;

        try {
            String token = userService.authenticateUser(name,password);
            UserResponse user = userService.readUserInfo(new String[]{name},"-","-").get(0);

            response = new AuthResponse(token,user);
            return response;

        } catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new Unauthorized401());
        }
    }


    /// ==================================USER============================================
    public ArrayList<UserResponse> getAllUsers(String token)
            throws ServerError {
        checkAuth(token);
        long id = userService.getUserIdFromToken(token);
        try {
            checkAdmin(id);
            return userService.getAllUsers("-", "-");
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }
    public UserResponse getSpecificUser(String token, long id)
            throws ServerError {
        checkAuth(token);
        long user_id = userService.getUserIdFromToken(token);
        try {
            boolean is_admin = userService.checkIsAdmin(user_id);
            if (is_admin || user_id == id) {
                return userService.readUserInfo(new long[]{id},"-", "-").get(0);
            }
            else{
                throw errorHandler(new Forbidden403());
            }
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new NotFound404());
        }

    }
    public UserResponse postUser(String token, RegisterRequest registerRequest)
            throws ServerError {

        checkAuth(token);
        long id = userService.getUserIdFromToken(token);
        checkAdmin(id);

        return registerUser(registerRequest).user();
    }
    public UserResponse postUserAdmin(String token, RegisterRequest registerRequest)
            throws ServerError {
        checkAuth(token);
        long id = userService.getUserIdFromToken(token);
        checkAdmin(id);

        return registerUserAdmin(registerRequest).user();
    }
    public UserResponse putUser(String token, long id, RegisterRequest registerRequest)
            throws ServerError {
        checkAuth(token);
        long user_id = userService.getUserIdFromToken(token);
        try {
            boolean is_admin = userService.checkIsAdmin(user_id);
            if (is_admin || user_id == id) {
                String name = registerRequest.name();
                String email = registerRequest.email();
                String password = registerRequest.password();

                userService.updateUserInfo(id,name,email,password);
                return userService.readUserInfo(new long[]{id},"-","-").get(0);
            }
            else{
                throw errorHandler(new Forbidden403());
            }
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new NotFound404());
        }
    }
    public UserResponse putUserRole(String token, long id, Boolean isAdmin)
            throws ServerError {
        if(isAdmin == null){
            throw errorHandler(new BadRequest400());
        }
        checkAuth(token);
        long user_id = userService.getUserIdFromToken(token);
        try {
            checkAdmin(user_id);
            userService.updateUserRole(id,isAdmin);
            return userService.readUserInfo(new long[]{id},"-","-").get(0);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch (ServiceArgumentsException e){
            throw errorHandler(new NotFound404());
        }
    }
    public void deleteUser(String token, long id)
            throws ServerError {
        checkAuth(token);
        long user_id = userService.getUserIdFromToken(token);
        try {
            checkAdmin(user_id);
            userService.removeUser(id);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            errorService.logException(e);
            throw new InternalServerError500();
        }
        catch (ServiceArgumentsException e){
            throw errorHandler(new NotFound404());
        }

    }

    /// ======================================FUNCTIONS==================================
    public ArrayList<MathFunctionInfoResponse> getAllFunctions(String token, String type)
            throws ServerError {
        checkAuth(token);
        long id = userService.getUserIdFromToken(token);
        try {
            var functions = functionService.readMFunctionInfoByOwnerId(id,"-","-");
            ArrayList<MathFunctionInfoResponse> response;
            if(type != null){
                response = new ArrayList<>();
                for(var func : functions){
                    if(func.type().equals(type)){
                        response.add(func);
                    }
                }
            }
            else{
                response = functions;
            }
            return response;
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }

    }
    public MathFunctionDetailsResponse getSpecificFunction(String token, long id)
            throws ServerError {
        checkAuth(token);
        try {
            checkFuncBelongs(token,id);
            return functionService.readMFunctionDetails(new long[]{id},"-","-").get(0);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
        catch(ServiceArgumentsException e){
            throw errorHandler(new NotFound404());
        }
    }
    public AnalyticFunctionResponse postAnalyticFunction
            (String token, AnalyticFunctionRequest analyticFunctionRequest)
            throws ServerError {
        checkAuth(token);
        try {
            long userId;
            String func_expression = analyticFunctionRequest.functionExpression();
            String name = analyticFunctionRequest.name();

            if(analyticFunctionRequest.userId() == null){
                userId = userService.getUserIdFromToken(token);
            }
            else{userId = analyticFunctionRequest.userId();}

            long func_id = functionService.addAnalyticMFunction(func_expression,name,userId);
            return (AnalyticFunctionResponse) getSpecificFunction(token,func_id);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }
    public TabulatedFunctionResponse postTabulatedFunction
            (String token, TabulatedFunctionRequest tabulatedFunctionRequest)
            throws ServerError {
        checkAuth(token);
        try {
            long userId;
            double[] xVals = todoubleArray(tabulatedFunctionRequest.xvals());
            double[] yVals = todoubleArray(tabulatedFunctionRequest.yvals());
            String name = tabulatedFunctionRequest.name();

            if(tabulatedFunctionRequest.userId() == null){
                userId = userService.getUserIdFromToken(token);
            }
            else{userId = tabulatedFunctionRequest.userId();}

            long func_id = functionService.addTabulatedMFunction(xVals,yVals,name,userId);
            return (TabulatedFunctionResponse) getSpecificFunction(token,func_id);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler(new InternalServerError500());
        }
    }
    public void deleteFunction(String token, long id)
            throws ServerError {
        checkAuth(token);
        checkFuncBelongs(token,id);
        long user_id = userService.getUserIdFromToken(token);
        try {
            functionService.removeMFunction(user_id,id);
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw errorHandler (new InternalServerError500());
        }

    }
    public ArrayList<PointDTO> calculateFunction(String token, long id, FunctionRangeRequest request)
            throws ServerError {
        checkAuth(token);
        checkFuncBelongs(token,id);
        if(!request.validate()){
            throw errorHandler(new BadRequest400());
        }

        long user_id = userService.getUserIdFromToken(token);
        try {
            double from = request.from();
            double to = request.to();
            double step = request.step();

            return functionService.calcPoints(id,from,to,step);
        }
        catch (SmartConnectionException | SQLRepositoryException | IllegalArgumentException e ) {
            throw errorHandler(new InternalServerError500());
        }

    }
}
