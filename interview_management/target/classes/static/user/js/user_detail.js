$(document).ready(function() {
    const userId = $('#userDetail').data('user-id'); // Retrieve user ID from data attribute
    let userStatus = $('#userDetail').data('user-status'); // Retrieve user status from data attribute

    $("#statusButton").click(function() {
        // Determine the new status based on the current status
        const newStatus = userStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        
        $.ajax({
            url: `/api/v1/user/change-status`,
            type: 'POST',
            data:JSON.stringify( {
                userId: userId,
                status: newStatus
            }),
            success: function(response) {
                // Update button appearance and text based on the new status
                userStatus = newStatus; // Update local status
                $("#statusButton")
                    .removeClass(userStatus === 'ACTIVE' ? 'btn-success' : 'btn-danger')
                    .addClass(userStatus === 'ACTIVE' ? 'btn-danger' : 'btn-success')
                    .text(userStatus === 'ACTIVE' ? 'Deactivate User' : 'Activate User');

                // Update the displayed status text
                $('#userDetail .mb-3:has(label:contains("Status")) p').text(newStatus);

                // Optionally, show a success message or perform other UI updates
            },
            error: function(xhr, status, error) {
                // Handle errors here
                alert("Error updating user status: " + error + " User ID: " + userId);
            }
        });
    });
});
