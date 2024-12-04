// Lấy các phần tử
const openPopupButton = document.getElementById('import-job');
const filePopup = document.getElementById('filePopup');
const cancelButton = document.getElementById('cancelButton-import');
const sendButton = document.getElementById('sendButton-import');

// Mở popup khi nhấn nút "Open Popup"
openPopupButton.addEventListener('click', () => {
    filePopup.style.display = 'flex';
});

// Đóng popup khi nhấn nút "Hủy"
cancelButton.addEventListener('click', () => {
    filePopup.style.display = 'none';
});

// Xử lý nút "Gửi"
sendButton.addEventListener('click', () => {
    console.log('send');
    filePopup.style.display = 'none';
});

const uploadExcelFile = () => {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    const formData = new FormData();
    formData.append("file", file);
    clearInput();

    fetch("/api/v1/jobs/upload-excel", {
        method: "POST",
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            console.log(result);
            if (result.code === 200) {
                document.getElementById("successModal").style.display = "block";
            } else {
                if (result.code === 422) {
                    console.log("list loi")
                    showErrorModal(result.result, result.code);
                } else {
                    console.log("1 loi")
                    showErrorModal(result.message, result.code)
                }
            }
            console.log("Server response:", result);
        })
        .catch(error => {
            console.error("Error:", error);
        });
};

function hideFailModal() {
    document.getElementById('failModal').style.display = 'none';
}

function showErrorModal(errors, code) {
    const errorModal = document.getElementById("errorModal-fail");
    const errorList = document.getElementById("errorList-fail");
    const error = document.getElementById("error-file");
    errorModal.style.display = "flex";
    errorList.innerHTML = "";
    if (code !== 422) {
        errorList.style.display = "block";
        errorList.textContent = errors;
        error.style.display = "none";
    } else {
        error.style.display = "block";
        errorList.style.display = "none";
    }
}

function formatString(input) {
    return input
        .replace(/([A-Z])/g, ' $1')
        .trim()
        .replace(/^./, (str) => str.toUpperCase());
}

function closeErrorModal() {
    document.getElementById("errorModal-fail").style.display = "none";
}

function exportErrors() {
    const url = `/api/v1/jobs/export-errors`;

    fetch(url)
        .then(response => {
            if (response.ok) {
                window.open(url, "_blank");
            } else {
                return response.text().then(error => {
                });
            }
        })
        .catch(error => {
            alert("doe co")
        });
}

function clearInput() {
    document.getElementById("fileInput").value = "";
}