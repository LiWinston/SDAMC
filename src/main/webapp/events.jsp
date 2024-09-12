<%@ page import="org.sdamc.DomainObject.Events " %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Events</title>
    <!-- Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f7f7f7;
            padding-top: 20px;
        }

        .container {
            max-width: 1500px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            color: #343a40;
            font-size: 2.5rem;
            font-weight: 700;
            text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
        }

        .card {
            border-radius: 15px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
            background-color: #ffffff;
        }

        .card-header {
            background-color: #007bff;
            color: #ffffff;
            font-size: 1.25rem;
            border-top-left-radius: 15px;
            border-top-right-radius: 15px;
            padding: 15px;
            text-align: center;
        }

        .card-body {
            padding: 20px;
        }

        .table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            margin-bottom: 0;
        }

        .table th,
        .table td {
            text-align: center;
            vertical-align: middle;
            padding: 15px;
            border-bottom: 1px solid #e0e0e0;
        }

        .table thead th {
            background-color: #343a40;
            color: #ffffff;
            font-weight: bold;
            border-top: none;
        }

        .table tbody tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .table tbody tr:hover {
            background-color: #e9ecef;
            transition: background-color 0.3s ease;
        }

        .btn-custom {
            background-color: #007bff;
            color: #ffffff;
            border: none;
            border-radius: 5px;
            padding: 10px 15px;
            transition: background-color 0.3s ease;
        }

        .btn-custom:hover {
            background-color: #0056b3;
        }

        .btn-edit {
            background-color: #ffc107;
            color: #212529;
            border-radius: 5px;
            padding: 8px 12px;
            transition: background-color 0.3s ease;
        }

        .btn-edit:hover {
            background-color: #e0a800;
        }

        .btn-delete {
            background-color: #dc3545;
            color: #ffffff;
            border-radius: 5px;
            padding: 8px 12px;
            transition: background-color 0.3s ease;
        }

        .btn-delete:hover {
            background-color: #c82333;
        }

        .form-inline input {
            margin-right: 10px;
            border-radius: 5px;
            padding: 10px;
            box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
            transition: box-shadow 0.3s ease;
        }

        .form-inline input:focus {
            box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1), 0 0 5px rgba(0, 123, 255, 0.3);
        }

        .form-inline button {
            border-radius: 5px;
            padding: 10px 15px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1 class="mb-4">Events for Club</h1>
    </div>

    <div class="card">
        <div class="card-header">
            <i class="fas fa-calendar-alt"></i> Event List
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Venue</th>
                        <th>Capacity</th>
                        <th>Begin</th>
                        <th>End</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        List<Events> events = (List<Events>) request.getAttribute("events");
                        if (events!= null) {
                            for (Events event : events) {
                    %>
                    <tr>
                        <td><a href="<%= request.getContextPath() %>/events?id=<%= event.getId() %>"><%= event.getTitle() %></a></td>
                        <td><%= event.getDescription() %></td>
                        <td><%= event.getVenue() %></td>
                        <td><%= event.getCapacity() %></td>
                        <td><%= event.getBeginTime() %></td>
                        <td><%= event.getEndTime() %></td>
<%--                        <td><a href="<%= request.getContextPath() %>/clubDetails?id=<%= event.getClubId() %>"><%= event.getClubName() %></a></td>--%>
                        <td>
                            <a class="btn btn-edit btn-sm" href="<%= request.getContextPath() %>/editEvent?id=<%= event.getId() %>"><i class="fas fa-edit"></i> Edit</a>
                            <form action="<%= request.getContextPath() %>/deleteEvent" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="<%= event.getId() %>"/>
                                <input type="hidden" name="clubId" value="<%= event.getClubId() %>"/>
                                <button type="submit" class="btn btn-delete btn-sm"><i class="fas fa-trash-alt"></i> Delete</button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center">No events available</td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="card mt-5">
        <div class="card-header">
            <i class="fas fa-plus"></i> Create New Event
        </div>
        <div class="card-body">
            <form action="<%= request.getContextPath() %>/createEvent" method="post" class="form-inline">
                <input type="text" name="title" class="form-control mb-2 mr-sm-2" placeholder="Title" required/>
                <input type="text" name="description" class="form-control mb-2 mr-sm-2" placeholder="Description" required/>
                <input type="text" name="venue" class="form-control mb-2 mr-sm-2" placeholder="Venue" required/>
                <input type="number" name="capacity" class="form-control mb-2 mr-sm-2" placeholder="Capacity"/>
                <input type="datetime-local" name="beginTime" class="form-control mb-2 mr-sm-2" placeholder="Begin Time" required/>
                <input type="datetime-local" name="endTime" class="form-control mb-2 mr-sm-2" placeholder="End Time" required/>
                <input type="hidden" name="clubId" value="<%= request.getParameter("clubId") %>"/>
                <button type="submit" class="btn btn-custom mb-2">Create</button>
            </form>

        </div>
    </div>
</div>

<!-- Bootstrap JS and dependencies -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>