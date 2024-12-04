function displayErrors(errors) {
    // Xóa các thông báo lỗi hiện có
    clearErrors();

    errors.forEach(error => {
        const field = document.getElementById(error.field);

        if (field) {
            const errorMessageElement = field.nextElementSibling;

            if (errorMessageElement && errorMessageElement.tagName === 'P' && errorMessageElement.classList.contains('error')) {
                errorMessageElement.innerText = error.defaultMessage;
            } else {
                const newErrorMessage = document.createElement("p");
                newErrorMessage.className = "error text-danger";
                newErrorMessage.innerText = error.defaultMessage;
                field.insertAdjacentElement("afterend", newErrorMessage);
            }

            // Thêm sự kiện 'input' để xóa thông báo lỗi khi người dùng bắt đầu nhập
            field.addEventListener("input", function () {
                if (field.nextElementSibling && field.nextElementSibling.classList.contains("error")) {
                    field.nextElementSibling.remove();
                }
            }, { once: true }); // Sự kiện chỉ kích hoạt một lần cho đến khi lỗi mới xuất hiện
        }
    });
}


function clearErrors() {
    document.querySelectorAll(".error").forEach(el => el.remove());
}

function submitForm(event) {
    event.preventDefault();
    const interviewId = document.getElementById("interviewSelect").value;
    const contractType = document.getElementById("contractType").value;
    const department = document.getElementById("department").value;
    const periodfrom = document.getElementById("periodfrom").value;
    const periodto = document.getElementById("periodto").value;
    const duedate = document.getElementById("duedate").value;
    const offerNote = document.getElementById("OfferNote").value;
    const salary = parseFloat(document.getElementById("salary").value.replace(/,/g, ''));
    const approver = document.getElementById("approver").value;

    const formData = {
        interviewId: interviewId,
        contractType: contractType,
        department: department,
        periodfrom: periodfrom,
        periodto: periodto,
        duedate: duedate,
        offerNote: offerNote,
        salary: salary,
        approver: approver
    };
    console.log(formData);

    fetch('/api/v1/offer/offer_create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errors => {
                displayErrors(errors);
                document.getElementById("failModal").style.display = "block";
            });
        } else {
            document.getElementById("successModal").style.display = "block";
        }
    }).catch(err => {
        console.error('Error:', err);
        alert("An unexpected error occurred. Please try again.");
    });
}

function submitEditForm(event) {
    event.preventDefault();
    const interviewId = document.getElementById("interviewId").value;
    const contractType = document.getElementById("contractType").value;
    const department = document.getElementById("department").value;
    const periodfrom = document.getElementById("periodfrom").value;
    const periodto = document.getElementById("periodto").value;
    const duedate = document.getElementById("duedate").value;
    const offerNote = document.getElementById("OfferNote").value;
    const salary = parseFloat(document.getElementById("salary").value.replace(/,/g, ''));
    const approver = document.getElementById("approver").value;
    const interviewinfo = document.getElementById("interviewinfo").value;

    const formData = {
        interviewId: interviewId,
        contractType: contractType,
        department: department,
        periodfrom: periodfrom,
        periodto: periodto,
        duedate: duedate,
        offerNote: offerNote,
        salary: salary,
        approver: approver,
        interviewinfo: interviewinfo
    };
    console.log(formData);

    fetch('/api/v1/offer/offer_edit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errors => {
                displayErrors(errors);
                document.getElementById("failModal").style.display = "block";
            });
        } else {
            document.getElementById("successModal").style.display = "block";
        }
    }).catch(err => {
        console.error('Error:', err);
        alert("An unexpected error occurred. Please try again.");
    });
}

function approveOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/approve/' + interviewId,
        type: 'POST',
        success: function (response) {
            alert('Offer approved successfully');
            location.reload();
        },
        error: function (xhr, status, error) {
            alert('Failed to approve offer');
        }
    });
}

function rejectOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/reject/' + interviewId,
        type: 'POST',
        success: function (response) {
            location.reload();
            alert('Offer approved successfully');
        },
        error: function (xhr, status, error) {
            alert('Failed to reject offer');
        }
    });
}

function sentOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/sentOffer/' + interviewId,
        type: 'POST',
        success: function (response) {
            alert('Offer sent to Candidate successfully');
            location.reload();
        },
        error: function (xhr, status, error) {
            alert('Failed to sent offer');
        }
    });
}

function acceptOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/accept/' + interviewId,
        type: 'POST',
        success: function (response) {
            alert('Offer accepted successfully');
            location.reload();
        },
        error: function (xhr, status, error) {
            alert('Failed to accept offer');
        }
    });
}

function declinedOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/declined/' + interviewId,
        type: 'POST',
        success: function (response) {
            alert('Offer declined successfully');
            location.reload();
        },
        error: function (xhr, status, error) {
            alert('Failed to declin offer');
        }
    });
}

function cancelOffer(interviewId) {
    $.ajax({
        url: '/api/v1/offer/cancel/' + interviewId,
        type: 'POST',
        success: function (response) {
            alert('Offer canceled successfully');
            location.reload();
        },
        error: function (xhr, status, error) {
            alert('Failed to cancel offer');
        }
    });
}




