new Drop_list_multi_choice("skill");
$(document).ready(function () {
    $('#recruiterId').select2({
        placeholder: 'Select Recruiter',
        allowClear: false
    });
});
document.getElementById("assignMeBtn").addEventListener("click", function() {
    assignRecruiter.call(this);
});

function assignRecruiter() {
    const userId = this.getAttribute("data-user-id");
    const recruiterSelect = $('#recruiterId');
    recruiterSelect.val(userId).trigger('change');
}
$(document).ready(function() {
    $('#recruiterId').select2({
        width: '100%'
    });
});
function validatePositiveInteger(input) {
    input.value = input.value.replace(/[^0-9]/g, '');
}
function hideFailModal(){
    document.getElementById('failModal').style.display = 'none';
}
