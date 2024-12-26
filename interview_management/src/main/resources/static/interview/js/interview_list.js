
async function updateInterviewTable(interviews) {
    const tableBody = document.querySelector('table tbody');
    tableBody.innerHTML = ''; // Clear existing rows
    // get the current user
    const userData = await waitForUserData();
    var role = 0;
    if (userData && userData.role) {
        role = parseInt(userData.role);
    }
    var handleTitle = (role === 4) ? 'Submit' : 'Edit';

    if (interviews.length === 0) {
        $('#InterviewTableBody').append(`
            <tr>
                <td colspan="7" style="text-align: center; color: gray;">
                    No item matches with your search data. Please try again.
                </td>
            </tr>
        `);
        return;
    }

    interviews.forEach(interview => {
        const row = `
               <tr id=${interview.interviewId} class="interview-row" data-interview-id="${interview.interviewId}">
                    <td class="clickable">${interview.title}</td>
                    <td class="clickable">${interview.candidate}</td>
                    <td class="clickable">${interview.interviewer}</td>
                    <td class="clickable">${interview.time}</td>
                    <td class="clickable">${!interview.result ? 'N/A' : interview.result}</td>
                    <td class="clickable">${interview.statusInterview}</td>
                    <td class="clickable">${interview.job}</td>
                    <td class="action-icons">
                        <a href="/api/v1/interview/view/${interview.interviewId}" style="text-decoration: none;">
                            <i class="fas fa-eye" style="color:black" title="View"></i>
                        </a>
                        ${(interview.statusInterview === 'Closed' || interview.statusInterview === 'Cancelled')
                ? ''
                : `<a href="/api/v1/interview/${role === 4 ? 'submit' : 'edit'}/${interview.interviewId}">
                                    <i class="fas fa-edit" style="color:black" title="${handleTitle}"></i>
                                </a>`
            }
                    </td>
               </tr>
          `;
        tableBody.innerHTML += row;
    });

    // Add click event listener to all clickable cells
    const clickableCells = document.querySelectorAll('.clickable');
    clickableCells.forEach(cell => {
        cell.addEventListener('click', function () {
            const interviewId = this.parentElement.getAttribute('data-interview-id');
            window.location.href = `/api/v1/interview/view/${interviewId}`;
        });
    });
}

/**
 * Load the interview list page
 * @param {number} pageNumber 
 * @param {number} status 
 * @param {number} interviewer 
 */
function loadPage(
    pageNumber = 0
) {
    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get('interviewStatus');
    const interviewer = urlParams.get('interviewer');
    const searchQuery = urlParams.get('query');
    const payload = {
        page: pageNumber,
        pageSize: 10,
        statusId: status ? parseInt(status) : null,
        interviewerId: interviewer ? parseInt(interviewer) : null,
        query: searchQuery
    }
    initApi().then(api => {
        api.post(`/interview`, payload)
            .then((data) => {
                updateInterviewTable(data.content);
                renderPagination(data.totalPages, data.number);
            }).catch(error => {
                console.error(error);
            });
    });
}

/**
 * Render dropdown menu and data from session storage
 */

document.addEventListener('DOMContentLoaded', async function () {
    fillOutDropDownMenu('/master/interview-status', 'status-filter', 'interviewStatus', (option, record) => {
        option.value = record.categoryId;
        option.textContent = record.categoryValue;
    });
    fillOutDropDownMenu(`/user/user-role/${4}`, 'interviewer-list', 'interviewer', (option, record) => {
        option.value = record.id;
        option.textContent = record.fullname;
    });

    // get the current user
    const userData = await waitForUserData();
    if (userData && userData.role) {
        const role = parseInt(userData.role);
        if (role === 4) {
            document.getElementById('add-btn').hidden = true;
        }
    }

    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');

    const urlParams = new URLSearchParams(window.location.search);
    // Get the query parameter from the URL and set it to the search input
    const searchQuery = urlParams.get('query');
    searchInput.value = searchQuery;
    loadPage(0);

    searchInput.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            const searchValue = searchInput.value.trim();
            urlParams.set('query', searchValue);
            window.history.pushState({}, '', `${window.location.pathname}?${urlParams.toString()}`);
            loadPage(0);
        }
    });

    searchButton.addEventListener('click', function () {
        const searchValue = searchInput.value.trim();
        urlParams.set('query', searchValue);
        window.history.pushState({}, '', `${window.location.pathname}?${urlParams.toString()}`);
        loadPage(0);
    });
});

/**
 * render pagination
 * @param {number} totalPages 
 * @param {number} currentPage 
 */

function renderPagination(totalPages, currentPage) {
    let pagination = $('#pagination');
    pagination.empty(); // Clear the existing pagination

    // Add the previous page button
    pagination.append(`
         <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
             <a class="page-link" href="#" onclick="loadPage(${currentPage - 1})">&laquo;</a>
         </li>
     `);

    // Add the first page button
    pagination.append(`
         <li class="page-item ${currentPage === 0 ? 'active' : ''}">
             <a class="page-link" href="#" onclick="loadPage(0)">1</a>
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
                 <a class="page-link" href="#" onclick="loadPage(${i})">${i + 1}</a>
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
                 <a class="page-link" href="#" onclick="loadPage(${totalPages - 1})">${totalPages}</a>
             </li>
         `);
    }

    // Add the next page button
    pagination.append(`
         <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
             <a class="page-link" href="#" onclick="loadPage(${currentPage + 1})">&raquo;</a>
         </li>
     `);
}
