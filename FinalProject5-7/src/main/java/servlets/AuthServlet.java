package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import SQL.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // Initialize the UserService with database connection details
        try {
            userService = new UserService("jdbc:postgresql://localhost:5432/math_functions_db", 
                                         "postgres", "password");
            objectMapper = new ObjectMapper();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AuthServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();

        if ("/register".equals(pathInfo)) {
            handleRegister(request, response);
        } else if ("/login".equals(pathInfo)) {
            handleLogin(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Endpoint not found\"}");
            out.flush();
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for user registration
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print("{\"message\":\"Register endpoint called\"}");
        out.flush();
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for user login
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print("{\"message\":\"Login endpoint called\"}");
        out.flush();
    }
}