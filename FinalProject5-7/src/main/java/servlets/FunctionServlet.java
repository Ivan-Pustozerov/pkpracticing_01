package servlets;

import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnectionException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import SQL.Services.UserService;
import SQL.Services.FunctionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/functions/*")
public class FunctionServlet extends HttpServlet {
    private UserService userService;
    private FunctionService functionService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // Initialize the services with database connection details
        try {
            userService = new UserService("jdbc:postgresql://localhost:5432/Final",
                                         "postgres", "lkroot");
            functionService = new FunctionService("jdbc:postgresql://localhost:5432/Final",
                                                "postgres", "lkroot");
            objectMapper = new ObjectMapper();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize FunctionServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for GET /api/functions and /api/functions/{id}
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String name;
        try {
            var result = userService.readUserInfo(new long[]{1},"-","-");
            name = result.get(0).name();
        } catch (SmartConnectionException e) {
            throw new RuntimeException(e);
        } catch (SQLRepositoryException e) {
            throw new RuntimeException(e);
        }

        out.print("{\"message\":\"GET functions endpoint called\"} " + name);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/analytic".equals(pathInfo)) {
            // TODO: Implementation for POST /api/functions/analytic
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"Create analytic function endpoint called\"}");
            out.flush();
        } else if ("/tabulated".equals(pathInfo)) {
            // TODO: Implementation for POST /api/functions/tabulated
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"Create tabulated function endpoint called\"}");
            out.flush();
        } else if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && "calculate".equals(pathParts[2])) {
                // TODO: Implementation for POST /api/functions/{id}/calculate
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Calculate function endpoint called\"}");
                out.flush();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid path\"}");
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for DELETE /api/functions/{id}
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print("{\"message\":\"Delete function endpoint called\"}");
        out.flush();
    }
}