//define global currentPage.
let currentPage=0;

/**
 * Sends a request to search for candidates and updates the UI with the results.
 *
 * @param {number} page - The current page number to retrieve data for.
 * @returns {void} - This function does not return anything; it updates the table and pagination.
 */
function searchCandidates(page) {
    // Get the values from the search input and status dropdown
    var nameQuery = $('#search').val().trim();
    var statusQuery = parseInt($('#status').val(), 10);
    statusQuery = isNaN(statusQuery) ? null : statusQuery;

    console.log(statusQuery);

    // Create the request object to send to the server
    var requestData = {
        query: nameQuery,  // Search keyword for candidate's name
        status: statusQuery,  // Candidate's status filter
        page: page || 0,  // The page number to retrieve
    };
    console.log("Request Data:", requestData);
    console.log(currentPage);


    // Send an AJAX request to the search API
    $.ajax({
        type: 'POST',  // HTTP method to use
        url: '/api/v1/candidates/search',  // API endpoint to call
        contentType: 'application/json',  // Request content type as JSON
        data: JSON.stringify(requestData),  // Convert requestData to JSON string
        success: function (response) {
            // On success, update the table with the response content
            updateTable(response.content);
            renderPagination(response.totalPages, response.number);
            updateRowCount(response.content.length, response.totalElements, response.number);
            currentPage=page;
        },
        error: function () {
            // Log an error message if the request fails
            console.error("Error fetching candidates");
        }
    });
}


/**
 * Updates the candidates table with new data.
 *
 * @param {Array} candidates - Array of candidate objects to be displayed in the table.
 */
function updateTable(candidates) {
    // Clear the table body before populating new data
    $('#candidateTableBody').empty();
    const roleId = $('#roleId').val();

    // If no candidates are found, display a message in the table
    if (candidates.length === 0) {
        $('#candidateTableBody').append(`
            <tr>
                <td colspan="7" style="text-align: center; color: gray;">
                    No candidates found.
                </td>
            </tr>
        `);
        return;
    }

    // Iterate over each candidate and append them to the table
    $.each(candidates, function (index, candidate) {
        // Define the delete URL for the candidate
        var deleteUrl = `/api/v1/candidates/delete_candidate/${candidate.id}`;

        // Escape HTML to prevent XSS
        var fullName = escapeHtml(candidate.fullName);
        var email = escapeHtml(candidate.email);
        var phone = escapeHtml(candidate.phone);
        var currentPosition = escapeHtml(candidate.currentPosition);
        var ownerHR = escapeHtml(candidate.ownerHR);
        var status = escapeHtml(candidate.status);

        // Create a new row with the candidate data
        var row = `
            <tr>
                <td>${fullName}</td>
                <td>${email}</td>
                <td>${phone}</td>
                <td>${currentPosition}</td>
                <td>${ownerHR}</td>
                <td>${status}</td>
                <td>
                    <a href="javascript:void(0);" title="View Details" style="color: black;" class="icon-hover" onclick="goToDetail('${candidate.id}')">
                       <i class="fas fa-eye"></i>
                    </a>
                    &nbsp;&nbsp;
                    ${candidate.status === 'Open' && roleId != 4 ?
            `<a href="javascript:void(0);" title="Delete" style="color: red;" class="icon-hover" onclick="showDeleteModal('${deleteUrl}')">
                        <i class="fas fa-trash-alt"></i>
                    </a>` : ''}
                </td>
            </tr>`;

        // Append the row to the table body
        $('#candidateTableBody').append(row);
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

/**
 * Go to Candidate Detail Page
 *
 * @param candidateId - The id of the candidate to view details for.
 */
function goToDetail(candidateId) {

    // Save search query and current page to session storage
    sessionStorage.setItem("nameQuery", $('#search').val().trim());
    sessionStorage.setItem("statusQuery", $('#status').val().trim());
    sessionStorage.setItem("currentPage", parseInt(currentPage, 10) || 0);

    // Redirect to candidate detail page
    window.location.href = `/api/v1/candidates/candidate_detail/${candidateId}`;
}
$(document).ready(function () {

    // Load saved search query and status query from session storage
    var savedNameQuery = sessionStorage.getItem("nameQuery") || "";
    var savedStatusQuery = sessionStorage.getItem("statusQuery") || "";
    var savedPage = sessionStorage.getItem("currentPage") || 0;
    var statusQuery = savedStatusQuery ? parseInt(savedStatusQuery, 10) : null;

    // Set search query and status query to input fields
    $('#search').val(savedNameQuery);
    $('#status').val(statusQuery);

    // Call search function to keep result search and page after reload page.
    searchCandidates(savedPage);

    // Clear session storage
    sessionStorage.removeItem("nameQuery");
    sessionStorage.removeItem("statusQuery");
    sessionStorage.removeItem("currentPage");
});

//define global deleteUrl
let deleteUrl = '';

/**
 * Show Delete Modal
 *
 * @param {string} url
 */
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
function deleteCandidate() {
    $.ajax({
        type: 'DELETE',
        url: deleteUrl,
        success: function () {
            //Hide Modal after success
            hideDeleteModal();

            //Call search function to keep result search and page after delete.
            searchCandidates(currentPage);
        },
        error: function () {
            console.error("Error deleting candidate");
            hideDeleteModal();
        }
    });
}

/**
 * Renders the pagination controls based on the total pages and the current page.
 *
 * @param {number} totalPages - Total number of pages available.
 * @param {number} currentPage - The current active page.
 */
function renderPagination(totalPages, currentPage) {
    let pagination = $('#pagination');
    pagination.empty(); // Clear the existing pagination

    // Add the previous page button
    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchCandidates(${currentPage - 1})">&laquo;</a>
        </li>
    `);

    // Add the first page button
    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
            <a class="page-link" href="#" onclick="searchCandidates(0)">1</a>
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
                <a class="page-link" href="#" onclick="searchCandidates(${i})">${i + 1}</a>
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
                <a class="page-link" href="#" onclick="searchCandidates(${totalPages - 1})">${totalPages}</a>
            </li>
        `);
    }

    // Add the next page button
    pagination.append(`
        <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchCandidates(${currentPage + 1})">&raquo;</a>
        </li>
    `);
}

/**
 * Updates the row count summary at the bottom of the table.
 *
 * @param {number} currentCount - The number of candidates on the current page.
 * @param {number} totalCount - The total number of candidates.
 * @param {number} page - The current page number.
 */
function updateRowCount(currentCount, totalCount, page) {
    // Update the row count summary text if there are no candidates
    if(totalCount === 0) {
        $('#rowCount').text('0 Candidates');
        return;
    }
    const start = page * 10 + 1;  // Calculate the start index for the current page
    const end = start + currentCount - 1;  // Calculate the end index
    $('#rowCount').text(`Showing ${start} to ${end} of ${totalCount} candidates`);
}
