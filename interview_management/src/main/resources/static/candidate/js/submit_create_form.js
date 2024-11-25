/**
 * Display error messages on the form
 *
 * @param errors - An array of error objects, where each object contains information about
 *                 the field with an error and the corresponding error message.
 */
function displayErrors(errors) {
    clearErrors();

    errors.forEach(error => {
        // Get field in html similar to error field
        const field = document.getElementById(error.field);

        // If f
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

            field.addEventListener("input", function() {
                if (field.nextElementSibling && field.nextElementSibling.classList.contains("error")) {
                    field.nextElementSibling.remove();
                }
            }, { once: true });
        }
    });
}

/**
 * clear errors
 */
function clearErrors() {
    document.querySelectorAll(".error").forEach(el => el.remove());
}

/**
 * submit create form
 *
 * @param event
 */
function submitForm(event) {

    // Prevent default action
    event.preventDefault();

    // Get element by id
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

    // Create form dada
    const formData = {
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

    // Send create request
    fetch('/api/v1/candidates/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errors => {
                // Display errors if errors
                displayErrors(errors);
                document.getElementById("failModal").style.display = "block";
            });
        } else {
            document.getElementById("successModal").style.display = "block";
        }
    }).catch(err => {
        console.error('Error:', err);
        alert("An error occurred. Please try again later.");
    });

}