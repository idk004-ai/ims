function searchUsers(page = 0) {
    var nameQuery = $('#search').val().trim() || '';  
    var statusQuery = $('#status').val().trim() || ''; 
    console.log("Name Query: ", nameQuery);
    console.log("Status Query: ", statusQuery);
    console.log("Page: ", page);
    $.ajax({
        type: 'POST',
        url: '/api/v1/user/search',
        contentType: 'application/json', // Set content type to JSON
        data: JSON.stringify({
            query: nameQuery,
            status: statusQuery,
            page: page,
            size: 10
        }),
        success: function(response) {
            updateTable(response.content);
            renderPagination(response.totalPages, response.number);
            updateRowCount(response.content.length, response.totalElements, response.number);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("Error fetching users:", textStatus, errorThrown);
        }
    });
}

$(document).ready(function () {
    searchUsers(0);
});
function updateRowCount(currentCount, totalCount, page) {
    const start = page * 10 + 1;
    const end = start + currentCount - 1;
    $('#rowCount').text(`Showing ${start} to ${end} of ${totalCount} Users`);
}


function updateTable(users) {
    $('#userTableBody').empty();
    if (users.length === 0) {
        $('#userTableBody').append(`
            <tr>
                <td colspan="7" style="text-align: center; color: gray;">
                    No item matches with your search data. Please try again.
                </td>
            </tr>
        `);
        return;
    }
    $.each(users, function(index, user) {
        var viewUrl = '/api/v1/user/user_detail/' + user.id; 
        var row = `
            <tr>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.phoneNo}</td>
                <td>${user.role}</td>
                <td>${user.status}</td>
                <td>
                    <a href="${viewUrl}" title="View Details" style="color: black;">
                        <i class="fas fa-eye"></i>
                    </a>
                </td>
            </tr>`;
        $('#userTableBody').append(row);
    });
}


function renderPagination(totalPages, currentPage) {
    let pagination = $('#pagination');
    pagination.empty();

    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchUsers(${currentPage - 1})">
                &laquo;
            </a>
        </li>
    `);

    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
            <a class="page-link" href="#" onclick="searchUsers(0)">1</a>
        </li>
    `);

    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);
    let startPage, endPage;

    if (totalPages <= maxVisiblePages) {
        startPage = 1;
        endPage = totalPages - 2;
    } else {
        if (currentPage <= halfVisiblePages) {
            startPage = 1;
            endPage = maxVisiblePages - 2;
        } else if (currentPage + halfVisiblePages >= totalPages - 1) {
            startPage = totalPages - maxVisiblePages + 1;
            endPage = totalPages - 2;
        } else {
            startPage = currentPage - halfVisiblePages;
            endPage = currentPage + halfVisiblePages;
        }
    }

    if (startPage > 1) {
        pagination.append(`
            <li class="page-item disabled"><span class="page-link">...</span></li>
        `);
    }

    for (let i = startPage; i <= endPage; i++) {
        pagination.append(`
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="searchUsers(${i})">${i + 1}</a>
            </li>
        `);
    }

    if (endPage < totalPages - 2) {
        pagination.append(`
                <li class="page-item disabled"><span class="page-link">...</span></li>
            `);
    }

    if (totalPages > 1) {
        pagination.append(`
                <li class="page-item ${currentPage === totalPages - 1 ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="searchUsers(${totalPages - 1})">${totalPages}</a>
                </li>
            `);
    }

    pagination.append(`
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="searchUsers(${currentPage + 1})">
                    &raquo;
                </a>
            </li>
        `);
}

$(document).ready(function() {
    let isSearchTriggered = false; 
    $('#search').on('keydown', function(event) {
        if (event.key === 'Enter' && !isSearchTriggered) {
            event.preventDefault();  
            isSearchTriggered = true; 
            $('#searchBtn').click();  
        }
    });
    $('#search').on('input', function() {
        isSearchTriggered = false;
    });
});

$(document).ready(function() {
    searchUsers(0);
});
function updateRowCount(currentCount, totalCount, page) {
    const start = page * 10 + 1;
    const end = start + currentCount - 1;
    $('#rowCount').text(`Showing ${start} to ${end} of ${totalCount} Users`);
}
