package org.sdamc.jspdemo;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
        out.println("<html><body>");
        //show a form to the user to enter the name of their pet
        out.println("<form action='hello-servlet' method='post'>");
        out.println("Enter the name of your pet: <input type='text' name='petName'>");
        out.println("<input type='submit'>");
        out.println("</form>");
        out.println("</body></html>");
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
        out.println("<html><body>");
        //get the name of the pet from the form
        String petName = request.getParameter("petName");
        out.println("Your pet's name is: " + petName);
        out.println("</body></html>");
        out.close();
    }

    public void destroy() {
    }
}