package servlets;

import SQL.DTO.requestDTO.LoginRequest;
import SQL.DTO.requestDTO.RegisterRequest;
import SQL.Server.Server;
import SQL.Server.ServerSingleton;
import SQL.Server.exception.ServerError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import SQL.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private Server server;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);

    private StringBuilder readRequest(HttpServletRequest request){
        StringBuilder jsonBody = new StringBuilder();
        try(BufferedReader reader = request.getReader()){
            String line;
            while((line = reader.readLine()) != null){
                jsonBody.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonBody;
    }

    @Override
    public void init() {
        try {
            server = ServerSingleton.getINSTANCE();
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AuthServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if ("/register".equals(pathInfo))  /// REGISTER
        {
            handleRegister(request, response,"/register");
        }
        else if ("/login".equals(pathInfo)) /// LOGIN
        {
            handleLogin(request, response,"/login");
        }
        else /// ERROR
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }




    /// =============================================HANDLERS==========================================
    private void handleRegister(HttpServletRequest request, HttpServletResponse response,String path)
            throws IOException {


        RegisterRequest registerRequest = objectMapper.readValue(readRequest(request).toString(),
                                                                    RegisterRequest.class);

        try {
            var authResponse = server.registerUser(registerRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), authResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse(path);
            response.setStatus(errorResponse.status());
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {

        LoginRequest loginRequest = objectMapper.readValue(readRequest(request).toString(),
                                                                            LoginRequest.class);

        try {
            var logResponse = server.loginUser(loginRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), logResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse(path);
            response.setStatus(errorResponse.status());
        }
    }
}