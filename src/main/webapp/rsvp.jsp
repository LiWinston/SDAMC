<%@ page import="org.sdamc.DomainObject.Events" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RSVP for Event</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Arial', sans-serif;
        }
        .container {
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            padding: 30px;
            margin-top: 50px;
        }
        h1 {
            color: #007bff;
            font-weight: bold;
            margin-bottom: 30px;
        }
        .event-card {
            background-color: #f1f8ff;
            border-left: 5px solid #007bff;
        }
        .form-control {
            border-radius: 20px;
        }
        .btn {
            border-radius: 20px;
            padding: 10px 20px;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-secondary {
            background-color: #6c757d;
            border-color: #6c757d;
        }
        .attendee {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <%
        Events event = (Events) request.getAttribute("event");
//        if (event != null) {
    %>

    <h1 class="text-center"><i class="fas fa-calendar-check mr-2"></i>RSVP for Event</h1>

    <div class="card mb-4 event-card">
        <div class="card-body">
            <h5 class="card-title"><%= event.getTitle() %></h5>
            <p class="card-text"><%= event.getDescription() %></p>
            <p><i class="fas fa-map-marker-alt mr-2"></i>Venue: <%= event.getVenue() %></p>
            <p><i class="far fa-calendar-alt mr-2"></i>Date: <%= event.getBeginTime() %></p>
        </div>
    </div>

    <form id="rsvpSubmitForm">
        <input type="hidden" id="eventId" name="eventId" value="${eventId}">
        <div id="attendees">
            <div class="attendee">
                <h5 class="mb-3">Attendee Information</h5>
                <div class="form-group">
                    <input type="number" name="studentId[]" class="form-control" placeholder="Student ID" required>
                </div>
                <div class="form-group">
                    <input type="text" name="name[]" class="form-control" placeholder="Name" required>
                </div>
                <div class="form-group">
                    <input type="email" name="email[]" class="form-control" placeholder="Email" required>
                </div>
            </div>
        </div>
        <div class="text-center mt-4">
            <button type="button" class="btn btn-secondary mb-3" onclick="addAttendee()">
                <i class="fas fa-user-plus mr-2"></i>Add Attendee
            </button>
            <button type="submit" class="btn btn-primary mb-3 ml-2">
                <i class="fas fa-paper-plane mr-2"></i>Submit RSVP
            </button>
        </div>
    </form>
</div>

<script>
    function addAttendee() {
        var attendeesDiv = document.getElementById('attendees');
        var newAttendee = document.createElement('div');
        newAttendee.className = 'attendee mt-4';
        newAttendee.innerHTML = `
            <h5 class="mb-3">Additional Attendee</h5>
            <div class="form-group">
                <input type="number" name="studentId[]" class="form-control" placeholder="Student ID" required>
            </div>
            <div class="form-group">
                <input type="text" name="name[]" class="form-control" placeholder="Name" required>
            </div>
            <div class="form-group">
                <input type="email" name="email[]" class="form-control" placeholder="Email" required>
            </div>
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

        // Print all form data
        for (var pair of formData.entries()) {
            console.log(pair[0]+ ', ' + pair[1]);
        }

        // Get all attendee-related field data
        var studentIds = formData.getAll('studentId[]');
        var names = formData.getAll('name[]');
        var emails = formData.getAll('email[]');

        // Construct attendee array
        var attendees = [];
        for (var i = 0; i < studentIds.length; i++) {
            attendees.push({
                studentId: studentIds[i],
                name: names[i],
                email: emails[i]
            });
        }

        // Construct final JSON structure
        var rsvpData = {
            eventId: eventId,
            attendees: attendees
        };

        fetch('/rsvp/submit', {
            method: 'POST',
            headers: {
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