$(document).ready(function () {
    $('#interviewSelect').on('change', function () {
        const interviewId = $(this).val(); // Get the selected interview ID

        if (interviewId) {
            $.ajax({
                url: '/api/v1/offer/get-offer-info', // API endpoint
                type: 'GET', // HTTP method
                data: { id: interviewId }, // Send selected interview ID
                success: function (response) {
                    // Directly access the response properties, as it's already the DTO object
                    $('#candidate-name').val(response.candidateName || 'N/A');
                    $('#position').val(response.position || 'N/A');
                    $('#interview-notes').val(response.interviewNote || 'N/A');
                    $('#level').val(response.level || 'N/A');
                    $('#recruiter').val(response.recruiter || 'N/A');
                    $('#interviewId').val(interviewId); // Set the interview ID in the hidden input field
                },
                error: function (xhr, status, error) {
                    console.error('Error:', error);
                    alert('Failed to retrieve interview information.');
                }
            });
        } else {
            // Reset fields if nothing is selected
            $('#candidate-name, #position, #level, #recruiter','#interview-notes').val(''); // #approve,
            $('#interviewId').val(''); // Reset the hidden input value
        }
    });
});

function enableFields() {
    var interviewSelect = document.getElementById("interviewSelect");
    var approverSelect = document.getElementById("approver");
    var contractTypeSelect = document.getElementById("contractType");
    var departmentSelect = document.getElementById("department");
    var periodFromInput = document.getElementById("periodfrom");
    var periodToInput = document.getElementById("periodto");
    var dueDateInput = document.getElementById("duedate");
    var salaryInput = document.getElementById("salary");
    var offerNoteInput = document.getElementById("OfferNote");
    
    // Enable the fields only if interview info is selected
    if (interviewSelect.value !== "") {
        approverSelect.disabled = false;
        contractTypeSelect.disabled = false;
        departmentSelect.disabled = false;
        periodFromInput.disabled = false;
        periodToInput.disabled = false;
        dueDateInput.disabled = false;
        salaryInput.disabled = false;
        offerNoteInput.disabled = false;
    } else {
        // If no interview info is selected, disable the fields
        approverSelect.disabled = true;
        contractTypeSelect.disabled = true;
        departmentSelect.disabled = true;
        periodFromInput.disabled = true;
        periodToInput.disabled = true;
        dueDateInput.disabled = true;
        salaryInput.disabled = true;
        offerNoteInput.disabled = true;
    }
}