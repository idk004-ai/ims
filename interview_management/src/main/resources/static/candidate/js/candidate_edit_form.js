/**
 * Display error messages on the form
 *
 * @param errors - An array of error objects, where each object contains information about
 *                 the field with an error and the corresponding error message.
 */
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
            field.addEventListener("input", function() {
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
    const id = document.getElementById("candidateId").value;
    const fullName = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const dob = document.getElementById("dob").value;
    const address = document.getElementById("address").value;
    const phoneNumber = document.getElementById("phoneNumber").value;
    const gender = document.getElementById("gender").value;
    const cvLink = document.getElementById("cvLink").value;
    const cvName = document.getElementById("cvName").value;
    const position = document.getElementById("position").value;
    const skills = Array.from(document.getElementById("skill").selectedOptions).map(option => option.value);
    const status = document.getElementById("status").value;
    const yoe = document.getElementById("yoe").value;
    const highestLevel = document.getElementById("highestLevel").value;
    const recruiter = document.getElementById("recruiterId").value;
    const note = document.getElementById("note").value;

    const formData = {
        id: id,
        fullName: fullName,
        email: email,
        dob: dob,
        address: address,
        phoneNumber: phoneNumber,
        gender: gender,
        cvLink: cvLink,
        cvFileName: cvName,
        position: position,
        skills: skills,
        status: status,
        yoe: yoe,
        highestLevel: highestLevel,
        recruiter: recruiter,
        note: note
    }
    console.log(formData);

    fetch('/api/v1/candidates/edit_candidate', {
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
            sessionStorage.removeItem("nameQuery");
            sessionStorage.removeItem("statusQuery");
            sessionStorage.removeItem("currentPage");
            document.getElementById("successModal").style.display = "block";
        }
    }).catch(err => {
        console.error('Error:', err);
        document.getElementById("failModal").style.display = "block";
    });

}