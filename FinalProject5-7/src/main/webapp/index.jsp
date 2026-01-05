<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>FinalProject5-7 - Web Application</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .info {
            margin: 20px 0;
            padding: 15px;
            background-color: #e7f3ff;
            border-left: 4px solid #2196F3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to FinalProject5-7 Web Application</h1>

        <div class="info">
            <p>This is a web application built with:</p>
            <ul>
                <li>Java Servlets</li>
                <li>JSP (JavaServer Pages)</li>
                <li>Apache Tomcat Server</li>
                <li>Maven for build management</li>
            </ul>
        </div>

        <h2>Available Servlets:</h2>
        <ul>
            <li><a href="hello">Hello World Servlet</a></li>
            <li><a href="functions">Functions Servlet</a></li>
        </ul>

        <p>Server time: <%= new java.util.Date() %></p>
    </div>
</body>
</html>