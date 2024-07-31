    package org.winston.jspdemo;
    import java.io.*;

    import jakarta.servlet.RequestDispatcher;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.*;
    import jakarta.servlet.annotation.*;

    @WebServlet(name = "simpleCalculatorServlet", value = "/simple-calculator-servlet")
    public class SimpleCalculatorServlet extends HttpServlet {
        private String message;

        public void init() {
            message = "Simple Calculator";
        }

        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.setAttribute("message", message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("calculator.jsp");
            dispatcher.forward(request, response);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String firstNumberStr = request.getParameter("firstNumber");
            String secondNumberStr = request.getParameter("secondNumber");
            String operator = request.getParameter("operator");
            double result = 0;
            String errorMessage = null;

            try {
                double firstNumber = Double.parseDouble(firstNumberStr);
                double secondNumber = Double.parseDouble(secondNumberStr);

                switch (operator) {
                    case "+":
                        result = firstNumber + secondNumber;
                        break;
                    case "-":
                        result = firstNumber - secondNumber;
                        break;
                    case "*":
                        result = firstNumber * secondNumber;
                        break;
                    case "/":
                        if (secondNumber == 0) {
                            errorMessage = "Error: Division by zero";
                        } else {
                            result = firstNumber / secondNumber;
                        }
                        break;
                    default:
                        errorMessage = "Invalid operator";
                }
            } catch (NumberFormatException e) {
                errorMessage = "请输入有效的数字";
            }

            request.setAttribute("result", result);
            request.setAttribute("errorMessage", errorMessage);

            RequestDispatcher dispatcher = request.getRequestDispatcher("calculator.jsp");
            dispatcher.forward(request, response);
        }

        public void destroy() {
        }

    }
