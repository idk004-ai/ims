let me = null;

function waitForUserData() {
     return new Promise((resolve) => {
          const checkMe = () => {
               if (me) {
                    resolve(me);
               } else {
                    setTimeout(checkMe, 100);
               }
          };
          checkMe();
     });
}

function logout() {
     // Create modal elements
     const modalHtml = `
         <div id="logoutModal" class="modal-overlay">
             <div class="modal-content">
                 <h3>Confirm Logout</h3>
                 <p>Are you sure you want to log out?</p>
                 <div class="modal-buttons">
                    <button class="btn btn-primary me-2" onclick="confirmLogout()">
                         Logout
                    </button>
                    <button class="btn btn-secondary" onclick="cancelLogout()" class="modal-buttons">
                         Cancel
                    </button>
                 </div>
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

document.addEventListener('DOMContentLoaded', function () {

     function updateUserInfo(data) {
          const { username } = data;
          const authButton = document.getElementById("auth-button");
          const usernameElement = document.getElementById("username");
          const departmentElement = document.getElementById("department");

          usernameElement.textContent = !username ? "Guest" : username;
          departmentElement.textContent = !username ? "" : data.department;
          authButton.textContent = !username ? "Login" : "Logout";
          authButton.onclick = !username
               ? () => (window.location.href = "/api/v1/auth/login")
               : () => logout();
     }


     initApi().then((api) => {
          api.get("/profile")
               .then(response => {
                    me = response;
                    updateUserInfo(response);
               })
               .catch(err => {
                    me = null;
                    updateUserInfo(null);
               });
     });

});