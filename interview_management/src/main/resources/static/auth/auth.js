// File: auth.js

$(document).ready(function () {
    $('#loginForm').submit(function (e) {
        e.preventDefault();
        var email = $('#email').val();
        var password = $('#password').val();

        initApi().then(api => {
            api.post('/auth/authenticate', {
                email: email,
                password: password
            }).then((response) => {
                console.log(response);
                if (response.success) {
                    window.location.href = response.redirectUrl;
                }
            }).catch(error => {
                $('#loginError').text('Invalid email or password').show();
            });
        });
    })
})
