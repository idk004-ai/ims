document.querySelectorAll('.inputSalary').forEach(function (input) {
    // Xử lý sự kiện 'input' cho mỗi input có class 'inputSalary'
    input.addEventListener('input', function (e) {
        // Lấy giá trị hiện tại và loại bỏ các ký tự không phải số
        let value = e.target.value.replace(/[^\d]/g, '');

        // Định dạng lại số với dấu chấm
        value = value.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

        // Cập nhật lại giá trị vào input
        e.target.value = value;
    });

    // Ngăn chặn nhập các ký tự khác ngoài số
    input.addEventListener('keypress', function (e) {
        // Chỉ cho phép các phím số từ 0-9
        if (!/[0-9]/.test(e.key)) {
            e.preventDefault();
        }
    });
});


//Xóa lỗi khi người dùng tác động vào elemets
$('input').keydown(function () {
    $(this).parent().find('p.error').text("");
});

$('input').change(function () {
    $(this).parent().find('p.error').text("");
});

$('select').parent().click(function () {
    $(this).find('p.error').text("");
});

var currentURL = window.location.href.toString();
if (currentURL.includes('create-job')) {
    $('lin').text('Create Job')
}
if (currentURL.includes('edit-job')) {
    $('lin').text('Edit Job')
}

$(`#send-data`).on('click', function () {
    $("html, body").animate({scrollTop: 0}, "slow");
    if (currentURL.includes('create-job')) {
        console.log("create");
        submitCreateJobForm();
    }
    if (currentURL.includes('edit-job')) {
        console.log("edit");
        submitEditJobForm();
    }
});

//Edit job
function submitEditJobForm() {

    // Lấy dữ liệu từ form
    const title = document.getElementById('title').value;
    const skills = document.getElementById('skills');
    const skillsSelectedValues = Array.from(skills.selectedOptions).map(option => option.value);
    let startDate = document.getElementById('startDate').value;
    let endDate = document.getElementById('endDate').value;
    const salaryFrom = document.getElementById('salaryFrom').value !== "" ? document.getElementById('salaryFrom').value.replace(/\./g, '') : 0;
    const salaryTo = document.getElementById('salaryTo').value !== "" ? document.getElementById('salaryTo').value.replace(/\./g, '') : 0;
    const benefits = document.getElementById('benefits');
    const benefitsSelectedValues = Array.from(benefits.selectedOptions).map(option => option.value);
    const workingAddress = document.getElementById('workingAddress').value;
    const level = document.getElementById('level');
    const levelSelectedValues = Array.from(level.selectedOptions).map(option => option.value);
    const description = document.getElementById('description').value;

    startDate = convertToDate(startDate);
    endDate = convertToDate(endDate);
    let a = currentURL.split("/");

    const formData = {
        jobId: a[a.length - 1],
        title: title,
        skills: skillsSelectedValues,
        startDate: startDate,
        endDate: endDate,
        salaryFrom: salaryFrom,
        salaryTo: salaryTo,
        benefits: benefitsSelectedValues,
        workingAddress: workingAddress,
        level: levelSelectedValues,
        description: description,
        statusJob: $('#status').text()
    };
    f(formData, "edit");
}

//Create job
function submitCreateJobForm() {

    // Lấy dữ liệu từ form
    const title = document.getElementById('title').value;
    const skills = document.getElementById('skills');
    const skillsSelectedValues = Array.from(skills.selectedOptions).map(option => option.value);
    let startDate = document.getElementById('startDate').value;
    let endDate = document.getElementById('endDate').value;
    const salaryFrom = document.getElementById('salaryFrom').value !== "" ? document.getElementById('salaryFrom').value.replace(/\./g, '') : 0;
    const salaryTo = document.getElementById('salaryTo').value !== "" ? document.getElementById('salaryTo').value.replace(/\./g, '') : 0;
    const benefits = document.getElementById('benefits');
    const benefitsSelectedValues = Array.from(benefits.selectedOptions).map(option => option.value);
    const workingAddress = document.getElementById('workingAddress').value;
    const level = document.getElementById('level');
    const levelSelectedValues = Array.from(level.selectedOptions).map(option => option.value);
    const description = document.getElementById('description').value;

    startDate = convertToDate(startDate);
    endDate = convertToDate(endDate);
    const formData = {
        title: title,
        skills: skillsSelectedValues,
        startDate: startDate,
        endDate: endDate,
        salaryFrom: salaryFrom,
        salaryTo: salaryTo,
        benefits: benefitsSelectedValues,
        workingAddress: workingAddress,
        level: levelSelectedValues,
        description: description
    };
    f(formData, "create");
}

function f(formData, type) {

    let url;
    if (type === "create") {
        url = '/api/v1/jobs/create-job';
    } else {
        url = '/api/v1/jobs/edit-job';
    }

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errors => {
                    errors.forEach((item, index) => {
                        $(`#${item.field}`).parent().find('p.error').text(item.defaultMessage);
                    })
                    document.getElementById("failModal").style.display = "block";
                });
            } else {
                console.log(response.url);
                console.log(response)
                document.getElementById("successModal").style.display = "block";
            }
        }).catch(err => {
        console.error('Error:', err);
        alert("An error occurred. Please try again later.");
    })
}

function hideFailModal(){
    document.getElementById('failModal').style.display = 'none';
}


function convertToDate(input) {
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(input)) {
        return "";
    }
    const date = new Date(input);
    if (isNaN(date.getTime())) {
        return "";
    }
    const [year, month, day] = input.split("-");
    if (
        date.getFullYear() !== parseInt(year, 10) ||
        date.getMonth() + 1 !== parseInt(month, 10) ||
        date.getDate() !== parseInt(day, 10)
    ) {
        return "";
    }
    return date.toISOString().split("T")[0];
}
