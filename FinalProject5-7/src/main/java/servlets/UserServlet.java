package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import SQL.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // Initialize the UserService with database connection details
        try {
            userService = new UserService("jdbc:postgresql://localhost:5432/Final",
                                         "postgres", "lkroot");
            objectMapper = new ObjectMapper();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for GET /api/users and /api/users/{id}
        response.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/admin")) /// new ADMIN
        {
            // TODO: Implementation for POST /api/users/admin
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else /// new USER
        {
            // TODO: Implementation for POST /api/users
            response.setStatus(HttpServletResponse.SC_OK);

        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");

            if (pathParts.length >= 3 && "role".equals(pathParts[2])) /// change ROLE
            {
                // TODO: Implementation for PUT /api/users/{id}/role
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else /// change USER
            {
                // TODO: Implementation for PUT /api/users/{id}
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Update user endpoint called\"}");
                out.flush();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: Implementation for DELETE /api/users/{id}
        response.setStatus(HttpServletResponse.SC_OK);
    }
}