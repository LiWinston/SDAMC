<%@ page import="org.sdamc.Pojo.Event" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Manage Events</title>
</head>
<body>
<h1>Events for Club</h1>
<table border="1">
    <tr>
        <th>Title</th>
        <th>Description</th>
        <th>Venue</th>
        <th>Capacity</th>
        <th>Club</th>
        <th>Actions</th>
    </tr>
    <%
        List<Event> events = (List<Event>) request.getAttribute("events");
        if (events != null) {
            for (Event event : events) {
    %>
    <tr>
        <td><a href="<%= request.getContextPath() %>/eventDetails?id=<%= event.getId() %>"><%= event.getTitle() %></a></td>
        <td><%= event.getDescription() %></td>
        <td><%= event.getVenue() %></td>
        <td><%= event.getCapacity() %></td>
        <td><a href="<%= request.getContextPath() %>/clubDetails?id=<%= event.getClubId() %>"><%= event.getClubName() %></a></td>
        <td>
            <a href="<%= request.getContextPath() %>/editEvent?id=<%= event.getId() %>">Edit</a>
            <form action="<%= request.getContextPath() %>/deleteEvent?id=<%= event.getId() %>" method="post" style="display:inline;">
                <input type="submit" value="Delete"/>
            </form>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="6">No events available</td>
    </tr>
    <%
        }
    %>
</table>

<h2>Create New Event</h2>
<form action="<%= request.getContextPath() %>/createEvent" method="post">
    <input type="text" name="title" placeholder="Title" required/>
    <input type="text" name="description" placeholder="Description" required/>
    <input type="text" name="venue" placeholder="Venue" required/>
    <input type="number" name="capacity" placeholder="Capacity"/>
    <input type="hidden" name="clubId" value="<%= request.getParameter("clubId") %>"/>
    <input type="submit" value="Create"/>
</form>
</body>
</html>
