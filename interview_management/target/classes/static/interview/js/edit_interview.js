function goPrev() {
     window.location.href = '/api/v1/interview';
}

function getSelected(id) {
     const select = document.getElementById(id);
     const selectedOptions = Array.from(select.selectedOptions);
     return selectedOptions.map(option => option.value);
}


function submitForm() {
     const title = document.getElementById('interview_title').value;
     const job = document.getElementById('interview_job').value;
     const candidate = document.getElementById('interview_candidate').value;
     const selectedInterviewers = getSelected('interviewer_select');
     const schedule = document.getElementById('interview_schedule').value;
     const location = document.getElementById('interview_location').value;
     const startTime = document.getElementById('startTime').value;
     const endTime = document.getElementById('endTime').value;
     const recruiter = document.getElementById('interview_recruiter').value;
     const notes = document.getElementById('note').value;
     const meetingLink = document.getElementById('meetingLink').value;
     const result = document.getElementById('interview_result').value;

     const payload = {
          interview_title: title,
          interview_job: job,
          interview_candidate: candidate,
          interviewer_tag: selectedInterviewers,
          interview_schedule: schedule,
          startTime: startTime,
          endTime: endTime,
          interview_recruiter: recruiter,
          interview_location: location,
          note: notes,
          meetingLink: meetingLink,
          interview_result: result
     }
     const id = window.location.pathname.split('/').pop();
     const updateSuccess = document.getElementById('updateInterviewSuccessModal');
     const updateError = document.getElementById('updateInterviewErrorModal');
     initApi().then(api => {
          api.post(`/interview/edit/${id}`, payload)
               .then(data => {
                    if (data) {
                         updateSuccess.style.display = 'block';
                         clearErrors();
                    }
               }).catch(err => {
                    updateError.style.display = 'block';
                    displayErrors(err.responseJSON);
               });
     });
}

document.addEventListener('DOMContentLoaded', async function () {

     // get the current user
     const userData = await waitForUserData();
     if (userData && userData.role) {
          const role = parseInt(userData.role);
          if (role !== 3) {
               document.getElementById('assignMeBtn').hidden = true;
          }
     }


})

function removeError() {
     console.log('remove error');
     document.getElementById('interviewer_tag').nextElementSibling.remove();
}

function assignMe() {
     if (me) {
          const recruiterSelect = document.getElementById('interview_recruiter');

          const existingOption = Array.from(recruiterSelect.options)
               .find(option => option.value === me.id.toString());

          if (existingOption) {
               recruiterSelect.value = me.id;
          } else {
               // If option doesn't exist, create and add it
               const option = new Option(me.fullname, me.id);
               recruiterSelect.add(option);
               recruiterSelect.value = me.id;
          }
     } else {
          console.log('me is null');
     }
}

function confirmCancelInterview() {
     document.getElementById('confirmModal').style.display = 'block';
}

function cancelInterview() {
     // window.location.href = '/api/v1/interview';
     const id = window.location.pathname.split('/').pop();
     const updateSuccess = document.getElementById('cancelInterviewSuccessModal');
     const updateError = document.getElementById('cancelInterviewErrorModal');
     initApi().then(api => {
          api.post(`/interview/cancel/${id}`)
               .then(data => {
                    if (data) {
                         document.getElementById('confirmModal').style.display = 'none';
                         updateSuccess.style.display = 'block';
                    }
               }).catch(err => {
                    console.log(err);
                    document.getElementById('confirmModal').style.display = 'none';
                    const errorMessageElement = document.querySelector('#cancelInterviewErrorModal h4');
                    errorMessageElement.innerHTML = err.responseText;
                    updateError.style.display = 'block';
               });
     });
}

