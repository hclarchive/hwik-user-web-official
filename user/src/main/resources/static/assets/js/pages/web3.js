!function (c) {
    "use strict";

    let msgTimmer = null;
    let popup = null;

    $(document).ready(function () {
        function receiveMessage(event) {
            if (event.origin !== 'https://oauth.telegram.org') return;

            console.log(event.data);
            const data = JSON.parse(event.data);

            postAjax({
                url: `/api/v1/web3/sns/telegram`,
                data: JSON.stringify({authResult: JSON.stringify(data.result)})
            }).done(function (data, textStatus, jqXHR) {
                if (data) {
                    openCommonModal("Error", data, function (yes, modal) {
                        location.reload();
                    }, true);
                } else {
                    location.reload();
                }
            });
        }

        window.addEventListener("message", receiveMessage, false);

        const options = {
            backdrop: 'static ',
            closable: false,
            onHide: () => {
                console.log('modal is hidden');
            },
            onShow: () => {
                console.log('modal is shown');
            },
            onToggle: () => {
                console.log('modal has been toggled');
            }
        };

        const options2 = {
            backdrop: 'static ',
            closable: true,
            onHide: () => {
                console.log('modal is hidden');
                $('#confettiStopButton').trigger("click");
            },
            onShow: () => {
                console.log('modal is shown');
                $('#confettiStartButton').trigger("click");
            },
            onToggle: () => {
                console.log('modal has been toggled');
            }
        };

        const claimModal = new Modal($('#claim_modal').get(0), options);
        $("#claim_modal_no_btn").on("click", function () {
            claimModal.hide();
        });

        const progressModal = new Modal($('#progress_modal').get(0), options);

        const successModal = new Modal($('#success_modal').get(0), options2);
        $("#success_modal_no_btn").on("click", function () {
            location.reload();
        });

        const failModel = new Modal($('#fail_model').get(0), options);

        $("#fail_modal_no_btn").on("click", function () {
            failModel.hide();
        });

        $("[data-popup]").on("click", function (e) {
            const address = $("#address").val();
            if (!address) {
                openRequestLoginModal();
                return;
            }

            const $target = $(e.target);

            const id = $target.findData("popup");
            if (!id) {
                return;
            }

            const sns = $target.findData("sns");
            const extraData = $target.findData("extra");
            let title = "";
            let content = "";

            if (sns === 'Twitter') {
                title = "Twitter";
                content = `Do you want follow @${extraData}`;
            } else if (sns === 'Telegram') {
                title = "Telegram";
                content = `Do you want to join community`;
            }

            if (!title) {
                return;
            }

            openCommonModal(title, content, function (yes, modal) {

                modal.hide();

                if (!yes) {
                    return;
                }

                getAjax({url: `/api/v1/web3/${id}/popupurl`}).done(function (data, textStatus, jqXHR) {
                    console.log(data);

                    window.localStorage.setItem("web3_oauth_msg", "");

                    if (msgTimmer) {
                        clearInterval(msgTimmer);
                        msgTimmer = null;
                    }

                    if (popup) {
                        popup.close();
                        popup = null;
                    }

                    if (data.startsWith("telegram_login")) {
                        const split = data.split(":");
                        window.Telegram.Login.auth({bot_id: split[1]})
                    } else {
                        const url = data;
                        const name = "sns popup";
                        const option = "width = 650, height = 700, top = 100, left = 200, location = no"
                        popup = window.open(url, name, option);
                    }

                    const limit = new Date();
                    msgTimmer = setInterval(() => {
                        try {
                            const msg = window.localStorage.getItem("web3_oauth_msg");
                            if (msg) {
                                window.localStorage.setItem("web3_oauth_msg", "");

                                if (msg === "success") {
                                    location.reload();
                                } else {
                                    openCommonModal("Error", msg, function (yes, modal) {
                                        //modal.hide();
                                        location.reload();
                                    }, true);
                                }

                                msgTimmer && clearInterval(msgTimmer);
                            }
                        } catch (e) {
                        }
                    }, 1000);

                }).fail(function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR);
                });
            });
        });


        $("div[data-claim]").on("click", function (e) {
            const $target = $(e.target);

            const claimId = $target.findData("claim");
            const reward = $target.findData("reward");
            $("#claim_modal_amount").text(`${reward} WHIK`);

            $("#claim_modal_yes_btn").off("click").on("click", function () {
                claimModal.hide();
                progressModal.show();

                try {
                    postAjax({
                        url: `/api/v1/web3/${claimId}/claim`
                    }).done(function (data, textStatus, jqXHR) {
                        const hash = data.hash;
                        $("#go_exeplorer").attr("href", `${data.explorer}`);

                        successModal.show();
                    }).fail(function (jqXHR, textStatus, errorThrown) {
                        console.log(errorThrown);

                        failModel.show();
                    }).always(function () {
                        progressModal.hide();
                    });
                } catch (e) {
                    progressModal.hide();
                }
            });
            claimModal.show();
            console.log("claimId", claimId);
        });
    });
}(window.jQuery);