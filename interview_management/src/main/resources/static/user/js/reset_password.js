$(document).ready(function () {
    const currentUrl = window.location.pathname;
    const uuid = currentUrl.split("/").pop();
    $("#submitBtn").click(function (event) {
        event.preventDefault();
        const newPassword = $("input[name='newPassword']").val();
        const confirmPassword = $("input[name='newConfirmPassword']").val();
        const passwordFeedback = $("#password-feedback");
        const confirmPasswordFeedback = $("#confirmPassword-feedback");
        passwordFeedback.text("");
        confirmPasswordFeedback.text("");
        const payload = {
            newPassword: newPassword,
            uuid: uuid,
            confirmPassword: confirmPassword
        };

        $.ajax({
            url: `/api/v1/user/reset-password`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function (response) {
                if (response.success) {
                    $("#successPopup").fadeIn();
                    setTimeout(function () {
                        window.location.href = "/api/v1/auth/login";
                    }, 3000);
                } else {
                    if (response.message.includes("Required field")) {
                        passwordFeedback.text(response.message).css("color", "red");
                    }
                    if (response.message.includes("must not")) {
                        confirmPasswordFeedback.text(response.message).css("color", "red");
                    }
                }
            },
            error: function (xhr) {
                const errorMessage = xhr.responseJSON?.message || "An unexpected error occurred.";
                passwordFeedback.text(errorMessage).css("color", "red");
            }
        });
    });
    
    $("input").on("input", function () {
        const inputId = $(this).attr("id");
        if (inputId === "password") {
            $("#password-feedback").text("");
        } else if (inputId === "confirmPassword") {
            $("#confirmPassword-feedback").text("");
        }
    });
});
