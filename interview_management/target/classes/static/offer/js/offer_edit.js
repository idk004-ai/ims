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