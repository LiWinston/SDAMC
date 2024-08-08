<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
<%--    set message = "Hello, JSP!"--%>
    <%
        request.setAttribute("message", "Hello, JSP!");
    %>
    <title>JSP - Hello World</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            max-width: 90%;
            min-width: 300px;
            width: 100%;
        }
    </style>
</head>
<body>
<div class="container">
    <h1><%= request.getAttribute("message") %></h1>
    <br/>
    <a href="hello-servlet" class="btn btn-primary">Hello Servlet</a>
    <br/>
    <a href="simple-calculator-servlet" class="btn btn-primary">Simple Calculator</a>
    <!-- 添加测试链接 -->
    <a href="events" class="btn btn-primary">Test EventServlet</a>
</div>
</body>
</html>