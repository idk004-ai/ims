// Format salary input and initial display value
document.addEventListener("DOMContentLoaded", function () {
    // Get the salary element by its ID or class and format it on load
    const salaryElement = document.getElementById("basic-salary");
    if (salaryElement) {
        salaryElement.innerText = formatNumber(salaryElement.innerText);
    }
});

function formatNumber(value) {
    if (!value) return '';
    const parts = value.split(".");
    // Format the integer part with commas
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    // Join integer and decimal parts (if any)
    return parts.join(".");
}

document.addEventListener("DOMContentLoaded", function () {
    const statusMapping = {
        1: "Waiting for approval",
        2: "Approved offer",
        3: "Rejected offer",
        4: "Waiting for response",
        5: "Accepted offer",
        6: "Declined offer",
        7: "Cancelled"
    };

    const statusElement = document.getElementById("status");
    const statusId = statusElement.innerText.trim();

    if (statusMapping[statusId]) {
        statusElement.innerText = statusMapping[statusId];
    }
});

document.getElementById("salary").addEventListener("input", function (event) {
    // Save the current caret (cursor) position
    const cursorPosition = event.target.selectionStart;

    // Remove all non-numeric characters except for the decimal point
    let value = event.target.value.replace(/[^0-9.]/g, '');

    // Format the value with commas
    value = formatNumber(value);

    // Update the input value with the formatted value
    event.target.value = value;
});

// Helper function to format the number with commas
function formatNumber(value) {
    // Ensure the value has at most one decimal point
    const parts = value.split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ","); // Add commas
    return parts.join('.');
}


function hideFailModal(){
    document.getElementById('failModal').style.display = 'none';
}