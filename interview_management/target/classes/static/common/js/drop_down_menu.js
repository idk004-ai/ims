// Chỉ được truyền vào "drop-down-menu" element
function fillOutDropDownMenu(url, dropDownMenuId, urlParam = null, callback) {
     initApi().then(api => {
          api.get(url).then(data => {
               const selectElement = document.getElementById(dropDownMenuId);
               // selectElement.innerHTML = '';

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
          });
     })
}