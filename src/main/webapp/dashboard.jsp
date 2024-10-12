<%@ page import="org.sdamc.DomainObject.Events " %>
<%@ page import="org.sdamc.DomainObject.Students " %>
<%@ page import="org.sdamc.DTO.ClubMember " %>
<%@ page import="org.sdamc.DTO.Fundings " %>
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
    <link rel="stylesheet" href="css/dashboard.css">
</head>

<body>
<div class="container">
    <header class="header">
        <h1>Funding Applications</h1>
    </header>

    <div class="card">
        <div class="card-header">
            Funding Applications List
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table id="fundingTable" class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Club</th>
                            <th>Applicant</th>
                            <th>Description</th>
                            <th>Amount</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="7" class="text-center">Loading funding applications...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    // 使用 fetchFundingApplications 加载 funding applications 数据
    document.addEventListener('DOMContentLoaded', function () {
        const token = localStorage.getItem('token');  // 从 localStorage 获取 token
        fetchFundingApplications(token);
    });

    // 定义 fetchFundingApplications 函数
    function fetchFundingApplications(token) {
        let url = '/funding/all';
        console.log('Constructed URL:', url);

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
                tableBody.innerHTML = '';  // 清空表格内容

                if (applications.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No funding applications found</td></tr>';
                    return;
                }

                applications.forEach(row => {
                    const tr = document.createElement('tr');

                    const idTd = document.createElement('td');
                    idTd.textContent = row.id;
                    tr.appendChild(idTd);

                    const clubTd = document.createElement('td');
                    clubTd.textContent = row.clubName;
                    tr.appendChild(clubTd);

                    const applicantTd = document.createElement('td');
                    applicantTd.textContent = row.applicant;
                    tr.appendChild(applicantTd);

                    const descriptionTd = document.createElement('td');
                    descriptionTd.textContent = row.description;
                    tr.appendChild(descriptionTd);

                    const amountTd = document.createElement('td');
                    amountTd.textContent = row.amount;
                    tr.appendChild(amountTd);

                    const statusTd = document.createElement('td');
                    statusTd.textContent = row.status;
                    tr.appendChild(statusTd);

                    const actionsTd = document.createElement('td');
                    const approveButton = document.createElement('button');
                    approveButton.textContent = 'Approve';
                    approveButton.addEventListener('click', () => {
                        const fid = row.id;
                        let url = '/funding/' + fid + '/approve';

                        fetch(url, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }).then(response => {
                            if (response.ok) {
                                showSweetAlert('Funding application approved.');
                                fetchFundingApplications(token);
                            }
                        }).catch(error => {
                            console.error('There was an error!', error);
                            showSweetAlert('Error approving funding application: ' + error.message);
                        });
                    });
                    actionsTd.appendChild(approveButton);

                    const rejectButton = document.createElement('button');
                    rejectButton.textContent = 'Reject';
                    rejectButton.addEventListener('click', () => {
                        const fid = row.id;
                        let url = '/funding/' + fid + '/reject';

                        fetch(url, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }).then(response => {
                            if (response.ok) {
                                showSweetAlert('Funding application rejected.');
                                fetchFundingApplications(token);
                            }
                        }).catch(error => {
                            console.error('There was an error!', error);
                            showSweetAlert('Error rejecting funding application: ' + error.message);
                        });
                    });
                    actionsTd.appendChild(rejectButton);

                    tr.appendChild(actionsTd);

                    tableBody.appendChild(tr);
                });
            })
            .catch(error => {
                console.error('Error fetching applications:', error);
                const tableBody = document.getElementById('fundingTable').querySelector('tbody');
                tableBody.innerHTML = `<tr><td colspan="5" class="text-center">Failed to load funding applications: ${error.message}</td></tr>`;
            });
    }


    // 使用 SweetAlert 弹窗
    function showSweetAlert(message) {
        Swal.fire({
            title: 'Notification',
            text: message,
            icon: 'info',
            confirmButtonText: 'OK'
        });
    }
</script>
</body>
</html>