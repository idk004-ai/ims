$(document).ready(function() {
    const PHONE_REGEX = /^(0[1-9][0-9]{7,13}|\+84[1-9][0-9]{0,13})$/;
    const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(\.[a-zA-Z]{2,})*$/;

    function showSuccessPopup() {
        $('#successPopup').fadeIn();
        const id = $("#userId").val()
        setTimeout(function() {
            window.location.href = "http://localhost:9090/api/v1/user/user_detail/" + id;
        }, 1500);

    }

    function validateForm() {
        let isValid = true;
        $(".text-danger").hide();

        if (!$("input[name='fullname']").val()) {
            $("#fullnameError").text("Required field.").show();
            isValid = false;
        }

        const email = $("#email").val();
        if (!email || !EMAIL_REGEX.test(email)) {
            $("#emailError").text("Required field.").show();
            isValid = false;
        }

        const dob = $("input[name='dob']").val();
        if (!dob) {
            $("#dobError").text("Required field.").show();
            isValid = false;
        } else {
            const dobDate = new Date(dob);
            const today = new Date();
            if (dobDate >= today) {
                $("#dobError").text("Required field").show();
                isValid = false;
            }
        }

        if (!$("input[name='address']").val()) {
            $("#addressError").text("Required field").show();
            isValid = false;
        }

        if (!$("select[name='role']").val()) {
            $("#roleError").text("Required field").show();
            isValid = false;
        }

        if (!$("select[name='status']").val()) {
            $("#statusError").text("Required field").show();
            isValid = false;
        }

        if (!$("select[name='gender']").val()) {
            $("#genderError").text("Required field").show();
            isValid = false;
        }

        if (!$("select[name='department']").val()) {
            $("#departmentError").text("Required field").show();
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

    function showFailPopup() {
        $('#failPopup').html(`
            <div class="popup-content" style="background: #fff; padding: 20px; border-radius: 5px; max-width: 300px; margin: 50px auto; text-align: center;">
                <h4 class="text-danger" style="margin: 0; font-size: 18px;">Failed to update changes</h4>
            </div>
        `);
        $('#failPopup').fadeIn();
    }

    function hideFailPopup() {
        $('#failPopup').fadeOut();
    }
    
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
            success: function() {
                showSuccessPopup();
            },
            error: function(xhr) {
                const errors = xhr.responseJSON;
                if (errors.email) {
                    $("#emailError").text(errors.email).show();
                }
                if(errors.fullname){
                    $("#fullnameError").text(errors.fullname).show();
                }
                if(errors.dob){
                    $("#dobError").text(errors.dob).show();
                }
                if (errors.phoneNo) {
                    $("#phoneNoError").text(errors.phoneNo).show();
                }
               showFailPopup();
               setTimeout(function() {
                hideFailPopup();
             }, 1500);
            }
        });
    });

    $("#confirmNo").on("click", function() {
        $("#confirmationPopup").hide();
    });
});
