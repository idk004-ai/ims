$(document).ready(function() {
    $('#confirmationPopup').hide();

    $('input, select').on('input change', function() {
        clearError($(this).attr('name'));
    });

    $('#submitBtn').click(function() {
        const formData = gatherFormData();
        const errors = validateFormData(formData);

        if (Object.keys(errors).length > 0) {
            displayErrors(errors);
        } else {
            showConfirmationPopup(); 
            $("#confirmationPopup").css("display", "flex"); 
        }
    });

    $('#confirmYes').click(function() {
        const formData = gatherFormData();
        submitForm(formData);
        hideConfirmationPopup();
    });

    $('#confirmNo').click(function() {
        hideConfirmationPopup(); 
    });

    function showConfirmationPopup() {
        $('#confirmationPopup').fadeIn(); 
    }

    function hideConfirmationPopup() {
        $('#confirmationPopup').fadeOut(); 
    }

    function gatherFormData() {
        return {
            fullname: $('input[name="fullname"]').val().trim(),
            email: $('input[name="email"]').val().trim(),
            dob: $('input[name="dob"]').val(),
            phoneNo: $('input[name="phoneNo"]').val().trim(),
            role: $('select[name="role"]').val(),
            status: $('select[name="status"]').val(),
            address: $('input[name="address"]').val().trim(),
            gender: $('select[name="gender"]').val(),
            department: $('select[name="department"]').val(),
            note: $('input[name="note"]').val().trim()
        };
    }

    function validateFormData(data) {
        const errors = {};
        if (!data.fullname) errors.fullname = "Required Field";
        if (!data.email) {
            errors.email = "Required Field";
        } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/.test(data.email)) {
            errors.email = "Invalid email format";
        }
        if (!data.dob) errors.dob = "Required Field";
        else if (new Date(data.dob) > new Date()) errors.dob = "Date of Birth must be in the past";
        if (!data.phoneNo) errors.phoneNo = "Required Field";
        else if (!/^(0[1-9][0-9]{7,13}|\+84[1-9][0-9]{0,13})$/.test(data.phoneNo)) errors.phoneNo = "Invalid phone number format";
        if (!data.role) errors.role = "Required Field";
        if (!data.status) errors.status = "Required Field";
        if (!data.address) errors.address = "Required Field";
        if (!data.gender) errors.gender = "Required Field";
        if (!data.department) errors.department = "Required Field";
        return errors;
    }

    function displayErrors(errors) {
        $(".text-danger").hide().empty();
        for (let key in errors) {
            const errorElement = $(`#${key}Error`);
            if (errorElement.length > 0) {
                errorElement.text(errors[key]).fadeIn();
            } else {
                console.warn(`Error element for '${key}' not found. Ensure #${key}Error exists.`);
            }
        }
    }

    function clearError(fieldName) {
        $(`#${fieldName}Error`).hide().empty();
    }

    function showSuccessPopup() {
        $('#successPopup').fadeIn();
        setTimeout(function() {
            window.location.href = "/api/v1/user/get-all-user";
        }, 2000);
    }

    function showFailPopup() {
        $('#failPopup').html(`
            <div class="popup-content" style="background: #fff; padding: 20px; border-radius: 5px; max-width: 300px; margin: 50px auto; text-align: center;">
                <h4 class="text-danger" style="margin: 0; font-size: 18px;">Failed to create user</h4>
            </div>
        `);
        $('#failPopup').fadeIn();
    }

    function hideFailPopup() {
        $('#failPopup').fadeOut();
    }

    function submitForm(data) {
        $.ajax({
            url: '/api/v1/user/add-user',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                if (response.success) {
                    showSuccessPopup();
                } else {
                    displayErrors(response.errors);
                }
            },
            error: function(xhr) {
                const errors = xhr.responseJSON;
                if (errors.errors.email) {
                    $("#emailError").text(errors.errors.email).show();
                }
                if(errors.errors.fullname){
                    $("#fullnameError").text(errors.errors.fullname).show();
                }
                if(errors.errors.dob){
                    $("#dobError").text(errors.errors.dob).show();
                }
                if (errors.errors.phoneNo) {
                    $("#phoneNoError").text(errors.errors.phoneNo).show();
                }
                if (errors.errors.address) {
                    $("#addressError").text(errors.errors.address).show();
                }
                if (errors.errors.note) {
                    $("#noteError").text(errors.errors.note).show();
                }
                showFailPopup();
                setTimeout(function() {
                   hideFailPopup();
                }, 1500);
            }
        });
    }
});
