(function () {
     let $body = $("body");
     let ajaxCount = 0;
     $(document).ajaxStart(function () {
          ajaxCount++;
          $body.addClass("loading");
     });

     $(document).ajaxStop(function () {
          ajaxCount--;
          if (ajaxCount <= 0) {
               ajaxCount = 0;
               $body.removeClass("loading");
          }
     });
})();
