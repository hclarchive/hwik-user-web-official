!function (c) {
    "use strict";

    const getTxList = function (page) {
        if ($("#isLogin").val() === "false") return;

        getAjax({
            url: `/api/v1/transaction?page=${page}`,
        }).done(function (data) {
            console.log(data);

            const $temp = $('<div>');
            const $body = $("#txBody");

            // data foreach
            data.content.forEach(function (item) {
                const $row = $('<tr>');
                const $statusCol = $('<td class="green">').text(item.status);
                const $categodyCol = $('<td>').text(item.category.replace("MystiBox", ""));
                const $totalCol = $('<td>').text(`${item.amount} ${item.symbol}`);
                const $timeCol = $('<td>').text(toLocalTime(item.createdAt));
                const $txCol = $('<td>').append($("<a>").text(toShortAddress(item.hash, 10)).attr("href", `${item.explorer}`).attr("target", "_blank"));

                $row.append($statusCol);
                $row.append($categodyCol);
                $row.append($totalCol);
                $row.append($timeCol);
                $row.append($txCol);

                $temp.append($row);
            });

            // const $body = $("#txBody");
            $body.html($temp.children());

            const currentPage = data.number;
            $("#txCurPage").text(currentPage + 1);
            $("#txTotalPage").text(data.totalPages);

            $("#txPreBtn").off("click");
            if (currentPage > 0) {
                $("#txPreBtn").on("click", function (event) {
                    getTxList(currentPage - 1);
                });
            }

            $("#txNextBtn").off("click");
            if (currentPage + 1 < data.totalPages) {
                $("#txNextBtn").on("click", function (event) {
                    getTxList(currentPage + 1);
                });
            }
        });
    }

    const getMystiBoxList = function () {
        getAjax({
            url: `/api/v1/mysti`,
        }).done(function (data) {
            console.log(data);

            const $templateBase = $($("#mystiBoxItemTemplate").html());

            for (let i = 0; i < data.content.length; i++) {
                const $template = $templateBase.clone();
                const itemData = data.content[i];

                $template.bindData(itemData);

                $template.find(".mytibox_col")
                    .css("background", `url('${itemData.imagePath}') no-repeat`)
                    .css("background-size", `cover`)
                    .css("background-position", `center`);

                if (itemData.status === "Ended") {
                    $template.addClass("mytibox_wrap_gray");
                }
                if (itemData.status === "Ing") {
                    $template.find("[data-bind-status]").text("Apply");
                }

                const startAt = $template.find(`[data-bind-startedAt]`).val();
                const endAt = $template.find(`[data-bind-endedAt]`).val();
                const format = "YYYY-MM-DD HH:mm";
                $template.find(".period").text(`${moment(startAt).format(format)} ~ ${moment(endAt).format(format)}`)
                $template.on("click", function () {
                    location.href = baseUrl + joinPaths(contextPath, `/mysti/${itemData.id}`);
                });

                $("#mystiBoxListWrap").append($template);
            }
        });
    }

    async function getTokenBalance(address, tokenAddress) {
        const web3 = new Web3(window.ethereum);

        const tokenAbi = [
            {
                inputs: [{internalType: 'address', name: 'account', type: 'address'}],
                name: 'balanceOf',
                outputs: [{internalType: 'uint256', name: '', type: 'uint256'}],
                stateMutability: 'view',
                type: 'function'
            },
        ];
        const tokenContract = new web3.eth.Contract(tokenAbi, tokenAddress);

        const balance1 = await tokenContract.methods.balanceOf(address).call()
        const balance = web3.utils.fromWei(balance1, 'ether');

        return balance;
    }

    $(document).ready(async function () {
        getTxList(0);
        getMystiBoxList();

        $("#addressBox").on("click", function () {
            const tempElem = document.createElement('textarea');
            tempElem.value = $("#address").val();
            document.body.appendChild(tempElem);

            tempElem.select();
            tempElem.setSelectionRange(0, 99999);
            document.execCommand("copy");
            tempElem.setSelectionRange(0, 0);
            document.body.removeChild(tempElem);

            $('.copy_btn').toggleClass('active');

            setTimeout(function () {
                $('.copy_btn').toggleClass('active');
            }, 1000);
        });

        const address = $("#address").val();

        try {
            // HWIK
            const hwik = await getTokenBalance(address, "0x7C79828adaA2af84F76558c80AF0D580d7F1A2e9");
            $("[data-bind-hwik]").text(hwik);
        } catch (error) {
            $("[data-bind-hwik]").text("0");
            console.log(error);
        }

        try {
            // HCL
            const hcl = await getTokenBalance(address, "0x53a92bd13eb51c9527b35b38158ccd2414e23b3a");
            $("[data-bind-hcl]").text(hcl);
        } catch (error) {
            console.log(error);
            $("[data-bind-hcl]").text("0");
        }
    });
}(window.jQuery);