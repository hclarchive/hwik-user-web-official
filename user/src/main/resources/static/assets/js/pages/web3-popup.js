!function (c) {
    "use strict";

    $(document).ready(function () {
        const url = $("#url").val();
        if (url) {
            console.log(url);
            location.href = url;
        } else {
            close()
        }
    });
}(window.jQuery);