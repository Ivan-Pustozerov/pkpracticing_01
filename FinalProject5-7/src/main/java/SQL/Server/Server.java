package SQL.Server;

import SQL.DTO.requestDTO.LoginRequest;
import SQL.DTO.requestDTO.RegisterRequest;
import SQL.DTO.responseDTO.AuthResponse;
import SQL.DTO.responseDTO.UserResponse;
import SQL.Server.exception.Forbidden403;
import SQL.Server.exception.InternalServerError500;
import SQL.Server.exception.Unauthorized401;
import SQL.Services.ErrorService;
import SQL.Services.FunctionService;
import SQL.Services.UserService;
import SQL.repositories.UserStatisticRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnectionException;

import java.util.ArrayList;

public class Server {
    private String bd_url="jdbc:postgresql://localhost:5432/Final";
    private String bd_username="postgres";
    private String bd_pswrd="lkroot";
    private boolean isActivated = false;

    private UserService userService;
    private FunctionService functionService;
    private ErrorService errorService;
    private UserStatisticRepository statisticService;

    private Server(){};
    private void init(){
        try {
            userService = new UserService(bd_url, bd_username, bd_pswrd);
            functionService = new FunctionService(bd_url, bd_username, bd_pswrd);
            errorService = new ErrorService(bd_url, bd_username, bd_pswrd);
            statisticService = new UserStatisticRepository();///ПОДПРАВИТЬ!
        }
        catch (SmartConnectionException | SQLRepositoryException e) {
            throw new RuntimeException("INIT_ERROR!");
        }
    }

    public Server getInstance(){
        if(!isActivated){
            init();
        }
        return this;
    }


    public AuthResponse registerUser(RegisterRequest registerRequest)
            throws InternalServerError500 {
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
            errorService.logException(e);
            throw new InternalServerError500();
        }
    }
    public AuthResponse loginUser(LoginRequest loginRequest)
            throws InternalServerError500 {
        String name = loginRequest.name();
        String password = loginRequest.password();

        AuthResponse response;

        try {
            String token = userService.authenticateUser(name,password);
            UserResponse user = userService.readUserInfo(new String[]{name},"-","-").get(0);

            response = new AuthResponse(token,user);
            return response;

        } catch (SmartConnectionException | SQLRepositoryException e) {
            errorService.logException(e);
            throw new InternalServerError500();
        }
    }

    public ArrayList<UserResponse> getAllUsers(String token)
            throws Forbidden403, Unauthorized401, InternalServerError500 {
        if(userService.validateToken(token)){
            long id = userService.getUserIdFromToken(token);
            try {
                boolean is_admin = userService.checkIsAdmin(id);
                if (is_admin) {
                    return userService.getAllUsers("-", "-");
                }
                else{
                    throw new Forbidden403();
                }
            }
            catch (SmartConnectionException | SQLRepositoryException e) {
                errorService.logException(e);
                throw new InternalServerError500();
            }

        }
        else{
            throw new Unauthorized401();
        }

    }






















}
