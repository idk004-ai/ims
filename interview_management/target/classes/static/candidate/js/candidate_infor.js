function showBanModal() {
    document.getElementById('banModal').style.display = 'flex';
}
function hideBanModal() {
    document.getElementById('banModal').style.display = 'none';
}
function banCandidate() {
    const id = $('#candidateId').val();
    $.ajax({
        type: 'DELETE',
        url: "/api/v1/candidates/ban/" + id,
        success: function () {
            hideBanModal();
            window.location.reload();
        },
        error: function () {
            console.error("Error banning candidate");
            hideBanModal();
            window.location.reload();
        }
    });
}

function showUnbanModal() {
    document.getElementById('unbanModal').style.display = 'flex';
}
function hideUnbanModal() {
    document.getElementById('unbanModal').style.display = 'none';
}
function unbanCandidate() {
    const id = $('#candidateId').val();
    console.log("Unban Candidate ID: ", id);
    $.ajax({
        type: 'DELETE',
        url: "/api/v1/candidates/unban/" + id,
        success: function () {
            console.log("Unbanned candidate");
            hideUnbanModal();
            window.location.reload();
        },
        error: function (xhr) {
            console.error("Error unbanning candidate", xhr.responseText);
            hideUnbanModal();
            window.location.reload();
        }
    });
}

