function showExportModal() {
    document.getElementById("exportModal").style.display = "block";
}
function submitExport() {
    const periodFrom = document.getElementById("periodFrom").value;
    const periodTo = document.getElementById("periodTo").value;

    if (!periodFrom || !periodTo) {
        document.getElementById("error-message").innerText = "Required fields";
        return;
    }
    const url = `/api/v1/offer/export_offer?periodFrom=${periodFrom}&periodTo=${periodTo}`;

    fetch(url)
        .then(response => {
            if (response.ok) {
                clearInput();
                window.open(url, "_blank");
            } else {
                return response.text().then(error => {
                    document.getElementById("error-message").innerText = error;
                });
            }
        })
        .catch(error => {
            document.getElementById("error-message").innerText = "Error: " + error;
        });
}
function hideExportModal() {
    document.getElementById("exportModal").style.display = "none";
    clearError();
    clearInput();
}
function clearError() {
    document.getElementById("error-message").innerText = "";
}
function clearInput() {
    document.getElementById("periodFrom").value = "";
    document.getElementById("periodTo").value = "";
}