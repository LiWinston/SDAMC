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
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
                            <a class="btn btn-edit btn-sm" data-target="#editEventModal"
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
                            </a>

                            <form id="deleteForm_<%= event.getId() %>" style="display:inline;"
                                  onsubmit="event.preventDefault(); submitDeleteForm(this);">
                                <input type="hidden" name="eventId" value="<%= event.getId() %>"/>
                                <input type="hidden" name="clubId" value="<%= event.getClubId() %>"/>
                                <button type="submit" class="btn btn-delete btn-sm">
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
                    <select id="clubSelect" name="clubId" class="form-control mb-2 mr-sm-2" required>
                        <option value="">Select Club to Add Event</option>
                        <!-- Options will be dynamically loaded here -->
                    </select>
                </div>

                <input type="text" name="title" class="form-control mb-2 mr-sm-2" placeholder="Title" required/>
                <input type="text" name="description" class="form-control mb-2 mr-sm-2" placeholder="Description"/>
                <input type="text" name="venue" class="form-control mb-2 mr-sm-2" placeholder="Venue" required/>
                <input type="number" name="capacity" class="form-control mb-2 mr-sm-2" placeholder="Capacity"/>
                <input type="datetime-local" name="beginTime" class="form-control mb-2 mr-sm-2" placeholder="Begin Time"
                       required/>
                <input type="datetime-local" name="endTime" class="form-control mb-2 mr-sm-2" placeholder="End Time"/>
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
    // 全局数组存储用户管理的俱乐部 IDs
    let adminClubs = new Map();

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
                    showSweetAlert(result.code === 1 ? result.msg : "Request Denied due to " + result.msg, {
                        icon: result.code === 1 ? 'success' : 'error',
                        title: result.code === 1 ? 'Success' : 'Error',
                        confirmButtonColor: result.code === 1 ? '#3085d6' : '#d33'
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
        document.getElementById('editDescription').value = event.description;
        document.getElementById('editVenue').value = event.venue;
        document.getElementById('editCapacity').value = event.capacity;
        document.getElementById('editBeginTime').value = event.beginTime;
        document.getElementById('editEndTime').value = event.endTime;

        $('#editEventModal').modal('show');
    }

    function submitEditEvent() {
        const form = document.getElementById('editEventForm');
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

    window.onload = function () {
        const token = getToken();
        const userId = localStorage.getItem('userId');
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
                console.log('Clubs:', clubs);

                // Populate the clubSelect dropdown
                clubs.forEach(club => {
                    const option = document.createElement('option');
                    option.value = club.id;
                    option.textContent = club.name;
                    clubSelect.appendChild(option);
                    adminClubs.set(String(club.id), club.name);
                });
                console.log('Admin clubs:\n', adminClubs);
                if (adminClubs.size === 0) {
                    // Hide the create event form if no clubs are administered
                    // document.getElementById('createEventFormContainer').style.display = 'none';
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
                console.error('Error fetching clubs:', error);
                showSweetAlert('Failed to load clubs: ' + error.message);
            });

        // Handle form submission
        const form = document.getElementById('createEventForm');
        form.onsubmit = function (event) {
            event.preventDefault();

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            data['userId'] = userId;
            let reqBodyJson = JSON.stringify(data);
            // add local userID to request body

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
                        showSweetAlert(result.msg);  // 成功信息
                        window.location.href = '/events';
                    } else {
                        return Promise.reject(result.msg);  // 失败信息
                    }
                })
                .catch(error => {
                    console.error('Error creating event:', error);
                    showSweetAlert('Failed to create event: ' + error);
                });
        };
    };

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