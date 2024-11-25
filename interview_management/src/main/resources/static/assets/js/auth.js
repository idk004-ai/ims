// File: auth.js

// $(document).ready(function () {
//     $('#loginForm').submit(function (e) {
//         e.preventDefault();
//         var email = $('#email').val();
//         var password = $('#password').val();

//         $.ajax({
//             url: 'http://localhost:9090/api/v1/auth/authenticate',
//             type: 'POST',
//             contentType: 'application/json',
//             data: JSON.stringify({
//                 email: email,
//                 password: password
//             }),
//             success: function (response) {
//                 window.location.href = 'http://localhost:9090/api/v1/home';
//             },
//             error: function (xhr, status, error) {
//                 $('#loginError').text('Invalid email or password').show();
//             }
//         });

//     })
// })

// function getCookie(name) {
//     const value = `; ${document.cookie}`;
//     const parts = value.split(`; ${name}=`);
//     if (parts.length === 2) return parts.pop().split(';').shift();
// }

// function showUserInterface(userInfo) {
//     $('#')
// }