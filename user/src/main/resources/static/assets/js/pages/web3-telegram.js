!function (c) {
    "use strict";

    $(document).ready(function () {
        const state = searchParam("state");
        const authResult = hashParam("tgAuthResult");

        console.log("address", address);
        console.log("authResult", authResult);

        try {
            postAjax({
                url: `/api/v1/web3/sns/telegram`,
                data: JSON.stringify({
                    state: state,
                    authResult: authResult,
                })
            }).done(function (data, textStatus, jqXHR) {
                console.log(data);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                console.warn(errorThrown);
            });
        } catch (e) {
            console.error(e);
        }
    });
}(window.jQuery);