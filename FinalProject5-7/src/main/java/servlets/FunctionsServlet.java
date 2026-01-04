package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;
import functions.interfaces.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

@WebServlet("/functions")
public class FunctionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Functions Servlet</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
            out.println(".container { max-width: 800px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>Functions Servlet - Tabulated Functions Demo</h1>");
            
            // Create sample tabulated functions
            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {2.0, 4.0, 6.0, 8.0, 10.0};
            
            // Create ArrayTabulatedFunction
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            
            // Create LinkedListTabulatedFunction
            TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);
            
            out.println("<h2>Array Tabulated Function:</h2>");
            out.println("<table>");
            out.println("<tr><th>X</th><th>Y</th></tr>");
            for (int i = 0; i < arrayFunc.getCount(); i++) {
                out.println("<tr><td>" + arrayFunc.getX(i) + "</td><td>" + arrayFunc.getY(i) + "</td></tr>");
            }
            out.println("</table>");
            
            out.println("<h2>Linked List Tabulated Function:</h2>");
            out.println("<table>");
            out.println("<tr><th>X</th><th>Y</th></tr>");
            for (int i = 0; i < linkedListFunc.getCount(); i++) {
                out.println("<tr><td>" + linkedListFunc.getX(i) + "</td><td>" + linkedListFunc.getY(i) + "</td></tr>");
            }
            out.println("</table>");
            
            // Test function operations
            out.println("<h2>Function Operations:</h2>");
            out.println("<p>Function value at x=3.0 (Array): " + arrayFunc.apply(3.0) + "</p>");
            out.println("<p>Function value at x=4.0 (LinkedList): " + linkedListFunc.apply(4.0) + "</p>");
            out.println("<p>Function count (Array): " + arrayFunc.getCount() + "</p>");
            out.println("<p>Function count (LinkedList): " + linkedListFunc.getCount() + "</p>");
            
            out.println("<br><a href='index.jsp'>Back to Home</a>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}