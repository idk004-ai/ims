function searchOffers(page) {
    var nameQuery = $('#search').val().trim();
    var statusQuery = $('#status').val().trim();
    var departmentQuery = $('#department').val();


    $.ajax({
        type: 'POST', // Chuyển thành phương thức POST
        url: '/api/v1/offer/search-offer',
        contentType: 'application/json', // Xác định kiểu dữ liệu là JSON
        data: JSON.stringify({ // Gửi dữ liệu dưới dạng JSON
            query: nameQuery,
            status: statusQuery,
            department: parseInt(departmentQuery),
            page: page,
            size: 10
        }),
        success: function (response) {
            updateTable(response.content);
            renderPagination(response.totalPages, response.number);
            updateRowCount(response.content.length, response.totalElements, response.number);
        },
        error: function () {
            console.error("Error fetching Offers");
        }
    })
}


function updateTable(offers) {
    // Xóa nội dung hiện tại của bảng
    $('#OfferTableBody').empty();

    if (offers.length === 0) {
        $('#OfferTableBody').append(`
            <tr>
                <td colspan="7" style="text-align: center; color: gray;">
                    No item matches with your search data. Please try again.
                </td>
            </tr>
        `);
        return;
    }
    // Lặp qua từng ứng viên và tạo một hàng mới cho bảng
    $.each(offers, function (index, offer) {
        // Tạo URL động cho từng liên kết
        var candidateName = escapeHtml(offer.candidateName);
        var email = escapeHtml(offer.email);
        var approver = escapeHtml(offer.approver);
        var department = escapeHtml(offer.department);
        var notes = offer.notes ? escapeHtml(offer.notes) : 'N/A';
        if (notes.length > 30) {
            notes = notes.substring(0, 30) + '...';
        }
        var status = escapeHtml(offer.status);

        var detailUrl = `/api/v1/offer/offer-detail/${offer.interviewId}`;

        var row = '<tr>' +
            '<td>' + candidateName + '</td>' +
            '<td>' + email + '</td>' +
            '<td>' + approver + '</td>' +
            '<td>' + department + '</td>' +
            '<td>' + notes + '</td>' +
            '<td>' + status + '</td>' +
            '<td>' +
            '<a href="' + detailUrl + '" title="View Details" style="color: black;" class="icon-hover">' +
            '<i class="fas fa-eye"></i>' +
            '</a>' +
            '</td>' +
            '</tr>';

        $('#OfferTableBody').append(row);
    });
}


function renderPagination(totalPages, currentPage) {
    let pagination = $('#pagination');
    pagination.empty();

    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchOffers(${currentPage - 1})">
                &laquo;
            </a>
        </li>
    `);

    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
            <a class="page-link" href="#" onclick="searchOffers(0)">1</a>
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
                <a class="page-link" href="#" onclick="searchOffers(${i})">${i + 1}</a>
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
                <a class="page-link" href="#" onclick="searchOffers(${totalPages - 1})">${totalPages}</a>
            </li>
        `);
    }

    pagination.append(`
        <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="searchOffers(${currentPage + 1})">
                &raquo;
            </a>
        </li>
    `);
}

$(document).ready(function () {
    searchOffers(0);
});

function updateRowCount(currentCount, totalCount, page) {
    if(totalCount === 0) {
        $('#rowCount').text('0 Offers');
        return;
    }
    const start = page * 10 + 1;
    const end = start + currentCount - 1;
    $('#rowCount').text(`Showing ${start} to ${end} of ${totalCount} Offers`);
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}