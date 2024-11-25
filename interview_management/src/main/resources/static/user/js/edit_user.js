$(document).ready(function() {
    const PHONE_REGEX = /^(0[1-9][0-9]{7,13}|\+84[1-9][0-9]{0,13})$/;
    const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;


    function validateForm() {
        let isValid = true;
        $(".text-danger").hide();

        if (!$("input[name='fullname']").val()) {
            $("#fullnameError").text("Full Name is required.").show();
            isValid = false;
        }

        const email = $("#email").val();
        if (!email || !EMAIL_REGEX.test(email)) {
            $("#emailError").text("Email is invalid or required.").show();
            isValid = false;
        }

        const dob = $("input[name='dob']").val();
        if (!dob) {
            $("#dobError").text("Date of birth is required.").show();
            isValid = false;
        } else {
            const dobDate = new Date(dob);
            const today = new Date();
            if (dobDate >= today) {
                $("#dobError").text("Date of birth must be in the past.").show();
                isValid = false;
            }
        }

        if (!$("input[name='address']").val()) {
            $("#addressError").text("Address is required.").show();
            isValid = false;
        }

        if (!$("select[name='role']").val()) {
            $("#roleError").text("Role is required.").show();
            isValid = false;
        }

        if (!$("select[name='status']").val()) {
            $("#statusError").text("Status is required.").show();
            isValid = false;
        }

        if (!$("select[name='gender']").val()) {
            $("#genderError").text("Gender is required.").show();
            isValid = false;
        }

        if (!$("select[name='department']").val()) {
            $("#departmentError").text("Department is required.").show();
            isValid = false;
        }

        const phone = $("input[name='phoneNo']").val();
        if (phone && !PHONE_REGEX.test(phone)) {
            $("#phoneNoError").text("Phone number is invalid.").show();
            isValid = false;
        }

        return isValid;
    }


    $("#submitBtn").on("click", function(event) {
        event.preventDefault(); 

        if (validateForm()) {
            $("#confirmationPopup").css("display", "flex"); 
        }
    });

    $("input[name='fullname']").on("input", function() {
        $("#fullnameError").hide();
    });
    
    $("#email").on("input", function() {
        $("#emailError").hide();
    });
    
    $("input[name='dob']").on("input", function() {
        $("#dobError").hide();
    });
    
    $("input[name='address']").on("input", function() {
        $("#addressError").hide();
    });
    
    $("select[name='role']").on("change", function() {
        $("#roleError").hide();
    });
    
    $("select[name='status']").on("change", function() {
        $("#statusError").hide();
    });
    
    $("select[name='gender']").on("change", function() {
        $("#genderError").hide();
    });
    
    $("select[name='department']").on("change", function() {
        $("#departmentError").hide();
    });
    
    $("input[name='phoneNo']").on("input", function() {
        $("#phoneNoError").hide();
    });

    $("#confirmYes").on("click", function() {
        $("#confirmationPopup").hide();

        const userDTO = {
            id: $("#userId").val(),
            fullname: $("input[name='fullname']").val(),
            email: $("input[name='email']").val(),
            dob: $("input[name='dob']").val(),
            phoneNo: $("input[name='phoneNo']").val(),
            role: $("select[name='role']").val(),
            status: $("select[name='status']").val(),
            address: $("input[name='address']").val(),
            gender: $("select[name='gender']").val(),
            department: $("select[name='department']").val(),
            note: $("input[name='note']").val()
        };

        const userContactDTO = {
            originalEmail: $("#email").data("original-email"),
            originalPhoneNo: $("#phoneNo").data("original-phoneno")
        };

        const requestData = {
            userDTO: userDTO,
            userContactDTO: userContactDTO
        };

        $.ajax({
            url: "/api/v1/user/update-user",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestData),
            success: function(response) {
                $('#message').text('Change has been successfully updated').css('color', 'green').show();
                setTimeout(function() {
                    $('#message').fadeOut();
                }, 2000);
            },
            error: function(xhr) {
                const errors = xhr.responseJSON;
                if (errors.email) {
                    $("#emailError").text(errors.email).show();
                }
                if (errors.phoneNo) {
                    $("#phoneNoError").text(errors.phoneNo).show();
                }
                $('#message').text('Failed to update change').css('color', 'red').show();
                setTimeout(function() {
                    $('#message').fadeOut();
                }, 2000);
            }
        });
    });

    $("#confirmNo").on("click", function() {
        $("#confirmationPopup").hide();
    });
});
