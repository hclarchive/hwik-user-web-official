!function ($) {
    "use strict";

    $(document).ready(function () {
        const options = {
            backdrop: 'static ',
            closable: true,
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

        const quizStartModal = new Modal($('#quizStartModal').get(0), options);
        const quizModal = new Modal($('#quizModal').get(0), options);
        const quizEarnModal = new Modal($('#quizEarnModal').get(0), options2);
        const progressModal = new Modal($('#progressModal').get(0), options);
        const successModal = new Modal($('#successModal').get(0), options2);
        const failModal = new Modal($('#failModal').get(0), options);

        $("#quizModalCloseBtn").on("click", function () {
            location.reload();
        });

        $("#successModalCloseBtn").on("click", function () {
            location.reload();
        });

        $("#failModalCloseBtn").on("click", function () {
            failModal.hide()
        });

        $(".pop_close").on("click", function () {
            quizStartModal.hide();
            quizModal.hide();
            quizEarnModal.hide();
        });

        $(`[name="choice"]`).on("click", function () {
            $("#quizModalNextBtn").addClass("pass");
            $("#quizModalWrongSpan").hide();
        });

        const setQuizData = function (data) {
            $("#quizModalWrongSpan").hide();

            $("#quizModalNav").children().removeClass("on");
            $($("#quizModalNav").children()[data.currentIdx]).addClass("on");

            $("#quizModalContent").text(data.content);

            $("#quizModalChoice1Input").prop("checked", false);
            $("#quizModalChoice1Input").val(data.choice1);
            $("#quizModalChoice1Label").contents().last()[0].textContent = data.choice1;

            $("#quizModalChoice2Input").prop("checked", false);
            $("#quizModalChoice2Input").val(data.choice2);
            $("#quizModalChoice2Label").contents().last()[0].textContent = data.choice2;

            $("#quizModalChoice3Input").prop("checked", false);
            $("#quizModalChoice3Input").val(data.choice3);
            $("#quizModalChoice3Label").contents().last()[0].textContent = data.choice3;

            $("#quizModalChoice4Input").prop("checked", false);
            $("#quizModalChoice4Input").val(data.choice4);
            $("#quizModalChoice4Label").contents().last()[0].textContent = data.choice4;

            $("#quizModalNextBtn").data("id", data.id);
        }

        const showEarnModal = function (type, id, title, reward, isShow) {
            $("#quizEarnModalTitle").text(title);
            $("#quizEarnModalReward").text(`${reward} HWIK`);

            $("#quizEarnModalRewardBtn").off("click");
            $("#quizEarnModalRewardBtn").on("click", function (e) {
                quizEarnModal.hide();

                progressModal.show();

                postAjax({
                    url: `/api/v1/${type}/${id}/claim`,
                }).done(function (data) {
                    console.log(data);

                    $("#goExeplorer").attr("href", `${data.explorer}`);
                    successModal.show();
                }).fail(function (data) {
                    failModal.show();
                }).always(function () {
                    progressModal.hide();
                });
            });

            if (isShow === undefined || isShow === true) {
                quizEarnModal.show();
            }
        }

        $("#quizModalNextBtn").on("click", function (e) {
            $("#quizModalNextBtn").removeClass("pass");
            $("#quizModalNextBtn").prop("disabled", true);

            const quizId = $(e.target).data("quizid");
            const id = $(e.target).data("id");
            const answer = $(`[name="choice"]:checked`).val();
            if (answer) {
                postAjax({
                    url: `/api/v1/quiz/${quizId}/${id}`,
                    data: JSON.stringify({
                        answer: answer
                    })
                }).done(function (data) {
                    console.log(data);

                    if (data.currentIdx === data.count) {
                        quizModal.hide();
                        quizEarnModal.show();

                        return;
                    } else {
                        setQuizData(data);
                    }
                }).fail(function (data) {
                    $("#quizModalWrongSpan").show();
                }).always(function () {
                    $("#quizModalNextBtn").prop("disabled", false);
                });
            }
        });

        $(".claimBtn").on("click", function (e) {
            if ($("#isLogin").val() === "false") return;

            let $target = $(e.target);
            const tmpId = $target.data("id");
            if (!tmpId) {
                $target = $target.closest("[data-id]");
            }

            const type = $target.data("type");
            const id = $target.data("id");
            const title = $target.data("title");
            const reward = $target.data("reward");

            showEarnModal(type, id, title, reward);
        });

        $(".quizBtn").on("click", function (e) {
            const address = $("#address").val();
            if (!address) {
                openRequestLoginModal();
                return;
            }

            let $target = $(e.target);
            const tmpId = $target.data("id");
            if (!tmpId) {
                $target = $target.closest("[data-id]");
            }

            const type = $target.data("type");
            const id = $target.data("id");
            const title = $target.data("title");
            const reward = $target.data("reward");

            $("#quizModalNextBtn").data("quizid", id);

            $("#quizStartModalTitle").text(title);
            $("#quizModalTitle").text(title);

            $("#quizStartModalReward").text(reward);

            $("#quizStartModalStartBtn").off("click");

            showEarnModal(type, id, title, reward, false);

            postAjax({
                url: `/api/v1/quiz/${id}/start`,
            }).done(function (data) {
                console.log(data);

                $("#quizModalNav").empty();
                for (let i = 0; i < data.count; i++) {
                    $("#quizModalNav").append(`<span>${i + 1}</span>`)
                }

                $("#quizStartModalStartBtn").on("click", function (e) {
                    quizStartModal.hide();

                    setQuizData(data);

                    quizModal.show();
                });
            });

            quizStartModal.show();
        });

        $(".web3Btn").on("click", function (e) {
            const $target = $(e.target);
            let uuid = $target.data("uuid");

            if (!uuid) {
                const $parent = $target.closest("[data-uuid]");
                uuid = $parent.data("uuid");
            }

            location.href = baseUrl + joinPaths(contextPath, `/web3/${uuid}`);
        });
    });
}(window.jQuery);