<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SDAMC Event MG</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            display: flex;
            height: 100vh;
            background-color: #f4f4f4;
            align-items: center;
            justify-content: center;
            position: relative;
            overflow: hidden;
        }

        .main-content {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
            background-color: rgba(255, 255, 255, 0.65); /* 增加透明度 */
            border-radius: 12px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            position: relative;
            z-index: 1;
        }

        .spotify-player {
            width: 400px;
            height: 352px;
            margin-left: 20px;
        }

        .artistic-text {
            font-size: 2.5rem;
            font-weight: bold;
            background: linear-gradient(45deg, #ff0066, #ffcc00);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
            padding: 10px;
            border-radius: 12px;
            margin-bottom: 20px;
        }

        .rainbow-halo {
            position: absolute;
            width: 200%;
            height: 200%;
            background: linear-gradient(90deg, red, orange, yellow, green, blue, indigo, violet);
            animation: wave 10s linear infinite; /* 光谱波动画 */
            border-radius: 50%;
            filter: blur(100px);
            z-index: 0;
            opacity: 0.88; /* 调低饱和度 */
            background-size: 200% 200%; /* 确保背景大小适合动画 */
        }

        @keyframes wave {
            0% {
                background-position: 0 50%;
            }
            50% {
                background-position: 100% 50%;
            }
            100% {
                background-position: 0% 50%;
            }
        }

        .overlay {
            position: absolute;
            width: 100%;
            height: 100%;
            background-color: rgba(255, 255, 255, 0.3); /* 灰白色蒙板 */
            z-index: 1;
        }

        .logo {
            position: absolute;
            top: 20px;
            left: 20px;
            width: 100px;
            z-index: 2;
        }
    </style>
</head>
<body>

<script>
    function getToken() {
        const tokenData = localStorage.getItem('token');
        if (!tokenData) return null;

        const parsedToken = JSON.parse(tokenData);
        const now = new Date().getTime();

        if (now > parsedToken.expiry) {
            localStorage.removeItem('token');
            return null;
        }
        console.log('Token is valid' + parsedToken.token);
        return parsedToken.token;
    }

    window.onload = function() {
        const token = getToken();
        const btnShowEvents = document.getElementById('showEventsBtn');

        if (!token) {
            btnShowEvents.onclick = function() {
                window.location.href = 'login.jsp';
            };
        } else {
            btnShowEvents.onclick = function() {
                window.location.href = '/events';
            };
        }
    };
</script>

<!-- 墨尔本大学校徽 -->
<img src="https://designsystem.web.unimelb.edu.au/static/img/logo-icon.svg" alt="University of Melbourne Logo" class="logo">

<!-- 彩虹光晕 -->
<div class="rainbow-halo"></div>
<div class="overlay"></div>

<!-- 主内容 -->
<div class="main-content">
    <div class="text-content">
        <div class="artistic-text">SDAMC Event MG</div>
        <button id="showEventsBtn" class="btn btn-primary">Show events</button>
    </div>

    <iframe
            src="https://open.spotify.com/embed/track/1ESotnG260HrjQcBZrlL2m?utm_source=generator"
            class="spotify-player"
            frameborder="0"
            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
            loading="lazy"
    ></iframe>
</div>
</body>
</html>
