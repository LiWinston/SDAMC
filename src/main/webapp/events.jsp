<%@ page import="org.sdamc.DomainObject.Events " %>
<%@ page import="org.sdamc.DomainObject.Students " %>
<%@ page import="org.sdamc.DTO.ClubMember " %>
<%@ page import="org.sdamc.DomainObject.ClubMemberships " %>
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
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <link rel="stylesheet" href="css/events.css">
</head>
<body>
<div class="container">
    <header class="header">
        <div class="user-info">
            <span id="userLabel">User ID: </span><span id="userIdDsp"></span> -
            <span id="nameLabel">Username: </span><span id="userNameDsp"></span>
        </div>
        <h1>Upcoming Events</h1>
        <div class="logout-button">
            <button id="logoutBtn" class="btn btn-danger">Logout</button>
        </div>
    </header>


    <div class="card">
        <div class="card-header">
            Event List
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Venue</th>
                        <th>Capacity</th>
                        <th>Begin</th>
                        <th>End</th>
                        <th>Actions</th>
                        <!-- 新增的 RSVP 列标题 -->
                        <th>RSVP</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        List<Events> events = (List<Events>) request.getAttribute("events");
                        if (events != null && !events.isEmpty()) {
                            for (Events event : events) {
                    %>
                    <tr>
                        <td>
                            <a href="<%= request.getContextPath() %>/events?id=<%= event.getId() %>"><%= event.getTitle() %></a>
                        </td>
                        <td><%= event.getDescription() %></td>
                        <td><%= event.getVenue() %></td>
                        <td><%= event.getCapacity() %></td>
                        <td><%= event.getBeginTime() %></td>
                        <td><%= event.getEndTime() %></td>
                        <!-- 新增的 RSVP 按钮列 -->
                        <td>
                            <a href="<%= request.getContextPath() %>/rsvp?eventId=<%= event.getId() %>" class="btn btn-primary">RSVP</a>
                        </td>
                        <td>
                            <button class="btn btn-edit" data-target="#editEventModal"
                                    data-id="<%= event.getId() %>" onclick="openEditEventModal({
                                    id: '<%= event.getId() %>',
                                    clubId: '<%= event.getClubId() %>',
                                    title: '<%= event.getTitle() %>',
                                    description: '<%= event.getDescription() %>',
                                    venue: '<%= event.getVenue() %>',
                                    capacity: '<%= event.getCapacity() %>',
                                    beginTime: '<%= event.getBeginTime() %>',
                                    endTime: '<%= event.getEndTime() %>'
                                    })">
                                <i class="fas fa-edit"></i> Edit
                            </button>

                            <form id="deleteForm_<%= event.getId() %>" style="display:inline;"
                                  onsubmit="event.preventDefault(); submitDeleteForm(this);">
                                <input type="hidden" name="eventId" value="<%= event.getId() %>"/>
                                <input type="hidden" name="clubId" value="<%= event.getClubId() %>"/>
                                <button type="submit" class="btn btn-delete">
                                    <i class="fas fa-trash-alt"></i> Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="8" class="text-center">No events available</td>
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
            Your RSVP'd Events
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table id="userRsvpsTable" class="table">
                    <thead>
                    <tr>
                        <th>Event Title</th>
