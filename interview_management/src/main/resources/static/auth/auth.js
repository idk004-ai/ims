// File: auth.js

function logout() {
    initApi().then(api => {
        api.post('/auth/logout').then(() => {
            window.location.href = '/api/v1/auth/login';
        }).catch(error => {
            console.error('Logout failed:', error);
        });
    });
}

window.logout = logout;

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
                if (response.success) {
                    // Nếu có token trong response (API request)
                    if (response.token) {
                        // Xử lý token nếu cần
                        window.location.href = '/api/v1/home';
                    } else {
                        // Browser request - sử dụng redirectUrl
                        window.location.href = response.redirectUrl;
                    }
                } else {
                    $('#loginError').text('Login failed').show();
                }
            }).catch(error => {
                $('#loginError').text('Invalid email or password').show();
            });
        });
    })
})
