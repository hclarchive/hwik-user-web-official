!function (c) {
    "use strict";

    $(document).ready(function () {
        const msg = searchParam("msg");
        if (msg) {
            window.localStorage.setItem("web3_oauth_msg", msg);
        }

        setTimeout(() => {
            close();
        }, 100);
    });
}(window.jQuery);