// File: auth.js

// function logout() {
//     initApi().then(api => {
//         api.post('/auth/logout').then(() => {
//             window.location.href = '/api/v1/auth/login';
//         }).catch(error => {
//             console.error('Logout failed:', error);
//         });
//     });
// }

function logout() {
    // Create modal elements
    const modalHtml = `
        <div id="logoutModal" class="modal-overlay">
            <div class="modal-content">
                <h3>Confirm Logout</h3>
                <p>Are you sure you want to log out?</p>
                <div class="modal-buttons"><button class="btn btn-success" onclick="confirmLogout()">Yes, Logout</button></div>
                <div class="modal-buttons"><button class="btn btn-danger" onclick="cancelLogout()" class="modal-buttons">Cancel</button></div>
            </div>
        </div>
    `;

    // Add modal to document
    document.body.insertAdjacentHTML('beforeend', modalHtml);

    // Show modal
    document.getElementById('logoutModal').style.display = 'block';
}

function confirmLogout() {
    // Hide and remove modal
    const modal = document.getElementById('logoutModal');
    modal.style.display = 'none';
    modal.remove();

    // Proceed with logout
    initApi().then(api => {
        api.post('/auth/logout').then(() => {
            window.location.href = '/api/v1/auth/login';
        }).catch(error => {
            console.error('Logout failed:', error);
        });
    });
}

function cancelLogout() {
    // Hide and remove modal
    const modal = document.getElementById('logoutModal');
    modal.style.display = 'none';
    modal.remove();
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
