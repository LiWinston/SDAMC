<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.sdamc.DomainObject.Events" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RSVP for Event</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
<div class="container">
    <h1 class="my-4">RSVP for Event</h1>

    <!-- 添加调试信息 -->
    <p>Debug: eventId attribute = ${eventId}</p>

    <%
        Events event = (Events) request.getAttribute("event");
//        if (event != null) {
    %>

    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title"><%= event.getTitle() %></h5>
            <p class="card-text"><%= event.getDescription() %></p>
            <p>Venue: <%= event.getVenue() %></p>
            <p>Date: <%= event.getBeginTime() %></p>
        </div>
    </div>

    <%--<% } else { %>
    <p>Debug: No event found</p>
    <% } %>--%>

    <form id="rsvpSubmitForm">
        <input type="hidden" id="eventId" name="eventId" value="${eventId}">
        <!-- 其他表单字段 -->
        <div id="attendees">
            <div class="attendee form-group">
                <input type="number" name="studentId[]" class="form-control mb-2" placeholder="Student ID" required>
                <input type="text" name="name[]" class="form-control mb-2" placeholder="Name" required>
                <input type="email" name="email[]" class="form-control mb-2" placeholder="Email" required>
            </div>
        </div>
        <button type="button" class="btn btn-secondary mb-2" onclick="addAttendee()">Add Attendee</button>
        <button type="submit" class="btn btn-primary">Submit RSVP</button>
    </form>
</div>

<script>
    function addAttendee() {
        var attendeesDiv = document.getElementById('attendees');
        var newAttendee = document.createElement('div');
        newAttendee.className = 'attendee form-group';
        newAttendee.innerHTML = `
        <input type="number" name="studentId[]" class="form-control mb-2" placeholder="Student ID" required>
        <input type="text" name="name[]" class="form-control mb-2" placeholder="Name" required>
        <input type="email" name="email[]" class="form-control mb-2" placeholder="Email" required>
    `;
        attendeesDiv.appendChild(newAttendee);
    }

    const form = document.getElementById('rsvpSubmitForm');
    form.onsubmit = function(e) {
        e.preventDefault();
        var formData = new FormData(form);

        var eventId = formData.get('eventId');
        console.log('Submitting form with eventId:', eventId);

        if (!eventId) {
            console.log('EventId is missing from form, trying to get from URL');
            var urlParams = new URLSearchParams(window.location.search);
            eventId = urlParams.get('eventId');
            console.log('EventId from URL:', eventId);
            if (eventId) {
                formData.set('eventId', eventId);
            } else {
                Swal.fire('Error', 'Event ID is missing', 'error');
                return;
            }
        }

        // 打印所有表单数据
        for (var pair of formData.entries()) {
            console.log(pair[0]+ ', ' + pair[1]);
        }

        // 获取所有attendee相关字段的数据
        var studentIds = formData.getAll('studentId[]');
        var names = formData.getAll('name[]');
        var emails = formData.getAll('email[]');

        // 构造attendee数组
        var attendees = [];
        for (var i = 0; i < studentIds.length; i++) {
            attendees.push({
                studentId: studentIds[i],
                name: names[i],
                email: emails[i]
            });
        }

        // 构造最终的JSON结构
        var rsvpData = {
            eventId: eventId,
            attendees: attendees
        };

        fetch('/rsvp/submit', {
            method: 'POST',
            headers: {
                <%--'Authorization': `Bearer ${token}`,--%>
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(rsvpData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Response:', data);
                if (data.code === 1) {
                    // Swal.fire('Success', data.msg, 'success');
                    Swal.fire({
                        title: 'Success',
                        text: data.msg,
                        icon: 'success',
                        timer: 2000,
                        showConfirmButton: false
                    }).then(() => {
                        window.location.href = '/events';
                    });
                } else {
                    // Swal.fire('Error', data.msg, 'error');
                    Swal.fire({
                        title: 'Error',
                        text: data.msg,
                        icon: 'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // Swal.fire('Error', "Failed to submit RSVP" + error, 'error');
                Swal.fire({
                    title: 'Error',
                    text: 'Failed to submit RSVP',
                    icon: 'error',
                    timer: 2000,
                    showConfirmButton: false
                });
            });
    };
</script>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>