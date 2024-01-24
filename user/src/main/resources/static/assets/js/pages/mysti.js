!function (c) {
    "use strict";

    $(document).ready(function () {

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
        const couponModal = new Modal($('#coupon_modal').get(0), options);

        $("#coupon_modal_close_btn").on("click", function () {
            couponModal.hide();
        });

        // change dateformat
        $("#going_wrap, #ended_wrap").children().each(function () {
            const $this = $(this);
            const $p = $this.find(".period");
            const startAt = $this.find(`input[name='startedAt']`).val();
            const endAt = $this.find(`input[name='endedAt']`).val();

            const format = "YYYY-MM-DD HH:mm";
            $p.text(`${moment(startAt).format(format)} ~ ${moment(endAt).format(format)}`)
        })

        $("[data-mystibox-id]").on("click", function () {
            const id = $(this).data("mystibox-id");
            console.log(id);
            location.href = baseUrl + joinPaths(contextPath, `/mysti/${id}`);
        });

        const getHistory = function (page) {
            getAjax({url: `/api/v1/mysti/history?page=${page}&size=10`}
            ).done(function (data, textStatus, jqXHR) {
                $("#history_wrap").show();

                const $rowTemplate = $($("#history_row_template").html());
                const $subRowTemplate = $($("#history_sub_row_template").html());
                const $historyTable = $("#history_table");
                const format = "YYYY-MM-DD HH:mm";

                const $rows = $("<div>");
                data.content.forEach(function (item) {
                    const $row = $rowTemplate.clone();
                    $row.find("[name=title]").text(item.title);
                    $row.find("[name=date]").text(`${moment(item.startedAt).format(format)} ~ ${moment(item.endedAt).format(format)}`);

                    const applayCount = item.transactionList.filter(el => el.category === "MystiBoxApply").length;

                    $row.find("[name=apply]").text(`${applayCount} / ${item.curEntryCount}`);
                    if (item.status === "Ended") {
                        if (item.isWinner) {
                            $row.find("[name=summary-btn]").text("You're the winner! Check the Coupon").on("click", function () {
                                couponModal.show();
                            });
                        } else {
                            const reward = item.transactionList.filter(el => el.category === "MystiBoxReward").map(el => el.amount).reduce((a, b) => a + b, 0)
                            $row.find("[name=summary-btn]").text(`You got ${reward} WEMIX`).addClass("gray_btn");
                        }
                    } else {
                        $row.find("[name=summary-btn]").text(`You're participating in Mystibox.`).addClass("gray_btn");
                    }

                    $row.find("[name=collapse_btn]").on("click", function (e) {
                        const $target = $(e.target);
                        if ($target.hasClass("down")) {
                            $target.removeClass("down");

                            $($row[2]).show();
                        } else {
                            $target.addClass("down");

                            $($row[2]).hide();
                        }
                    })

                    const $subRows = $("<div></div>");
                    item.transactionList.forEach(function (tx) {
                        const $subRow = $subRowTemplate.clone();

                        $subRow.find("[name=status]").text(tx.status);
                        $subRow.find("[name=category").text(tx.category.replace("MystiBox", ""));
                        $subRow.find("[name=amount").text(`${tx.amount} ${tx.symbol}`);
                        $subRow.find("[name=time").text(`${tx.createdAt}`);
                        $subRow.find("[name=hash").children().first().text(`${toShortAddress(tx.hash, 10)}`).attr("href", `${tx.explorer}`);

                        $subRows.append($subRow)
                    })

                    $($row[2]).append($subRows.children()).hide();

                    $rows.append($row);
                })

                if (data.content.length === 0) {
                    $("#pagenavigation").hide();
                } else {
                    $("#pagenavigation").show();
                    $historyTable.empty().append($rows);
                }

                if (data.first) {
                    $("#page_pre_btn").hide();
                } else {
                    $("#page_pre_btn").show().off("click").on("click", function () {
                        const page = data.number - 1;
                        getHistory(page);
                    })
                }
                if (data.last) {
                    $("#page_next_btn").hide();
                } else {
                    $("#page_next_btn").show().off("click").on("click", function () {
                        const page = data.number + 1;
                        getHistory(page);
                    })
                }

                $("#page_cur").text(`${data.number + 1}`);
                $("#page_total").text(`${data.totalPages}`);

                console.log(data);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                console.log(errorThrown);
            }).always(function () {
            });
        }

        const address = $("#address").val();
        if (address) {
            getHistory(0);
        }

    });
}(window.jQuery);