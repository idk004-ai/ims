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

document.addEventListener('DOMContentLoaded', function () {
     function updateUserInfo(data) {
          const { username } = data || {};
          const authButton = document.getElementById("auth-button");
          const usernameElement = document.getElementById("username");
          const personIcon = document.querySelector(".person");

          usernameElement.textContent = !username ? "Guest" : username;
          authButton.textContent = !username ? "Login" : "Logout";

          authButton.onclick = !username
               ? () => (window.location.href = "/api/v1/auth/login")
               : () => {
                    authButton.classList.add("clicked");
                    personIcon.classList.remove("drop");

                    setTimeout(() => {
                         personIcon.classList.add("drop");
                         setTimeout(() => {
                              logout();
                         }, 1000);
                    }, 600);
               };
     }

     function logout() {
          window.location.href = "/api/v1/auth/login";
     }

     initApi().then((api) => {
          api.get("/profile")
               .then((response) => {
                    me = response;
                    updateUserInfo(response);
               })
               .catch((err) => {
                    me = null;
                    updateUserInfo(null);
               });
     });
});


