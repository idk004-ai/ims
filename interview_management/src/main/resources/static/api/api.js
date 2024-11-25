
const API_BASE_URL = '/api/v1'

let isRefreshing = false;
let failedQueue = [];
const maxRetries = 5;

const processQueue = (error = null) => {
     failedQueue.forEach(promise => {
          if (error) {
               promise.reject(error);
          } else {
               promise.resolve();
          }
     });
     failedQueue = [];
};

const refreshToken = () => {
     $.ajax({
          url: API_BASE_URL + '/auth/refresh-token',
          method: 'GET',
          xhrFields: {
               withCredentials: true
          }
     }).done(() => {
          processQueue(null);
     }).fail((error) => {
          processQueue(error);
          throw error;
     });
};


const getNewToken = () => {
     if (!isRefreshing) {
          isRefreshing = true;
          try {
               refreshToken();
          } finally {
               isRefreshing = false;
          }
          return;
     }
     return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
     });
};

const initApi = () => {
     $.ajaxSetup({
          beforeSend: function (xhr) {
               xhr.setRequestHeader('Content-Type', 'application/json');
               xhr.setRequestHeader('Access-Control-Allow-Origin', '*');
          },
          xhrFields: {
               withCredentials: true
          }
     });
     return new Promise((resolve) => {
          const api = {
               baseURL: API_BASE_URL,
               headers: {
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
               },
               withCredentials: true,
          };

          api.get = function (url, data) {
               return $.ajax({
                    url: this.baseURL + url,
                    method: 'GET',
                    data: data
               }).catch((error) => {
                    return handleError(error, { method: 'GET', url: '/api/v1' + url, data });
               });
          };

          api.post = function (url, data) {
               return $.ajax({
                    url: this.baseURL + url,
                    method: 'POST',
                    data: JSON.stringify(data)
               }).catch((error) => {
                    return handleError(error, { method: 'POST', url: '/api/v1' + url, data });
               });
          };

          function handleError(error, originalRequest, retryCount = 0) {
               const shouldRenewToken = (error.status === 401) && retryCount < maxRetries;
               if (shouldRenewToken) {
                    try {
                         getNewToken();
                         return $.ajax({
                              url: originalRequest.url,
                              method: originalRequest.method,
                              data: JSON.stringify(originalRequest.data)
                         }).catch(error => {
                              return handleError(error, originalRequest, retryCount + 1);
                         });
                    } catch (error1) {
                         return Promise.reject(error1);
                    }
               } else {
                    if (retryCount >= maxRetries) {
                         logout();
                    } else {
                         return Promise.reject(error);
                    }
               }
          }

          window.api = api;
          resolve(api);

     });
};


window.initApi = initApi;
