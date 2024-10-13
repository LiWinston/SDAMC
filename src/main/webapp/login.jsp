<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login/Register</title>
    <link rel="stylesheet" href="css/login.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
        //清空错误消息
        document.getElementById('errorMessage').textContent = '';
        isLogin = !isLogin;
    }

    // 新增：处理成功登录后的重定向
    function handleSuccessfulLogin(result) {
        function setTokenWithExpiry(token, expiryTimeInMs) {
            const now = new Date().getTime();
            const item = {
                token: token,
                expiry: now + expiryTimeInMs
            };
            localStorage.setItem('token', JSON.stringify(item));
        }

        setTokenWithExpiry(result.data.token, 240 * 1000);  // 设置4分钟有效期
        localStorage.setItem('userId', result.data.id);
        localStorage.setItem('userName', result.data.name);
        console.log(result.data.id + " : " + localStorage.getItem('token'));

        // 使用 SweetAlert2 显示多选项重定向对话框
        Swal.fire({
            title: 'Login Successful',
            text: 'Where would you like to go?',
            icon: 'success',
            confirmButtonText: 'Events',
            showCancelButton: true,
            cancelButtonText: 'Dashboard',
            showDenyButton: true,
            denyButtonText: 'Others',
            showCloseButton: true,
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = "/events";
            } else if (result.isDenied) {
                window.location.href = "/Others";
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                window.location.href = "/dashboard.jsp";
            } else {
                // 如果用户关闭对话框，默认重定向到事件页面
                window.location.href = "/events";
            }
        });
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
                        return response.json().then(data => {
                            throw new Error(data.msg || 'Unknown error');
                        });
                    } else {
                        throw new Error(`${response.status}: Unknown error (non-JSON response)`);
                    }
                }
                return response.json();
            })
            .then(result => {
                if (result.code === 1) {
                    if (isLogin) {
                        handleSuccessfulLogin(result);
                    } else {
                        Swal.fire({
                            title: 'Registration successful',
                            icon: 'success',
                            timer: 2000,
                            showConfirmButton: false
                        }).then(() => {
                            document.getElementById('errorMessage').textContent = '';
                            toggleForm();
                        });
                    }
                } else {
                    throw new Error(result.msg || 'Unknown error');
                }
            })
            .catch(error => {
                document.getElementById('errorMessage').textContent = error.message;
            });
    });
</script>
</body>
</html>