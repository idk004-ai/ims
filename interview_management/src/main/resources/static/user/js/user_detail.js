$(document).ready(function() {
    const userId = $('#userDetail').data('user-id'); 
    let userStatus = $('#userDetail').data('user-status'); 
    $("#statusButton").click(function() {
        const newStatus = userStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        $.ajax({
            url: `/api/v1/user/change-status`,
            type: 'POST',
            data:JSON.stringify( {
                userId: userId,
                status: newStatus
            }),
            success: function(response) {
                userStatus = newStatus; 
                $("#statusButton")
                    .removeClass(userStatus === 'ACTIVE' ? 'btn-success' : 'btn-danger')
                    .addClass(userStatus === 'ACTIVE' ? 'btn-danger' : 'btn-success')
                    .text(userStatus === 'ACTIVE' ? 'Deactivate User' : 'Activate User');
                $('#userDetail .mb-3:has(label:contains("Status")) p').text(newStatus);
            },
            error: function(xhr, status, error) {
                alert("Error updating user status: " + error + " User ID: " + userId);
            }
        });
    });
});
