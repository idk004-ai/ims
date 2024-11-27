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

          usernameElement.textContent = !username ? "Guest" : username;
          authButton.textContent = !username ? "Login" : "Logout";

          authButton.onclick = !username
               ? () => (window.location.href = "/api/v1/auth/login")
               : () => logout();
     }

     function logout() {
          initApi().then((api) => {
               api.post("/auth/logout")
                    .then((response) => {
                         window.location.reload();
                    })
                    .catch((err) => {
                         console.log(err);
                    });
          });
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


