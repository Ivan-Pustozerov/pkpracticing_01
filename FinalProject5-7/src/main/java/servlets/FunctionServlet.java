package servlets;

import SQL.DTO.requestDTO.AnalyticFunctionRequest;
import SQL.DTO.requestDTO.FunctionRangeRequest;
import SQL.DTO.requestDTO.RegisterRequest;
import SQL.DTO.requestDTO.TabulatedFunctionRequest;
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
import SQL.Services.FunctionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/functions/*")
public class FunctionServlet extends HttpServlet {
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
            throw new RuntimeException("Failed to initialize FunctionServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if(pathInfo == null){
            handleGetAllFunctions(request,response);
        }else{
            String[] pathParts = pathInfo.split("/");
            if(pathParts.length >= 2){
                Long funcId = Long.parseLong(pathParts[1]);
                handleGetFunction(request,response,funcId);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/analytic".equals(pathInfo)) /// ANALYTIC
        {
            handlePostAnalytic(request,response);

        }
        else if ("/tabulated".equals(pathInfo)) /// TABULATED
        {
            handlePostTabulated(request,response);
        }
        else if (pathInfo != null) /// CALCULATE
        {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && "calculate".equals(pathParts[2])) {
                Long funcId = Long.parseLong(pathParts[1]);
                handlePostCalculate(request,response,funcId);
            }
        }
        else /// ERROR
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length >= 2) {
            Long funcId = Long.parseLong(pathParts[1]);
            handleDelete(request,response,funcId);
        }
    }

    /// =======================================HANDLERS=====================================

    private void handleGetAllFunctions(HttpServletRequest request, HttpServletResponse response)
            throws IOException {


        String token = extractTokenFromRequest(request);

        try{
            var functionResponseArray = server.getAllFunctions(token,null);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), functionResponseArray);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }
    private void handleGetFunction(HttpServletRequest request, HttpServletResponse response, long funcId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        try{
            var functionResponse = server.getSpecificFunction(token,funcId);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), functionResponse);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }


    private void handlePostAnalytic(HttpServletRequest request, HttpServletResponse response)
            throws IOException {


        String token = extractTokenFromRequest(request);

        AnalyticFunctionRequest analyticRequest = objectMapper.readValue(readRequest(request).toString(),
                                                                            AnalyticFunctionRequest.class);

        try{
            var functionResponse = server.postAnalyticFunction(token,analyticRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), functionResponse);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }
    private void handlePostTabulated(HttpServletRequest request, HttpServletResponse response)
            throws IOException {


        String token = extractTokenFromRequest(request);

        TabulatedFunctionRequest tabulatedRequest = objectMapper.readValue(readRequest(request).toString(),
                TabulatedFunctionRequest.class);

        try{
            var functionResponse = server.postTabulatedFunction(token,tabulatedRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), functionResponse);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }
    private void handlePostCalculate(HttpServletRequest request, HttpServletResponse response, long funcId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        var rangeRequest = objectMapper.readValue(readRequest(request).toString(),
                FunctionRangeRequest.class);

        try{
            var PointResponseArray = server.calculateFunction(token,funcId,rangeRequest);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), PointResponseArray);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, long funcId)
            throws IOException {

        String token = extractTokenFromRequest(request);

        try{
            server.deleteFunction(token,funcId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ServerError e) {
            var errorResponse = e.getErrorResponse("-");
            response.setStatus(errorResponse.status());
        }

    }

}