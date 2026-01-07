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

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/analytic".equals(pathInfo)) /// ANALYTIC
        {
            // TODO: Implementation for POST /api/functions/analytic
            response.setStatus(HttpServletResponse.SC_OK);

        }
        else if ("/tabulated".equals(pathInfo)) /// TABULATED
        {
            // TODO: Implementation for POST /api/functions/tabulated
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else if (pathInfo != null) /// CALCULATE
        {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && "calculate".equals(pathParts[2])) {
                // TODO: Implementation for POST /api/functions/{id}/calculate + ID!
                response.setStatus(HttpServletResponse.SC_OK);

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
        // TODO: Implementation for DELETE /api/functions/{id}
        response.setStatus(HttpServletResponse.SC_OK);
    }
}