<%--                        <th>Begin Time</th>--%>
<%--                        <th>End Time</th>--%>
                        <th>Venue</th>
                        <th>Attendee Name</th>
                        <th>Attendee Email</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- RSVP 列表将通过 JavaScript 动态加载到这里 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="card mt-5" id="createEventFormContainer">
        <div class="card-header">
            <i class="fas fa-plus"></i> Create New Event
        </div>
        <div class="card-body">
            <div id="noAdminAccessOverlay" class="overlay-text" style="display: none;">
                <p>You are not an admin of any clubs. Please contact an admin for administrator status.</p>
            </div>
            <!-- Event creation form -->
            <form id="createEventForm" action="<%= request.getContextPath() %>/createEvent" method="post">
                <div class="form-group">
                    <label for="clubSelect">Select Club</label>
                    <select id="clubSelect" name="clubId" class="club-select" required>
                        <option value="">Select Club to Add Event</option>
                        <!-- Options will be dynamically loaded here -->
                    </select>
                </div>

                <input type="text" name="title" class="form-control" placeholder="Title" required/>
                <input type="text" name="description" class="form-control" placeholder="Description"/>
                <input type="text" name="venue" class="form-control" placeholder="Venue" required/>
                <input type="number" name="capacity" class="form-control" placeholder="Capacity"/>
                <input type="datetime-local" name="beginTime" class="form-control" placeholder="Begin Time" value="<%= java.time.LocalDateTime.now().toString().substring(0, 16) %>"/>
                <input type="datetime-local" name="endTime" class="form-control" placeholder="End Time"/>
                <button type="submit" class="btn btn-custom">Create</button>
            </form>
        </div>
    </div>

    <div class="card mt-5" id="clubManageCard">
        <div class="card-header">
            Manage Club Members
        </div>
        <div class="card-body">
            <div id="noAdminManageAccessOverlay" class="overlay-text" style="display: none;">
                <p>No permission to Club Admins Management</p>
            </div>
            <div class="table-responsive">
                <div class="form-group">
                    <label for="clubManageSelect">Select Club</label>
                    <select id="clubManageSelect" name="clubId" class="club-select" required>
                        <option value="">Select Club to Manage</option>
                        <!-- Options will be dynamically loaded here -->
                    </select>
                </div>
                <table id="memberTable" class="table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                <table id="fundingTable" class="table">
                    <thead>
                    <tr>
                        <th>Description</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                <form id="createFundingForm" action="<%= request.getContextPath() %>/submit" method="post">
                    <input type="text" name="description" class="form-control" placeholder="Description" required/>
                    <input type="number" name="amount" class="form-control" placeholder="Amount" required/>
                    <button type="submit" class="btn btn-custom">Submit</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal for editing funding application -->
    <div class="modal fade" id="editFundingModal" tabindex="-1" aria-labelledby="editFundingLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editFundingLabel">Edit Funding Application</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editFundingForm">
                        <input type="hidden" name="applicationId" id="applicationId">

                        <div class="form-group">
                            <label for="description">Description</label>
                            <input type="text" class="form-control" id="description" name="description" required>
                        </div>

                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" class="form-control" id="amount" name="amount" min="0" required>
                        </div>

                        <button type="button" id="saveEdit" class="btn btn-primary">Save</button>
                    </form>
                </div>
            </div>
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
                        <input type="hidden" name="eventId" id="eventId">
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
                        <button type="button" class="btn btn-custom" onclick="submitEditEvent()">Submit</button>
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
                        <button type="button" class="btn btn-custom" onclick="submitRSVP()">Submit</button>
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
    document.getElementById('logoutBtn').addEventListener('click', function() {
        // Clear user ID and token from local storage
        localStorage.removeItem('userId');
        localStorage.removeItem('token');

        // Redirect to login page
        window.location.href = 'login.jsp';
    });

    // 全局数组存储用户管理的俱乐部 IDs
    let adminClubs = new Map();

    // 全局数组存储用户管理的俱乐部 IDs for super admins
    let superAdminClubs = new Map();

    // 验证用户是否可以编辑事件
    function canEditEvent(clubId) {
        return adminClubs.has(String(clubId));
    }

    function submitDeleteForm(form) {
        const formData = new FormData(form);

        const userId = localStorage.getItem('userId');
        const clubId = formData.get('clubId');
        const eventId = formData.get('eventId');

        // 使用新的 showSweetChoice 替代原有的 confirmation 逻辑
        showSweeetChoice(
            "Are you sure you want to delete this event_" +
            eventId + " from club_" + clubId + " as user " + userId + "? This requires admin access.",
            {
                title: 'Confirm Delete',
                icon: 'warning',
                confirmButtonText: 'Delete',
                confirmButtonColor: '#d33',
                cancelButtonText: 'Cancel',
                cancelButtonColor: '#3085d6'
            }
        ).then((result) => {
            if (result.isConfirmed) {
                // 用户确认删除的逻辑
                console.log('Event deleted');

                // 检查 token 是否存在
                const token = getToken();
                if (!token) {
                    showSweetAlert("You must be logged in to create events");
                    window.location.href = 'login.jsp';
                    return;
                }

                // 发出删除请求
                fetch('<%= request.getContextPath() %>/events', {
                    method: 'delete',
                    headers: {
                        'Authorization': "Bearer " + token,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({userId, eventId, clubId})
                }).then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        console.error(response);
                        showSweetAlert('Failed to delete the event, due to \r\n  ' + response.statusText);
                    }
                }).then(result => {
                    showSweeetChoice(result.code === 1 ? result.msg : "Request Denied due to " + result.msg, {
                        icon: result.code === 1 ? 'success' : 'error',
                        title: result.code === 1 ? 'Success' : 'Error',
                        confirmButtonColor: result.code === 1 ? '#3085d6' : '#d33'
                    }).then(() => {
                        window.location.href = '/events';
                    });
                }).catch(error => console.error('Error:', error));
            } else if (result.isDismissed) {
                // 用户取消删除的逻辑
                console.log('Deletion cancelled');
            }
        });
    }


    function openEditEventModal(event) {
        if (!canEditEvent(event.clubId)) {
            showSweetAlert("Not authorized to edit this event, need to be an admin of the club " + event.clubId
                + ". You are admin of clubs: " + (adminClubs.size > 0 ? Array.from(adminClubs.values()).join(", ") : "None"),
                {
                    icon: 'error',
                    title: 'Access Denied',
                    confirmButtonColor: '#d33'
                });
            return;
        }

        // Populate modal fields with event data
        document.getElementById('eventId').value = event.id;
        document.getElementById('editTitle').value = event.title;
        document.getElementById('editTitle').setAttribute('data-original', event.title);
        document.getElementById('editDescription').value = event.description;
        document.getElementById('editDescription').setAttribute('data-original', event.description);
        document.getElementById('editVenue').value = event.venue;
        document.getElementById('editVenue').setAttribute('data-original', event.venue);
        document.getElementById('editCapacity').value = event.capacity;
        document.getElementById('editCapacity').setAttribute('data-original', event.capacity);
        document.getElementById('editBeginTime').value = event.beginTime;
        document.getElementById('editBeginTime').setAttribute('data-original', event.beginTime);
        document.getElementById('editEndTime').value = event.endTime;
        document.getElementById('editEndTime').setAttribute('data-original', event.endTime);

        $('#editEventModal').modal('show');
    }

    function submitEditEvent() {
        const form = document.getElementById('editEventForm');
        const formData = new FormData(form);

        const originalValues = {
            eventId: document.getElementById('eventId').value,
            title: document.getElementById('editTitle').getAttribute('data-original'),
            description: document.getElementById('editDescription').getAttribute('data-original'),
            venue: document.getElementById('editVenue').getAttribute('data-original'),
            capacity: document.getElementById('editCapacity').getAttribute('data-original'),
            beginTime: document.getElementById('editBeginTime').getAttribute('data-original'),
            endTime: document.getElementById('editEndTime').getAttribute('data-original')
        };

        const modifiedFields = {};
        for (const [key, value] of formData.entries()) {
            if (key === 'beginTime' || key === 'endTime') {
                // Trim seconds before comparing
                const trimmedValue = trimSecondsFromTime(value);
                if (trimmedValue !== originalValues[key]) {
                    modifiedFields[key] = trimmedValue;
                }
            } else if (value !== originalValues[key]) {
                modifiedFields[key] = value;
            }
        }
        // Helper function to remove seconds from datetime-local format
        function trimSecondsFromTime(timeStr) {
            if (timeStr.length >= 19) { // Format with seconds: YYYY-MM-DDTHH:mm:ss
                return timeStr.slice(0, 16); // Keep only YYYY-MM-DDTHH:mm
            }
            return timeStr; // If no seconds, return as-is
        }

        // Always include the eventId as it is needed for the update
        modifiedFields['eventId'] = originalValues.eventId;

        const jsonData = JSON.stringify(modifiedFields);

        fetch(`/events/${originalValues.eventId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: jsonData
        }).then(response => {
            if (response.ok) {
                showSweetAlert('Event updated successfully', {
                    icon: 'success',
                    title: 'Success',
                    showConfirmButton: false
                });
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showSweetError("Failed to update event: " + response.statusText);
            }
        }).catch(error => {
            console.error('Error updating event:', error);
            showSweetError('Failed to update event: ' + error);
            setTimeout(() => {
                window.location.reload();
            }, 1300);
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

    window.onload = function () {
        const token = getToken();
        const userId = localStorage.getItem('userId');
        const userName = localStorage.getItem('userName');

        // 设置用户信息
        document.getElementById('userIdDsp').textContent = userId;
        document.getElementById('userNameDsp').textContent = userName;

        console.log('userId:', userId);
        const clubSelect = document.getElementById('clubSelect');

        if (!token) {
            showSweetAlert("You must be logged in to create events");
            window.location.href = 'login.jsp';
            return;
        }
        if (!userId) {
            showSweetAlert("token OK, User ID not set in local storage");
            return;
        }

        let number = Number(userId);
        console.log('id:', number, 'type:', typeof number);
        if (!number || isNaN(number)) {
            showSweetAlert("Invalid user ID");
            return;
        }

        fetchAdminedClubsByUser(number, token, clubSelect)
            .then(clubs => {
                if (clubs.length === 0) {  // 使用 clubs.length 而不是 clubs.size
                    // Hide the create event form if no clubs are administered
                    document.getElementById('createEventFormContainer').classList.add('disabled-form');
                    document.getElementById('noAdminAccessOverlay').style.display = 'flex';  // 显示提示文字

                    // 禁用所有表单元素
                    const formElements = document.querySelectorAll('#createEventForm input, #createEventForm select, #createEventForm button');
                    formElements.forEach(element => {
                        element.disabled = true;  // 禁用每个元素
                    });
                }
            })
            .catch(error => {
                showSweetAlert(error.message);
            });

        fetchAdminedClubsByUser(number, token, clubManageSelect)
            .then(clubs => {
                if (clubs.length === 0) {  // 使用 clubs.length 而不是 clubs.size
                    // Hide the create event form if no clubs are administered
                    document.getElementById('clubManageCard').classList.add('disabled-form');
                    document.getElementById('noAdminManageAccessOverlay').style.display = 'flex';  // 显示提示文字
                }
            })
            .catch(error => {
                showSweetAlert(error.message);
            });


        // Handle form submission
        const form = document.getElementById('createEventForm');
        form.onsubmit = function (event) {
            event.preventDefault();

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            data['userId'] = userId;
            let reqBodyJson = JSON.stringify(data);

            fetch('/events', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: reqBodyJson
            })
                .then(response => response.json())  // 解析 JSON 响应
                .then(result => {
                    if (result.code === 1) {
                        showSweeetChoice(result.msg, {
                            title: 'Success',
                            icon: 'success',
                            confirmButtonText: 'OK',
                            confirmButtonColor: '#3085d6'
                        }).then(() => {
                            window.location.reload();
                        });
                    } else {
                        return Promise.reject(result.msg);  // 失败信息
                    }
                })
                .catch(error => {
                    console.error('Error creating event:', error);
                    showSweetAlert('Failed to create event: ' + error);
                });
        };

        const form2 = document.getElementById('createFundingForm');
        form2.onsubmit = function (event) {
            event.preventDefault();

            const formData = new FormData(form2);
            const data = Object.fromEntries(formData.entries());
            const clubId = document.getElementById('clubManageSelect').value;
            if (!clubId) {
                swal.fire({
                    icon: 'warning',
                    title: 'Warning',
                    text: 'Please select a club to manage.'
                });
                return;
            }
            data['studentId'] = userId;
            data['clubId'] = clubId;
            let reqBodyJson = JSON.stringify(data);

            fetch('/funding/submit', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: reqBodyJson
            })
                .then(response => response.json())  // 解析 JSON 响应
                .then(result => {
                    if (result.code === 1) {
                        showSweeetChoice(result.msg, {
                            title: 'Success',
                            icon: 'success',
                            confirmButtonText: 'OK',
                            confirmButtonColor: '#3085d6'
                        }).then(() => {
                            fetchFundingApplications(clubId, token);
                        });
                    } else {
                        showSweetAlert('Error creating funding: ' + result.msg, { icon: 'error' });
                        fetchFundingApplications(clubId, token);
                    }
                })
                .catch(error => {
                    console.error('Error creating funding:', error);
                    showSweetAlert('Failed to create event: ' + error, { icon: 'error' });
                });
        };

        // 加载用户的 RSVP 列表
        loadUserRsvps();
    };

    function loadUserRsvps() {
        const userId = localStorage.getItem('userId');
        const token = getToken();

        if (!userId || !token) {
            console.error('User ID or token not found');
            return;
        }

        fetch('/events/user-rsvps?userId=' + userId, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.code === 1) {
                    displayUserRsvps(data.data);
                } else {
                    console.error('Failed to load RSVPs:', data.msg);
                }
            })
            .catch(error => {
                console.error('Error loading RSVPs:', error);
            });
    }

    function displayUserRsvps(rsvps) {
        const rsvpTableBody = document.querySelector('#userRsvpsTable tbody');
        rsvpTableBody.innerHTML = ''; // Clear existing content

        if (rsvps && rsvps.length > 0) {
            rsvps.forEach(rsvp => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>\${rsvp.eventTitle}</td>
                    <td>\${rsvp.venue}</td>
                    <td>\${rsvp.attendeeName}</td>
                    <td>\${rsvp.attendeeEmail}</td>
                    <td>
                        <button class="btn btn-danger" onclick="cancelRsvp(\${rsvp.rsvpId})">
                            Cancel RSVP
                        </button>
                    </td>
                `;
                rsvpTableBody.appendChild(row);
            });
        } else {
            rsvpTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">You haven't RSVP'd to any events yet</td>
            </tr>
        `;
        }
    }

    function cancelRsvp(rsvpId) {
        Swal.fire({
            title: 'Are you sure?',
            text: 'Do you want to cancel this RSVP?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, cancel it!'
        }).then((result) => {
            if (result.isConfirmed) {
                const token = getToken();
                fetch('/rsvp/cancel', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ rsvpId: rsvpId })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.code === 1) {
                            showSweetAlert('RSVP cancelled successfully', {
                                icon: 'success',
                                title: 'Success'
                            });
                            loadUserRsvps(); // Reload the RSVP list
                            setTimeout(() => {
                                window.location.reload(); // 直接刷新页面
                            }, 1500); // 等待1.5秒后刷新，给用户时间看到成功消息
                        } else {
                            showSweetAlert(data.msg, {
                                icon: 'error',
                                title: 'Error'
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showSweetAlert('An error occurred while cancelling the RSVP', {
                            icon: 'error',
                            title: 'Error'
                        });
                    });
            }
        });
    }

    function fetchSuperAdminedClubsByUser(userId, token, clubSelect) {
        let url = '/user/' + userId + '/clubs_super';
        console.log('Constructed URL:', url);

        // Fetch clubs administered by the user
        return fetch(url, {
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
                console.log('Clubs:', clubs);

                superAdminClubs.clear();  // Clear the map before populating it
                // Populate the clubSelect dropdown
                clubs.forEach(club => {
                    const option = document.createElement('option');
                    option.value = club.id;
                    option.textContent = club.name;
                    clubSelect.appendChild(option);
                    superAdminClubs.set(String(club.id), club.name);
                });
                console.log('Super admin clubs:\n', superAdminClubs);
                return clubs;
            })
            .catch(error => {
                console.error('Error fetching clubs:', error);
                throw new Error('Failed to load clubs: ' + error.message);
            });
    }

    function fetchAdminedClubsByUser(userId, token, clubSelect) {
        let url = '/user/' + userId + '/clubs';
        console.log('Constructed URL:', url);

        // Fetch clubs administered by the user
        return fetch(url, {
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
                console.log('Clubs:', clubs);

                adminClubs.clear();  // Clear the map before populating it
                // Populate the clubSelect dropdown
                clubs.forEach(club => {
                    const option = document.createElement('option');
                    option.value = club.id;
                    option.textContent = club.name;
                    clubSelect.appendChild(option);
                    adminClubs.set(String(club.id), club.name);
                });
                console.log('Admin clubs:\n', adminClubs);
                return clubs;
            })
            .catch(error => {
                console.error('Error fetching clubs:', error);
                throw new Error('Failed to load clubs: ' + error.message);
            });
    }

    function fetchFundingApplications(clubId, token) {
        let url = '/funding/' + clubId + '/club';
        console.log('Constructed URL:', url);

        // Fetch clubs administered by the user
        return fetch(url, {
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
            .then(applications => {
                console.log('Applications:', applications);
                const table = document.getElementById('fundingTable');
                const tableBody = table.querySelector('tbody');
                tableBody.innerHTML = ''; // 清空现有表格内容

                applications.forEach(row => {
                    const tr = document.createElement('tr');

                    const nameTd = document.createElement('td');
                    nameTd.textContent = row.description;
                    tr.appendChild(nameTd);

                    const emailTd = document.createElement('td');
                    emailTd.textContent = row.amount;
                    tr.appendChild(emailTd);

                    const roleTd = document.createElement('td');
                    roleTd.textContent = row.status;
                    tr.appendChild(roleTd);
                    const actionsTd = document.createElement('td');

                    if(row.status == "submitted" || row.status == "in_review"){
                        const deleteButton = document.createElement('button');
                        deleteButton.textContent = 'Cancel Funding';
                        deleteButton.addEventListener('click', () => {
                            const fid = row.id;
                            let url = '/funding/' + fid + '/cancel';

                            fetch(url, {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            }).then(response => {
                                if (response.ok) {
                                    showSweetAlert('Funding application canceled.');
                                    fetchFundingApplications(clubId, token);
                                }
                            }).catch(error => {
                                console.error('There was an error!', error);
                                showSweetAlert('Error canceling funding application: ' + error.message);
                            });
                        });
                        actionsTd.appendChild(deleteButton);

                        // Edit Button
                        const editButton = document.createElement('button');
                        editButton.textContent = 'Edit';
                        editButton.addEventListener('click', () => {
                            openEditFundingModal(row, clubId, token);  // Call function to open edit modal
                        });
                        actionsTd.appendChild(editButton);
                    }
                    tr.appendChild(actionsTd);
                    tableBody.appendChild(tr); // 添加行到表格
                });
            })
            .catch(error => {
                console.error('Error fetching members:', error);
                throw new Error('Failed to load club members: ' + error.message);
            });
    }

    // Function to open edit modal
    function openEditFundingModal(row, clubId, token) {
        const modal = document.getElementById('editFundingModal');
        const descriptionInput = document.getElementById('description');
        const amountInput = document.getElementById('amount');
        const saveButton = document.getElementById('saveEdit');

        // Populate the modal with the current values
        descriptionInput.value = row.description;
        amountInput.value = row.amount;
        $('#editFundingModal').modal('show');

        saveButton.onclick = function () {
            const data = {
                 "id": row.id.toString(),
                 "description": descriptionInput.value,
                 "amount": amountInput.value
            };

            let reqBodyJson = JSON.stringify(data);

            fetch('/funding/update', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: reqBodyJson
            }).then(response => {
                  if (response.ok) {
                      showSweetAlert('Funding application updated.');
                      fetchFundingApplications(clubId, token);
                  } else {
                      showSweetError("Failed to update funding: " + response.msg);
                  }
              }).catch(error => {
                  console.error('There was an error!', error);
                  showSweetAlert('Error updating funding application: ' + error.message);
              });
        };
    }

    function fetchClubMembers(clubId, token) {
        let url = '/club/' + clubId + '/all';
        console.log('Constructed URL:', url);

        // Fetch clubs administered by the user
        return fetch(url, {
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
            .then(members => {
                console.log('Members:', members);
                const table = document.getElementById('memberTable');
                const tableBody = table.querySelector('tbody');
                tableBody.innerHTML = ''; // 清空现有表格内容

                members.forEach(row => {
                    const tr = document.createElement('tr');

                    const nameTd = document.createElement('td');
                    nameTd.textContent = row.name;
                    tr.appendChild(nameTd);

                    const emailTd = document.createElement('td');
                    emailTd.textContent = row.email;
                    tr.appendChild(emailTd);

                    const roleTd = document.createElement('td');
                    roleTd.textContent = row.role;
                    tr.appendChild(roleTd);
                    const actionsTd = document.createElement('td');
                    if(localStorage.getItem('userId') != row.studentId){
                        if(row.role == "normal_member"){
                            const editButton = document.createElement('button');
                            editButton.textContent = 'Set Admin';
                            editButton.addEventListener('click', () => {
                                const cmid = row.id;
                                let url = '/club/' + cmid + '/admin';

                                fetch(url, {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }).then(response => {
                                    if (response.ok) {
                                        showSweetAlert('Role updated to admin successfully!');
                                        fetchClubMembers(clubId, token);
                                    }
                                }).catch(error => {
                                    console.error('There was an error!', error);
                                    showSweetAlert('Error updating role to Admin: ' + error.message);
                                });
                            });
                            actionsTd.appendChild(editButton);
                        }
                        if(row.role == "admin"){
                            const deleteButton = document.createElement('button');
                            deleteButton.textContent = 'Cancel Admin';
                            deleteButton.addEventListener('click', () => {
                                const cmid = row.id;
                                let url = '/club/' + cmid + '/normal_member';

                                fetch(url, {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }).then(response => {
                                    if (response.ok) {
                                        showSweetAlert('Role updated to normal_member successfully!');
                                        fetchClubMembers(clubId, token);
                                    }
                                }).catch(error => {
                                    console.error('There was an error!', error);
                                    showSweetAlert('Error updating role to Normal Member: ' + error.message);
                                });
                            });
                            actionsTd.appendChild(deleteButton);
                        }
                    }
                    tr.appendChild(actionsTd);
                    tableBody.appendChild(tr); // 添加行到表格
                });
            })
            .catch(error => {
                console.error('Error fetching members:', error);
                throw new Error('Failed to load club members: ' + error.message);
            });
    }

    document.getElementById('clubManageSelect').addEventListener('change', function() {
        if(this.value == 0){
            return;
        }
        const token = getToken();
        const userId = localStorage.getItem('userId');
        console.log('userId:', userId);

        if (!token) {
            showSweetAlert("You must be logged in to create events");
            window.location.href = 'login.jsp';
            return;
        }
        if (!userId) {
            showSweetAlert("token OK, User ID not set in local storage");
            return;
        }

        let number = Number(userId);
        console.log('id:', number, 'type:', typeof number);
        if (!number || isNaN(number)) {
            showSweetAlert("Invalid user ID");
            return;
        }

        const selectedClub = this.value; // 获取当前选中的值

        fetchClubMembers(selectedClub, token);
        fetchFundingApplications(selectedClub, token);
    });


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
        console.log('get Token@' + now + " : " + parsedToken.token);
        return parsedToken.token;
    }


    function showSweeetChoice(message, options = {}) {
        const {
            title = 'Notification',
            icon = 'warning',  // 警告图标默认使用 'warning'
            confirmButtonText = 'OK',
            confirmButtonColor = '#3085d6',
            cancelButtonText = 'Cancel',
            cancelButtonColor = '#d33'
        } = options;

        return Swal.fire({
            title: title,
            text: message,
            icon: icon,
            showCancelButton: true,
            confirmButtonText: confirmButtonText,
            confirmButtonColor: confirmButtonColor,
            cancelButtonText: cancelButtonText,
            cancelButtonColor: cancelButtonColor
        });
    }

    function showSweetAlertWithRetVal(message, options = {}) {
        const {
            title = 'Notification',
            icon = 'warning',
        } = options;

        return Swal.fire({
            title: title,
            text: message,
            icon: icon,
            confirmButtonText: confirmButtonText,
            confirmButtonColor: confirmButtonColor
        });
    }

    // 默认弹窗，允许传入自定义参数
    function showSweetAlert(message, options = {}) {
        const {
            title = 'Notification',   // 默认标题
            icon = 'success',         // 默认图标
            confirmButtonText = 'OK', // 默认确认按钮文字
            confirmButtonColor = '#3085d6'  // 默认确认按钮颜色 (蓝色)
        } = options;

        Swal.fire({
            title: title,
            text: message,
            icon: icon,
            confirmButtonText: confirmButtonText,
            confirmButtonColor: confirmButtonColor
        });
    }


    function showSweetError(message) {
        showSweetAlert(message, {
            icon: 'error',
            title: 'Error',
            confirmButtonColor: '#d33'
        });
    }
</script>
<!-- Include Bootstrap JS and jQuery for modal functionality -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>