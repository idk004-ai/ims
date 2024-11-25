document.addEventListener('DOMContentLoaded', async function () {
     new Drop_list_multi_choice("interviewer_select");


     try {
          // Adjust textarea if it exists
          const textarea = document.getElementById('note');
          if (textarea) {
               textarea.focus();
               textarea.setSelectionRange(0, 0);
          }

          // Handle meeting link copying
          const meetingLinkInput = document.getElementById('meetingLink');
          const copyLinkBtn = document.getElementById('copyLinkBtn');
          const locationInput = document.getElementById('interview_location');

          if (copyLinkBtn && meetingLinkInput) {
               copyLinkBtn.addEventListener('click', function () {
                    meetingLinkInput.select();
                    meetingLinkInput.setSelectionRange(0, 99999);
                    navigator.clipboard.writeText(meetingLinkInput.value)
                         .then(() => alert('Meeting link copied to clipboard!'))
                         .catch(err => console.error('Could not copy text: ', err));
               });
          }

          // Handle location and meeting link mutual exclusivity
          if (locationInput && meetingLinkInput) {
               const handleInputState = (activeInput, inactiveInput) => {
                    inactiveInput.disabled = activeInput.value.trim() !== '';
               };

               locationInput.addEventListener('input', () =>
                    handleInputState(locationInput, meetingLinkInput));
               meetingLinkInput.addEventListener('input', () =>
                    handleInputState(meetingLinkInput, locationInput));

               // Initial state check
               handleInputState(locationInput, meetingLinkInput);
               handleInputState(meetingLinkInput, locationInput);
          }
     } catch (error) {
          console.error('Error in DOM manipulation:', error);
     }
});