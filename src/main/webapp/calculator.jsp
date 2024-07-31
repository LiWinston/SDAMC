<%--
  Created by IntelliJ IDEA.
  User: Winston
  Date: 2024/8/1
  Time: 上午1:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>数学计算器</title>
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
        .calculator {
            width: 100%;
            max-width: 400px;
            margin: 0 auto;
            border: 1px solid #ccc;
            padding: 10px;
        }
        input[type="text"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            font-size: 18px;
            box-sizing: border-box;
        }
        .buttons {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            grid-gap: 5px;
            column-gap: 5px;
            border-top: 1px solid #ccc;
        }
        button, .link-button {
            padding: 10px;
            font-size: 16px;
            border: 1px solid #ccc;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="calculator">
        <input type="text" id="display" readonly>
        <div class="buttons">
            <button onclick="appendNumber('7')">7</button>
            <button onclick="appendNumber('8')">8</button>
            <button onclick="appendNumber('9')">9</button>
            <button onclick="setOperator('/')">/</button>
            <button onclick="appendNumber('4')">4</button>
            <button onclick="appendNumber('5')">5</button>
            <button onclick="appendNumber('6')">6</button>
            <button onclick="setOperator('*')">*</button>
            <button onclick="appendNumber('1')">1</button>
            <button onclick="appendNumber('2')">2</button>
            <button onclick="appendNumber('3')">3</button>
            <button onclick="setOperator('-')">-</button>
            <button onclick="appendNumber('0')">0</button>
            <button onclick="appendNumber('.')">.</button>
            <button onclick="calculate()">=</button>
            <button onclick="setOperator('+')">+</button>
            <button onclick="clearDisplay()">C</button>
        </div>
    </div>
    <a href="index.jsp" class="link-button">返回首页</a>
    <a href="https://www.codecademy.com/learn/learn-java" class="link-button">阅读更多</a>
</div>
<script>
    let display = document.getElementById('display');
    let currentInput = '';
    let operator = null;
    let previousInput = null;

    function appendNumber(number) {
        currentInput += number;
        display.value = currentInput;
    }

    function setOperator(op) {
        if (previousInput !== null) {
            calculate();
        }
        operator = op;
        previousInput = parseFloat(currentInput);
        currentInput = '';
    }

    function calculate() {
        let result;
        let currentNumber = parseFloat(currentInput);
        switch (operator) {
            case '+':
                result = previousInput + currentNumber;
                break;
            case '-':
                result = previousInput - currentNumber;
                break;
            case '*':
                result = previousInput * currentNumber;
                break;
            case '/':
                if (currentNumber === 0) {
                    display.value = 'Error: Division by zero';
                    return;
                }
                result = previousInput / currentNumber;
                break;
        }
        display.value = result;
        currentInput = result.toString();
        operator = null;
        previousInput = null;
    }

    function clearDisplay() {
        display.value = '';
        currentInput = '';
        operator = null;
        previousInput = null;
    }
</script>
</body>
</html>