package servlets;

import SQL.DTO.requestDTO.RegisterRequest;
import SQL.Server.Server;
import SQL.Server.ServerSingleton;
import SQL.Server.exception.ServerError;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnectionException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import SQL.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private Server server;
    private ObjectMapper objectMapper;
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
    private String extractTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public void init() {
        try {
            server = ServerSingleton.getINSTANCE();
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServlet", e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if(pathInfo == null){
            handleGetAllUsers(request,response);
        }else{
            String[] pathParts = pathInfo.split("/");
            if(pathParts.length >= 2){
                Long userId = Long.parseLong(pathParts[1]);
                handleGetUser(request,response,userId);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/admin")) /// new ADMIN
        {
            handlePostAdmin(request,response);
        }
        else /// new USER
        {
            handlePostUser(request,response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();

        Long userId = null;
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if(pathParts.length >= 2){
                 userId = Long.parseLong(pathParts[1]);
            }

            if (pathParts.length >= 3 && "role".equals(pathParts[2])) /// change ROLE
            {
                handlePutRole(request, response, userId);
            }
            else /// change USER
            {
                handlePut(request, response, userId);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {

        String pathInfo = request.getPathInfo();
        Long userId = null;
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 2) {
                userId = Long.parseLong(pathParts[1]);
                handleDelete(request,response,userId);
            }
        }
    }




    /// =======================================HANDLERS=====================================

    private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response)
            throws IOException {


        String token = extractTokenFromRequest(request);

        try{
            var userResponseArray = server.getAllUsers(token);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponseArray);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }
    private void handleGetUser(HttpServletRequest request, HttpServletResponse response,long userId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        try{
            var userResponse = server.getSpecificUser(token,userId);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponse);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }


    private void handlePostUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String token = extractTokenFromRequest(request);

        RegisterRequest registerRequest = objectMapper.readValue(readRequest(request).toString(),
                RegisterRequest.class);

        try {
            var userResponse = server.postUser(token,registerRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }
    private void handlePostAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String token = extractTokenFromRequest(request);

        RegisterRequest registerRequest = objectMapper.readValue(readRequest(request).toString(),
                RegisterRequest.class);

        try {
            var userResponse = server.postUserAdmin(token,registerRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }


    private void handlePut(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        RegisterRequest registerRequest = objectMapper.readValue(readRequest(request).toString(),
                RegisterRequest.class);

        if(userId == null){
            userId = server.getUserIdFromToken(token);
        }

        try {
            var userResponse = server.putUser(token,userId,registerRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }
    private void handlePutRole(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        Boolean isAdmin = objectMapper.readValue(readRequest(request).toString(),Boolean.class);

        if(userId == null){
            userId = server.getUserIdFromToken(token);
        }

        try {
            var userResponse = server.putUserRole(token,userId,isAdmin);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), userResponse);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }


    private void handleDelete(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        try {
            server.deleteUser(token,userId);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }
    }
}