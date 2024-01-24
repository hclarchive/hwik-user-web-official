!function (c) {
    "use strict";

    let tokenContract = null;

    $(document).ready(function () {

            const tokenAddress = $("#tokenAddress").val();
            const toAddress = $("#walletAddress").val();
            const fromAddress = $("#address").val();
            const id = $("#id").val();
            const amount = $("#amount").val();

            const options = function (isReload) {
                return {
                    backdrop: 'static ',
                    closable: false,
                    onHide: () => {
                        console.log('modal is hidden');
                        if (isReload) {
                            location.reload();
                        }
                    },
                    onShow: () => {
                        console.log('modal is shown');
                    },
                    onToggle: () => {
                        console.log('modal has been toggled');
                    }
                }
            };

            const applyModal = new Modal($('#applyModal').get(0), options());
            const progressModal = new Modal($('#progressModal').get(0), options());
            const successModal = new Modal($('#successModal').get(0), options(true));
            const failModel = new Modal($('#failModel').get(0), options(true));

            const $p = $(".period");
            const startAt = $(`#startedAt`).val();
            const endAt = $(`#endedAt`).val();
            const endDate = new Date(endAt);

            const format = "YYYY-MM-DD HH:mm";
            $p.text(`${moment(startAt).format(format)} ~ ${moment(endAt).format(format)}`)

            const $reaminDay = $("#reaminDay");
            const $remainHour = $("#remainHour");
            const $remainMin = $("#remainMin");
            const $remainSec = $("#remainSec");

            setInterval(function () {
                const now = new Date();
                const diff = endDate - now;
                if (diff > 0) {
                    const diffDate = new Date(diff);
                    const diffDay = diffDate.getDate() - 1;
                    const diffHour = diffDate.getHours();
                    const diffMin = diffDate.getMinutes();
                    const diffSec = diffDate.getSeconds();

                    $reaminDay.val(diffDay > 9 ? diffDay : `0${diffDay}`);
                    $remainHour.val(diffHour > 9 ? diffHour : `0${diffHour}`);
                    $remainMin.val(diffMin > 9 ? diffMin : `0${diffMin}`);
                    $remainSec.val(diffSec > 9 ? diffSec : `0${diffSec}`);
                }
            }, 1000);

            $("#applyBtn").on("click", async function () {
                const address = $("#address").val();
                if (!address) {
                    openRequestLoginModal();
                    return;
                }

                getAjax({
                    url: `/api/v1/mysti/${id}/entry`,
                }).done(function (data, textStatus, jqXHR) {
                    if (window.ethereum) {
                        applyModal.show();

                        $("#applyModal_cancel_btn").off("click");
                        $("#applyModal_cancel_btn").on("click", function () {
                            applyModal.hide()
                        });

                        $("#applyModal_apply_btn").off("click");
                        $("#applyModal_apply_btn").on("click", async function () {
                            applyModal.hide();

                            progressModal.show();

                            try {
                                await checkNetwork();

                                window.web3 = new Web3(ethereum);
                                await ethereum.request({method: 'eth_requestAccounts'});
                                const tokenContract = getContract(tokenAddress);

                                const approveRes = await approve(tokenContract, fromAddress, toAddress, amount);

                                if (!approveRes) {
                                    progressModal.hide();

                                    openMetamaskFailModal();

                                    return;
                                }

                                const hash = approveRes.transactionHash;

                                postAjax({
                                    url: `/api/v1/mysti/${id}/entry`,
                                    data: JSON.stringify({
                                        "hash": hash,
                                    })
                                }).done(function (data, textStatus, jqXHR) {
                                    $("#goExeplorer").attr("href", `${data.explorer}`);

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
                    }
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    openCommonModal("Error", "The number of applications has been exceeded.", function() {
                        closeCommonModal();
                    }, true);

                }).always(function () {
                });
            });

            $("#successModal_close_btn").on("click", function () {
                location.reload();
            });

            $("#failModel_close_btn").on("click", function () {
                failModel.hide();
            });

            async function approve(contract, fromAddress, toAddress, amount) {
                try {
                    const amountToSend = web3.utils.toWei(amount, 'ether');
                    const approve = contract.methods.approve(toAddress, amountToSend);
                    const gas = await approve.estimateGas({from: fromAddress});

                    const tx = {
                        from: fromAddress,
                        to: tokenAddress,
                        gas: gas,
                        maxPriorityFeePerGas: 101000000000,
                        maxFeePerGas: 101000000000,
                        data: approve.encodeABI(),
                    }

                    return await web3.eth.sendTransaction(tx);
                } catch (e) {
                    console.log(e);
                }

                return null;
            }

            function getContract(tokenAddress) {
                const TOKEN_ABI = [
                    {
                        "inputs": [
                            {
                                "internalType": "string",
                                "name": "name",
                                "type": "string"
                            },
                            {
                                "internalType": "string",
                                "name": "symbol",
                                "type": "string"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "constructor"
                    },
                    {
                        "anonymous": false,
                        "inputs": [
                            {
                                "indexed": true,
                                "internalType": "address",
                                "name": "owner",
                                "type": "address"
                            },
                            {
                                "indexed": true,
                                "internalType": "address",
                                "name": "spender",
                                "type": "address"
                            },
                            {
                                "indexed": false,
                                "internalType": "uint256",
                                "name": "value",
                                "type": "uint256"
                            }
                        ],
                        "name": "Approval",
                        "type": "event"
                    },
                    {
                        "anonymous": false,
                        "inputs": [
                            {
                                "indexed": true,
                                "internalType": "address",
                                "name": "from",
                                "type": "address"
                            },
                            {
                                "indexed": true,
                                "internalType": "address",
                                "name": "to",
                                "type": "address"
                            },
                            {
                                "indexed": false,
                                "internalType": "uint256",
                                "name": "value",
                                "type": "uint256"
                            }
                        ],
                        "name": "Transfer",
                        "type": "event"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "owner",
                                "type": "address"
                            },
                            {
                                "internalType": "address",
                                "name": "spender",
                                "type": "address"
                            }
                        ],
                        "name": "allowance",
                        "outputs": [
                            {
                                "internalType": "uint256",
                                "name": "",
                                "type": "uint256"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "spender",
                                "type": "address"
                            },
                            {
                                "internalType": "uint256",
                                "name": "amount",
                                "type": "uint256"
                            }
                        ],
                        "name": "approve",
                        "outputs": [
                            {
                                "internalType": "bool",
                                "name": "",
                                "type": "bool"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "account",
                                "type": "address"
                            }
                        ],
                        "name": "balanceOf",
                        "outputs": [
                            {
                                "internalType": "uint256",
                                "name": "",
                                "type": "uint256"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [],
                        "name": "decimals",
                        "outputs": [
                            {
                                "internalType": "uint8",
                                "name": "",
                                "type": "uint8"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "spender",
                                "type": "address"
                            },
                            {
                                "internalType": "uint256",
                                "name": "subtractedValue",
                                "type": "uint256"
                            }
                        ],
                        "name": "decreaseAllowance",
                        "outputs": [
                            {
                                "internalType": "bool",
                                "name": "",
                                "type": "bool"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "spender",
                                "type": "address"
                            },
                            {
                                "internalType": "uint256",
                                "name": "addedValue",
                                "type": "uint256"
                            }
                        ],
                        "name": "increaseAllowance",
                        "outputs": [
                            {
                                "internalType": "bool",
                                "name": "",
                                "type": "bool"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "function"
                    },
                    {
                        "inputs": [],
                        "name": "name",
                        "outputs": [
                            {
                                "internalType": "string",
                                "name": "",
                                "type": "string"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [],
                        "name": "symbol",
                        "outputs": [
                            {
                                "internalType": "string",
                                "name": "",
                                "type": "string"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [],
                        "name": "totalSupply",
                        "outputs": [
                            {
                                "internalType": "uint256",
                                "name": "",
                                "type": "uint256"
                            }
                        ],
                        "stateMutability": "view",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "to",
                                "type": "address"
                            },
                            {
                                "internalType": "uint256",
                                "name": "amount",
                                "type": "uint256"
                            }
                        ],
                        "name": "transfer",
                        "outputs": [
                            {
                                "internalType": "bool",
                                "name": "",
                                "type": "bool"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "function"
                    },
                    {
                        "inputs": [
                            {
                                "internalType": "address",
                                "name": "from",
                                "type": "address"
                            },
                            {
                                "internalType": "address",
                                "name": "to",
                                "type": "address"
                            },
                            {
                                "internalType": "uint256",
                                "name": "amount",
                                "type": "uint256"
                            }
                        ],
                        "name": "transferFrom",
                        "outputs": [
                            {
                                "internalType": "bool",
                                "name": "",
                                "type": "bool"
                            }
                        ],
                        "stateMutability": "nonpayable",
                        "type": "function"
                    }
                ];

                return new web3.eth.Contract(TOKEN_ABI, tokenAddress);
            }
        }
    )
    ;

}
(window.jQuery);