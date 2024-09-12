<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login/Register</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="auth-container">
    <h2 id="formTitle">Login</h2>
    <form id="authForm">
        <div class="username-container">
            <label for="email">Email</label>
            <input type="text" id="email" placeholder="Email" required>

            <!-- 用户名仅在注册时显示 -->
            <label for="username" id="usernameLabel" style="display:none;">Username</label>
            <input type="text" id="username" placeholder="Username" style="display:none;">
        </div>

        <div class="password-container">
            <label for="passwordInput">Password</label>
            <input type="password" id="passwordInput" placeholder="Password" required>
            <button type="button" class="toggle-password" id="togglePasswordBtn">👁️</button>
        </div>

        <button type="submit" class="auth-button">Login</button>
    </form>

    <button id="switchButton" onclick="toggleForm()" class="auth-button">Switch to Register</button>
    <p id="errorMessage" class="error-message"></p>
</div>

<script>
    let isLogin = true;

    // 切换密码显示
    function togglePassword() {
        const passwordInput = document.getElementById('passwordInput');
        const toggleButton = document.getElementById('togglePasswordBtn');
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        toggleButton.textContent = type === 'password' ? '👁️' : '🙈';
    }

    // 绑定事件
    document.getElementById('togglePasswordBtn').addEventListener('click', togglePassword);

    // 切换登录/注册表单
    function toggleForm() {
        const formTitle = document.getElementById('formTitle');
        const submitButton = document.querySelector('.auth-button[type="submit"]');
        const usernameLabel = document.getElementById('usernameLabel');
        const usernameInput = document.getElementById('username');
        const switchButton = document.getElementById('switchButton');

        if (isLogin) {
            formTitle.textContent = 'Register';
            submitButton.textContent = 'Register';
            usernameLabel.style.display = 'block';
            usernameInput.style.display = 'block';
            usernameInput.setAttribute('required', 'required');
            switchButton.textContent = 'Switch to Login';
        } else {
            formTitle.textContent = 'Login';
            submitButton.textContent = 'Login';
            usernameLabel.style.display = 'none';
            usernameInput.style.display = 'none';
            usernameInput.removeAttribute('required');
            switchButton.textContent = 'Switch to Register';
        }
        isLogin = !isLogin;
    }

    document.getElementById('authForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('passwordInput').value;
        const username = isLogin ? null : document.getElementById('username').value;

        const BaseUrl = "";
        const url = isLogin ? BaseUrl + '/user/login' : BaseUrl + '/user/register';
        const payload = isLogin ? {email, password} : {email, username, password};

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                const contentType = response.headers.get('content-type');
                if (!response.ok) {
                    if (contentType && contentType.includes('application/json')) {
                        // 处理 JSON 格式的错误信息
                        return response.json().then(data => {
                            throw new Error(`${response.status}: ${data.message}`);
                        });
                    } else {
                        // 处理非 JSON 响应，显示状态码
                        throw new Error(`${response.status}: Unknown error (non-JSON response)`);
                    }
                }
                return response.json();
            })
            .then(data => {
                if (data.token) {
                    window.location.href = 'events.jsp';
                }
            })
            .catch(error => {
                // 显示错误信息
                document.getElementById('errorMessage').textContent = error.message;
            });
    });
</script>
</body>
</html>
