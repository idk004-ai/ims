function handleOfferAction(id, action, title, content, successMessage) {
    $.confirm({
        title: title,
        content: content,
        buttons: {
            confirm: {
                text: 'Yes',
                btnClass: 'btn-danger',
                action: function () {
                    $.ajax({
                        url: `/api/v1/offer/${action}/${id}`,
                        type: 'POST',
                        success: function (response) {
                            Swal.fire({
                                title: 'Success',
                                text: successMessage || response,
                                icon: 'success',
                                confirmButtonText: 'OK'
                            }).then(() => {
                                location.reload();
                            });
                        },
                        error: function (error) {
                            console.error(error);
                            Swal.fire({
                                title: 'Error',
                                text: 'An error occurred. Please try again.',
                                icon: 'error',
                                confirmButtonText: 'OK'
                            });
                        }
                    });
                }
            },
            cancel: {
                text: 'No',
                btnClass: 'btn-secondary'
            }
        }
    });
}


function rejectOffer(id) {
    handleOfferAction(id, 'reject', 'Confirm Reject', 'Are you sure you want to reject this offer?', 'Offer rejected successfully');
}

function approveOffer(id) {
    handleOfferAction(id, 'approve', 'Confirm Approve', 'Are you sure you want to approve this offer?', 'Offer approved successfully');
}

function cancelOffer(id) {
    handleOfferAction(id, 'cancel', 'Confirm Cancel', 'Are you sure you want to cancel this offer?', 'Offer cancelled successfully');
}

function sentOffer(id) {
    handleOfferAction(id, 'sentOffer', 'Confirm Sent', 'Are you sure you want to mark as sent this offer?', 'Offer marked as sent successfully');
}

function acceptOffer(id) {
    handleOfferAction(id, 'accept', 'Confirm Accept', 'Are you sure you want to accept this offer?', 'Offer accepted successfully');
}

function declinedOffer(id) {
    handleOfferAction(id, 'declined', 'Confirm Decline', 'Are you sure you want to decline this offer?', 'Offer declined successfully');
}