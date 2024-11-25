/**
 * Display error messages on the form
 *
 * @param errors - An array of error objects, where each object contains information about
 *                 the field with an error and the corresponding error message.
 */
function displayErrors(errors) {
     // Xóa các thông báo lỗi hiện có
     clearErrors();

     errors.forEach(error => {
          const field = document.getElementById(error.field);

          if (field) {
               const errorMessageElement = field.nextElementSibling;

               if (errorMessageElement && errorMessageElement.tagName === 'P' && errorMessageElement.classList.contains('error')) {
                    errorMessageElement.innerText = error.defaultMessage;
               } else {
                    const newErrorMessage = document.createElement("p");
                    newErrorMessage.className = "error text-danger";
                    newErrorMessage.innerText = error.defaultMessage;
                    field.insertAdjacentElement("afterend", newErrorMessage);
               }

               // Thêm sự kiện 'input' để xóa thông báo lỗi khi người dùng bắt đầu nhập
               field.addEventListener("input", function () {
                    if (field.nextElementSibling && field.nextElementSibling.classList.contains("error")) {
                         field.nextElementSibling.remove();
                    }
               }, { once: true }); // Sự kiện chỉ kích hoạt một lần cho đến khi lỗi mới xuất hiện
          }
     });
}

function clearErrors() {
     document.querySelectorAll(".error").forEach(el => el.remove());
}