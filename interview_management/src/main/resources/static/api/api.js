
const API_BASE_URL = '/api/v1'

let isRefreshing = false;
let failedQueue = [];
const maxRetries = 5;

const processQueue = (error = null) => {
     const queue = [...failedQueue];
     failedQueue = [];

     queue.forEach(promise => {
          if (error) {
               promise.reject(error);
          } else {
               promise.resolve();
          }
     });
};

const refreshToken = async () => {
     $.ajax({
          url: API_BASE_URL + '/auth/refresh-token',
          method: 'GET',
          timeout: 5000,
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


const getNewToken = async () => {
     if (!isRefreshing) {
          isRefreshing = true;
          try {
               await refreshToken();
          } catch (error) {
               throw error;
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
                    return handleError(error, { method: 'GET', url: API_BASE_URL + url, data });
               });
          };

          api.post = function (url, data) {
               return $.ajax({
                    url: this.baseURL + url,
                    method: 'POST',
                    data: JSON.stringify(data)
               }).catch((error) => {
                    return handleError(error, { method: 'POST', url: API_BASE_URL + url, data });
               });
          };

          function handleError(error, originalRequest, retryCount = 0) {
               const isTokenExpiredError = error.status === 401;
               if (isTokenExpiredError && retryCount < maxRetries) {
                    return getNewToken()
                         .then(() => {
                              return $.ajax({
                                   url: originalRequest.url,
                                   method: originalRequest.method,
                                   data: originalRequest.data ? JSON.stringify(originalRequest.data) : undefined
                              });
                         })
                         .catch(error => {
                              if (error.status === 401) {
                                   return handleError(error, originalRequest, retryCount + 1);
                              }
                              return Promise.reject(error);
                         });
               }
               if (retryCount >= maxRetries) {
                    window.dispatchEvent(new CustomEvent('session-expired'));
                    logout();
                    return Promise.reject(new Error('Max retry attemps reached'));
               }
               return Promise.reject(error);
          }

          window.api = api;
          resolve(api);

     });
};

const cleanup = () => {
     failedQueue = [];
     isRefreshing = false;
};

window.addEventListener('session-expired', cleanup);
window.addEventListener('logout', cleanup);

window.initApi = initApi;
