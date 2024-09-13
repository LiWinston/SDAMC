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
    <link rel="stylesheet" href="css/events.css">
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
                        if (events != null) {
                            for (Events event : events) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() %>/events?id=<%= event.getId() %>"><%= event.getTitle() %>
                            </a></td>
                        <td><%= event.getDescription() %>
                        </td>
                        <td><%= event.getVenue() %>
                        </td>
                        <td><%= event.getCapacity() %>
                        </td>
                        <td><%= event.getBeginTime() %>
                        </td>
                        <td><%= event.getEndTime() %>
                        </td>
                        <td>
                            <a class="btn btn-edit btn-sm"
                               href="<%= request.getContextPath() %>/editEvent?id=<%= event.getId() %>"><i
                                    class="fas fa-edit"></i> Edit</a>
                            <form action="<%= request.getContextPath() %>/deleteEvent" method="post"
                                  style="display:inline;">
                                <input type="hidden" name="id" value="<%= event.getId() %>"/>
                                <input type="hidden" name="clubId" value="<%= event.getClubId() %>"/>
                                <button type="submit" class="btn btn-delete btn-sm"><i class="fas fa-trash-alt"></i>
                                    Delete
                                </button>
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
            <!-- Event creation form -->
            <form id="createEventForm" action="<%= request.getContextPath() %>/createEvent" method="post">
                <div class="form-group">
                    <label for="clubSelect">Select Club</label>
                    <select id="clubSelect" name="clubId" class="form-control mb-2 mr-sm-2" required>
                        <option value="">Select Club to Add Event</option>
                        <!-- Options will be dynamically loaded here -->
                    </select>
                </div>

                <input type="text" name="title" class="form-control mb-2 mr-sm-2" placeholder="Title" required/>
                <input type="text" name="description" class="form-control mb-2 mr-sm-2" placeholder="Description" required/>
                <input type="text" name="venue" class="form-control mb-2 mr-sm-2" placeholder="Venue" required/>
                <input type="number" name="capacity" class="form-control mb-2 mr-sm-2" placeholder="Capacity"/>
                <input type="datetime-local" name="beginTime" class="form-control mb-2 mr-sm-2" placeholder="Begin Time" required/>
                <input type="datetime-local" name="endTime" class="form-control mb-2 mr-sm-2" placeholder="End Time" required/>
                <button type="submit" class="btn btn-custom mb-2">Create</button>
            </form>
        </div>
    </div>

    <!-- Modal for editing event -->
    <div class="modal fade" id="editEventModal" tabindex="-1" aria-labelledby="editEventLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editEventLabel">Edit Event</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editEventForm">
                        <input type="hidden" name="id" id="editEventId">
                        <div class="form-group">
                            <label for="editTitle">Title</label>
                            <input type="text" class="form-control" id="editTitle" name="title">
                        </div>
                        <div class="form-group">
                            <label for="editDescription">Description</label>
                            <input type="text" class="form-control" id="editDescription" name="description">
                        </div>
                        <div class="form-group">
                            <label for="editVenue">Venue</label>
                            <input type="text" class="form-control" id="editVenue" name="venue">
                        </div>
                        <div class="form-group">
                            <label for="editCapacity">Capacity</label>
                            <input type="number" class="form-control" id="editCapacity" name="capacity">
                        </div>
                        <div class="form-group">
                            <label for="editBeginTime">Begin Time</label>
                            <input type="datetime-local" class="form-control" id="editBeginTime" name="beginTime">
                        </div>
                        <div class="form-group">
                            <label for="editEndTime">End Time</label>
                            <input type="datetime-local" class="form-control" id="editEndTime" name="endTime">
                        </div>
                        <button type="button" class="btn btn-primary" onclick="submitEditEvent()">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal for RSVP -->
    <div class="modal fade" id="rsvpModal" tabindex="-1" aria-labelledby="rsvpLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="rsvpLabel">RSVP for Event</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="rsvpForm">
                        <div id="attendeeInputs">
                            <div class="form-group">
                                <label for="attendeeEmail">Attendee Email</label>
                                <input type="email" class="form-control" id="attendeeEmail" name="attendeeEmails[]">
                            </div>
                        </div>
                        <button type="button" class="btn btn-secondary" onclick="addAttendee()">Add Attendee</button>
                        <button type="button" class="btn btn-primary" onclick="submitRSVP()">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS and dependencies -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script>
    function openEditEventModal(event) {
        // Populate modal fields with event data
        document.getElementById('editEventId').value = event.id;
        document.getElementById('editTitle').value = event.title;
        document.getElementById('editDescription').value = event.description;
        document.getElementById('editVenue').value = event.venue;
        document.getElementById('editCapacity').value = event.capacity;
        document.getElementById('editBeginTime').value = event.beginTime;
        document.getElementById('editEndTime').value = event.endTime;

        $('#editEventModal').modal('show');
    }

    function submitEditEvent() {
        const form = document.getElementById('editEventForm');
        const eventId = document.getElementById('editEventId').value;
        const formData = new FormData(form);
        const jsonData = JSON.stringify(Object.fromEntries(formData.entries()));

        fetch(`/events/${eventId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonData
        }).then(response => {
            if (response.ok) {
                location.reload();
            }
        });
    }

    function openRSVPModal(eventId) {
        $('#rsvpModal').modal('show');
        document.getElementById('rsvpForm').dataset.eventId = eventId;
    }

    function addAttendee() {
        const attendeeDiv = document.createElement('div');
        attendeeDiv.className = 'form-group';
        attendeeDiv.innerHTML = `
            <label for="attendeeEmail">Attendee Email</label>
            <input type="email" class="form-control" name="attendeeEmails[]">
        `;
        document.getElementById('attendeeInputs').appendChild(attendeeDiv);
    }

    function submitRSVP() {
        const form = document.getElementById('rsvpForm');
        const eventId = form.dataset.eventId;
        const formData = new FormData(form);
        const jsonData = JSON.stringify(Object.fromEntries(formData.entries()));

        fetch(`/events/${eventId}/rsvp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonData
        }).then(response => {
            if (response.ok) {
                location.reload();
            }
        });
    }

    window.onload = function() {
        function getToken() {
            const tokenData = localStorage.getItem('token');
            if (!tokenData) return null;

            const parsedToken = JSON.parse(tokenData);
            const now = new Date().getTime();

            // 验证 token 是否过期
            if (now > parsedToken.expiry) {
                localStorage.removeItem('token');
                return null;
            }
            return parsedToken.token;
        }
        const token = getToken();
        const userId = localStorage.getItem('userId');
        console.log('userId:', userId);
        const clubSelect = document.getElementById('clubSelect');

        if (!token) {
            alert("You must be logged in to create events");
            window.location.href = 'login.jsp';
            return;
        }
        if (!userId) {
            alert("token OK, User ID not set in local storage");
            return;
        }


        let number = Number(userId);
        console.log('id:', number, 'type:', typeof number);
        if (!number || isNaN(number)) {
            alert("Invalid user ID");
            return;
        }
        let url = '/user/' + number + '/clubs';
        console.log('Constructed URL:', url);
        // Fetch clubs administered by the user
        fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(clubs => {
                // Populate the clubSelect dropdown
                clubs.forEach(club => {
                    const option = document.createElement('option');
                    option.value = club.id;
                    option.textContent = club.name;
                    clubSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error fetching clubs:', error);
                alert('Failed to load clubs: ' + error.message);
            });

        // Handle form submission
        const form = document.getElementById('createEventForm');
        form.onsubmit = function(event) {
            event.preventDefault();

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());

            fetch('/events', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (response.ok) {
                        alert('Event created successfully');
                        window.location.href = '/events';
                    } else {
                        return response.text().then(text => { throw new Error(text); });
                    }
                })
                .catch(error => {
                    console.error('Error creating event:', error);
                    alert('Failed to create event: ' + error.message);
                });
        };
    };
</script>
<!-- Include Bootstrap JS and jQuery for modal functionality -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>