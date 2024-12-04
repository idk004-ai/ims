// Chỉ được truyền vào "drop-down-menu" element
function fillOutDropDownMenu(url, dropDownMenuId, urlParam = null, callback) {
     initApi().then(api => {
          api.get(url).then(data => {
               const selectElement = document.getElementById(dropDownMenuId);
               selectElement.innerHTML = '';

               // Add default "All" option
               const defaultOption = document.createElement('option');
               defaultOption.value = '';
               defaultOption.textContent = 'All';
               selectElement.appendChild(defaultOption);

               data.forEach(record => {
                    const option = document.createElement('option');
                    callback(option, record);
                    selectElement.appendChild(option);
               });

               if (urlParam) {
                    const params = new URLSearchParams(window.location.search);
                    const selectedValue = params.get(urlParam);
                    if (selectedValue) {
                         selectElement.value = selectedValue;
                    }
               }

               selectElement.addEventListener('change', function () {
                    const params = new URLSearchParams(window.location.search);
                    if (this.value === '') {
                         params.delete(urlParam);
                    } else {
                         params.set(urlParam, this.value);
                    }

                    const newUrl = window.location.pathname +
                         (params.toString() ? '?' + params.toString() : '');
                    window.history.pushState({}, '', newUrl);
                    loadPage(0);
               })
          });
     })
}