document.addEventListener('DOMContentLoaded', async function () {
     // get the current user
     await waitForUserData();
     const processButton = document.getElementById('process-button');
     const reminderButton = document.getElementById('reminder-button');
     if (me && me.role) {
          const role = parseInt(me.role);
          if (role === 4) {
               reminderButton.style.display = 'none';
               processButton.textContent = 'Submit Result';
               processButton.setAttribute('onclick', 'redirectToSubmitForm()');
          } else {
               processButton.textContent = 'Edit Interview';
               processButton.setAttribute('onclick', 'goToEditForm()');
          }
     }
});
function goPrev() {
     window.history.back();
}
function redirectToSubmitForm() {
     const id = window.location.pathname.split('/').pop();
     window.location.href = `/api/v1/interview/submit/${id}`
}

function goToEditForm() {
     const id = window.location.pathname.split('/').pop();
     window.location.href = `/api/v1/interview/edit/${id}`
}

function sendReminder() {
     const sendReminderModalSuccess = document.getElementById('sendReminderModalSuccess');
     const sendReminderModalError = document.getElementById('sendReminderModalError');
     initApi().then(api => {
          const id = window.location.pathname.split('/').pop();
          api.post(`/interview/send-reminder-now/${id}`).then(response => {
               console.log('Response: ', response);
               if (response) {
                    sendReminderModalSuccess.style.display = 'block';
                    clearErrors();
               } 
          }).catch(error => {
               console.log('Error:', error);
               sendReminderModalError.style.display = 'block';
          });
     })
}