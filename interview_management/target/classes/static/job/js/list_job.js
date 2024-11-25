let currentPage = 0;
const roleId = $('#roleId').val();

function searchjobs(page) {
    // Get the values from the search input and status dropdown
    var nameQuery = $('#search').val().trim();
    var statusQuery = $('#status').val().trim();

    // Create the request object to send to the server
    var requestData = {
        query: nameQuery,  // Search keyword for job's name
        status: statusQuery,  // job's status filter
        page: page,  // The page number to retrieve
        pageSize: 10  // Number of results per page
    };

    // Send an AJAX request to the search API
    $.ajax({
        type: 'POST',  // HTTP method to use
        url: '/api/v1/jobs/search',  // API endpoint to call
        contentType: 'application/json',  // Request content type as JSON
        data: JSON.stringify(requestData),  // Convert requestData to JSON string
        success: function (response) {
            // On success, update the table with the response content
            updateTable(response.content);
            renderPagination(response.totalPages, response.number);
            updateRowCount(response.content.length, response.totalElements, response.number);
            currentPage = page;
        },
        error: function () {
            // Log an error message if the request fails
            console.error("Error fetching jobs");
        }
    });
}

function updateTable(jobs) {
    // Clear the existing table body
    $('#jobTableBody').empty();

    // If no candidates are found, display a message in the table
    if (jobs.length === 0) {
        $('#jobTableBody').append(`
            <tr>
                <td colspan="7" style="text-align: center; color: gray;">
                    No candidates found.
                </td>
            </tr>
        `);
        return;
    }

    // Iterate over each job and generate a new row for the table
    $.each(jobs, function (index, job) {
        // Template literal for the job row
        var deleteUrl = `/api/v1/jobs/delete-job/${job.jobId}`;

        var row = `
            <tr>
                <td>${escapeHtml(job.title)}</td>
                <td>${escapeHtml(job.skills)}</td>
                <td>${escapeHtml(job.startDate)}</td>
                <td>${escapeHtml(job.endDate)}</td>
                <td>${escapeHtml(job.level)}</td>
                <td>${escapeHtml(job.status)}</td>
                <td>
                    <a href="/api/v1/jobs/detail-job/${job.jobId}" 
                       title="View Details" style="color: black;">
                       <i class="fas fa-eye"></i>
                    </a>
                    ${roleId != 4 ? `&nbsp;
                    <a href="/api/v1/jobs/edit-job/${job.jobId}" 
                       title="Edit Job" style="color: black;">
                       <i class="fas fa-edit"></i>
                    </a>
                    &nbsp;
                    <a href="javascript:void(0)"
                       title="Delete" style="color: red;" onclick="showDeleteModal('${deleteUrl}')">
                       <i class="fas fa-trash-alt"></i>
                    </a>`: ``}
                </td>
            </tr>`;
        // Append the row to the table body
        $('#jobTableBody').append(row);
    });
}

function renderPagination(totalPages, currentPage) {
    let pagination = $('#pagination');
    pagination.empty(); // Clear the existing pagination

    // Add the previous page button
    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchjobs(${currentPage - 1})">&laquo;</a>
        </li>
    `);

    // Add the first page button
    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
            <a class="page-link" href="#" onclick="searchjobs(0)">1</a>
        </li>
    `);

    const maxVisiblePages = 5;  // Maximum number of pages to display
    const halfVisiblePages = Math.floor(maxVisiblePages / 2); // Half of the visible pages for balanced display
    let startPage, endPage;

    // Determine the start and end pages for pagination
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

    // Add ellipsis if there are hidden pages at the beginning
    if (startPage > 1) {
        pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
    }

    // Add the visible page buttons
    for (let i = startPage; i <= endPage; i++) {
        pagination.append(`
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="searchjobs(${i})">${i + 1}</a>
            </li>
        `);
    }

    // Add ellipsis if there are hidden pages at the end
    if (endPage < totalPages - 2) {
        pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
    }

    // Add the last page button if there is more than one page
    if (totalPages > 1) {
        pagination.append(`
            <li class="page-item ${currentPage === totalPages - 1 ? 'active' : ''}">
                <a class="page-link" href="#" onclick="searchjobs(${totalPages - 1})">${totalPages}</a>
            </li>
        `);
    }

    // Add the next page button
    pagination.append(`
        <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchjobs(${currentPage + 1})">&raquo;</a>
        </li>
    `);
}

function updateRowCount(currentCount, totalCount, page) {
    // Update the row count summary text if there are no candidates
    if(totalCount === 0) {
        $('#rowCount').text('0 Candidates');
        return;
    }
    const start = page * 10 + 1;  // Calculate the start index for the current page
    const end = start + currentCount - 1;  // Calculate the end index
    $('#rowCount').text(`Showing ${start} to ${end} of ${totalCount} jobs`);
}

/**
 * Initializes the search by loading the first page when the document is ready.
 */
$(document).ready(function () {
    searchjobs(0);  // Load the first page of jobs on page load
});


/**
 * Show Delete Modal
 *
 * @param {string} url
 */
    let deleteUrl = '';

function showDeleteModal(url) {
    deleteUrl = url;
    document.getElementById('deleteModal').style.display = 'flex';
}

/**
 * Hide Delete Modal
 *
 */
function hideDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
}


/**
 * Delete Candidate
 *
 */
function deleteJob() {
    $.ajax({
        type: 'DELETE',
        url: deleteUrl,
        success: function () {
            //Hide Modal after success
            hideDeleteModal();
    console.log("oke")
            //Call search function to keep result search and page after delete.
            searchjobs(currentPage);
        },
        error: function () {
            console.error("Error deleting job");
            hideDeleteModal();
        }
    });
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
