function toShortAddress(address, len) {
    if (!len) {
        len = 4;
    }
    return `${address.substring(0, (len + 2))}...${address.substring(address.length - len)}`;
}

function toLocalTime(utcTime) {
    return moment.utc(utcTime).local().format('YYYY-MM-DD HH:mm:ss');
}

function searchParam(key, search) {
    if (!search) {
        search = location.search;
    }
    return new URLSearchParams(search).get(key);
};

function hashParam(key) {
    return new URLSearchParams(location.hash.replace("#", "?")).get(key);
};

function utf8ToHex(str) {
    return Array.from(str).map(c =>
        c.charCodeAt(0) < 128 ? c.charCodeAt(0).toString(16) :
            encodeURIComponent(c).replace(/\%/g, '').toLowerCase()
    ).join('');
}

$.fn.findData = function (key) {
    let data = $(this).data(key);

    if (!data) {
        data = $(this).closest(`[data-${key}]`).data(key);
    }

    return data;
};

$.fn.bindData = function (obj) {
    for (let key in obj) {
        const $dom = $(this).find(`[data-bind-${key}]`);
        if ($dom.length === 0) {
            continue;
        }

        for (let i = 0; i < $dom.length; i++) {
            const item = $dom[i];
            if (item.tagName === "INPUT") {
                $(item).val(obj[key]);
            } else {
                $(item).text(obj[key]);
            }
        }
    }
};

function findData($target, key) {

}


function joinPaths(...parts) {
    return parts
        .map(part => part.replace(/(^\/+|\/+$)/g, ''))
        .join('/');
}

function postAjax(param) {
    if (!param.contentType) {
        param.contentType = "application/json; charset=utf-8";
    }

    return ajax(`POST`, param);
}

function getAjax(param) {
    return ajax(`GET`, param);
}

function putAjax(param) {
    if (!param.dataType) {
        param.contentType = "application/json; charset=utf-8";
    }

    return ajax(`PUT`, param);
}

function patchAjax(param) {
    if (!param.dataType) {
        param.contentType = "application/json; charset=utf-8";
    }

    return ajax(`PATCH`, param);
}

function deleteAjax(param) {
    return ajax(`DELETE`, param);
}

function ajax(type, param) {
    param.type = type;
    param.url = baseUrl + joinPaths(contextPath, param.url);
    if (param.error) {
        param.error = console.log("접속 도중 오류가 발생했습니다.");
    }

    return $.ajax(param);
}

async function checkNetwork() {

    let metaTestChainId = await window.ethereum.request({method: "eth_chainId"});

    if (chainId === metaTestChainId) {
        console.log("You are on the correct network!");
    } else {
        try {
            await window.ethereum.request({ method: 'wallet_switchEthereumChain', params:[{chainId: chainId}]});
        } catch (err) {
            if (err.code === 4902) {
                try {
                    if (chainId === web3.utils.toHex(1111)) {
                        await window.ethereum.request({
                            method: 'wallet_addEthereumChain',
                            params: [
                                {
                                    chainName: 'WEMIX_Mainnet',
                                    chainId: web3.utils.toHex(1111),
                                    nativeCurrency: { name : 'WEMIX', symbol: 'WEMIX', decimals: 18 },
                                    rpcUrls: ['https://api.wemix.com/'],
                                    blockExplorerUrls: ['https://explorer.wemix.com/']
                                }
                            ]
                        });
                    }
                    else if (chainId === web3.utils.toHex(1112)) {
                        await window.ethereum.request({
                            method: 'wallet_addEthereumChain',
                            params: [
                                {
                                    chainName: 'WEMIX_Testnet ',
                                    chainId: web3.utils.toHex(1112),
                                    nativeCurrency: { name : 'tWEMIX', symbol: 'tWEMIX', decimals: 18 },
                                    rpcUrls: ['https://api.test.wemix.com/'],
                                    blockExplorerUrls: ['https://explorer.test.wemix.com/']
                                }
                            ]
                        });
                    }
                }
                catch (addError) {
                    console.log(addError);
                }
            }
        }
    }

    metaTestChainId = await window.ethereum.request({method: "eth_chainId"});
    if (chainId !== metaTestChainId) {
        openMetamaskFailModal();

        throw new Error("You are on the wrong network!");
    }
}

function login() {
    if (window.ethereum) {
        window.web3 = new Web3(ethereum);

        async function connectToMetaMask() {
            try {
                await checkNetwork();

                await ethereum.request({method: 'eth_requestAccounts'});

                const accounts = await web3.eth.getAccounts();
                const from = accounts[0];
                getAjax({
                    url: `/api/v1/user/${from}/uuid/`,
                }).done(async function (data) {
                    console.log(data.uuid);

                    const message = data.uuid;
                    try {
                        // For historical reasons, you must submit the message to sign in hex-encoded UTF-8.
                        // This uses a Node.js-style buffer shim in the browser.
                        const msg = `0x${utf8ToHex(message)}`;
                        // const msg = exampleMessage;
                        const sign = await ethereum.request({
                            method: 'personal_sign',
                            params: [msg, from],
                            from: from
                        });

                        const data = {
                            "address": from,
                            "hash": sign,
                        };
                        postAjax({
                            url: `/login`,
                            data: JSON.stringify(data),
                        }).done(function (data, textStatus, jqXHR) {
                            window.location.reload();
                        });
                        console.log(sign);
                    } catch (err) {
                        console.error(err);
                    }
                });
            } catch (error) {
                console.error("User denied account access");
            }
        }

        connectToMetaMask();
    }
}

var commonModal = null;

function openCommonModal(title, content, callback, hideNoBtn, hideYesBtn) {

    $("#common_modal_title").text(title);
    $("#common_modal_desc").text(content);

    if (hideNoBtn) {
        $("#common_modal_no_btn").hide();
    } else {
        $("#common_modal_no_btn").show().off("click").on("click", function () {
            callback(false, commonModal);
        });

    }

    if (hideYesBtn) {
        $("#common_modal_yes_btn").hide();
    } else {
        $("#common_modal_yes_btn").show().off("click").on("click", function () {
            callback(true, commonModal);
        });
    }

    commonModal.show();
}

function openRequestLoginModal() {
    openCommonModal("Hwik Circle Layer", "This page requires connecting wallet to display information!", function (yes, modal) {
        closeCommonModal();
    }, true);
}

function openMetamaskFailModal() {
    openCommonModal("Hwik Circle Layer", "Unable to connect MetaMask", function (yes, modal) {
        closeCommonModal();
    }, true);
}


function closeCommonModal() {
    commonModal.hide();
}

!function (c) {
    "use strict";

    $(document).ready(function () {
        for (let prop in window) {
            if (window.hasOwnProperty(prop) && typeof window[prop] === 'function') {
                $(`[data-func="${prop}"]`).on("click", function (event) {
                    event.preventDefault();

                    window[prop]();
                });
            }
        }

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
        commonModal = new Modal($('#common_modal').get(0), options);

        {   // for mobile
            $('.m_menu').click(function () {
                $('.leftbar').toggleClass('active');
            })

            $('.leftbar .leftbar_close').click(function () {
                $('.leftbar').removeClass('active')
            })
        }

        $("#logoutBtn").on("click", function () {
            $("#logout_form").submit();
        });
    });
}(window.jQuery);