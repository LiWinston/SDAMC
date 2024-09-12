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
        }
        .main-content {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
            background-color: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
    </style>
</head>
<body>
<div class="main-content">
    <div class="text-content">
        <div class="artistic-text">SDAMC Event MG</div>
        <a href="events.jsp" class="btn btn-primary">Show events</a>
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
