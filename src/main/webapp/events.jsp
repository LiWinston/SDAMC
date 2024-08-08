<%--
  Created by IntelliJ IDEA.
  User: Winston
  Date: 2024/8/8
  Time: 下午11:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%--<%@ taglib uri="http://jakarta.apache.org/taglibs/standard-1.2" prefix="c" %>--%>
<html>
<head>
    <title>Manage Events</title>
</head>
<body>
<h1>Events for Club</h1>
<table>
    <tr>
        <th>Title</th>
        <th>Description</th>
        <th>Venue</th>
        <th>Capacity</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="event" items="${events}">
        <tr>
            <td><c:out value="${event.title}"/></td>
            <td><c:out value="${event.description}"/></td>
            <td><c:out value="${event.venue}"/></td>
            <td><c:out value="${event.capacity}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/clubs/${clubId}/events?id=${event.id}">Edit</a>
                <form action="${pageContext.request.contextPath}/clubs/${clubId}/events?id=${event.id}" method="post" style="display:inline;">
                    <input type="hidden" name="_method" value="delete"/>
                    <input type="submit" value="Delete"/>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<h2>Create New Event</h2>
<form action="${pageContext.request.contextPath}/clubs/${clubId}/events" method="post">
    <input type="text" name="title" placeholder="Title" required/>
    <input type="text" name="description" placeholder="Description" required/>
    <input type="text" name="venue" placeholder="Venue" required/>
    <input type="number" name="capacity" placeholder="Capacity"/>
    <input type="submit" value="Create"/>
</form>
</body>
</html>
