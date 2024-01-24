package com.hwik.hcl.core.util

import org.bouncycastle.jcajce.provider.digest.Keccak
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.abi.EventValues
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.tx.response.TransactionReceiptProcessor
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*


@Service
class Web3jUtil {

    @Value("\${web3j.token.client-address}")
    lateinit var clientAddress: String

    @Value("\${web3j.token.contract-address}")
    lateinit var tokenAddress: String

    @Value("\${web3j.mystibox.wallet-in-address}")
    lateinit var walletInAddress: String

    @Value("\${web3j.mystibox.wallet-in-private-key}")
    lateinit var inPrivateKey: String

    @Value("\${web3j.mystibox.wallet-out-address}")
    lateinit var walletOutAddress: String

    @Value("\${web3j.mystibox.wallet-out-private-key}")
    lateinit var outPrivateKey: String

    @Value("\${web3j.token.symbol}")
    lateinit var symbol: String

    @Value("\${web3j.token.chain-id}")
    var chainId: Long = 0L

    @Value("\${web3j.token.explorer-address:''}")
    lateinit var explorerAddress: String

    companion object {
        val logger = org.slf4j.LoggerFactory.getLogger(Web3jUtil::class.java)
    }

    fun getFee(receipt: TransactionReceipt): BigDecimal {
        return Convert.fromWei(BigDecimal(receipt.gasUsed), Convert.Unit.GWEI) * BigDecimal(100)
    }

    fun recoverAddressFromSignature(message: String, hash: String, address: String): String {
        val messageBytes = Numeric.hexStringToByteArray(message)
        val proof = createProof(messageBytes)

        val digest = Keccak.Digest256()
        val keccakHash = digest.digest(proof)

        val signatureBytes = Numeric.hexStringToByteArray(hash)
        var v = signatureBytes[64]
        if (v < 27) {
            v = (v + 27).toByte()
        }
        val r = Arrays.copyOfRange(signatureBytes, 0, 32)
        val s = Arrays.copyOfRange(signatureBytes, 32, 64)
        val address = ecrecoverAddress(keccakHash, v, r, s)

        return "0x${address}"
    }

    private fun ecrecoverAddress(proof: ByteArray?, v: Byte, r: ByteArray, s: ByteArray): String {
        val esig = ECDSASignature(Numeric.toBigInt(r), Numeric.toBigInt(s))
        var recovery = v - 27;
        var pubKey: BigInteger = Sign.recoverFromSignature(recovery, esig, proof)

        return Keys.getAddress(pubKey)
    }

    private fun createProof(trxId: ByteArray): ByteArray {
        val ethPrefixMessage: ByteArray =
            ("\u0019Ethereum Signed Message:\n" + trxId.size.toString()).toByteArray()
        val sigBuffer: ByteBuffer = ByteBuffer.allocate(ethPrefixMessage.size + trxId.size)
        sigBuffer.put(ethPrefixMessage)
        sigBuffer.put(trxId)
        return sigBuffer.array()
    }


    fun transferFromErc20Token(fromAddress: String, toAddress: String, amount: BigDecimal): TransactionReceipt {
        try {
            val TX_END_CHECK_DURATION: Long = 5000
            val TX_END_CHECK_RETRY = 3

            val web3j = Web3j.build(HttpService(clientAddress))
            val credential = Credentials.create("${inPrivateKey}")

            val params: MutableList<Type<*>> = ArrayList()
            params.add(Address(fromAddress))
            params.add(Address(toAddress))
            params.add(Uint256(Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()))

            // output parameters
            val returnTypes: List<TypeReference<*>> = Collections.emptyList<TypeReference<*>>()
            val function = Function(
                "transferFrom",
                params,
                returnTypes
            )

            val txData = FunctionEncoder.encode(function)

            val receiptProcessor: TransactionReceiptProcessor =
                PollingTransactionReceiptProcessor(web3j, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
            val manager: TransactionManager = RawTransactionManager(web3j, credential, chainId, receiptProcessor)
            val gasProvider: ContractGasProvider = DefaultGasProvider()

            val ethSendTransaction: EthSendTransaction = manager.sendEIP1559Transaction(
                chainId,
                BigInteger("100000000001"),
                BigInteger("100000000001"),
                gasProvider.getGasLimit("transfer"),
                "${tokenAddress}",
                txData,
                BigInteger.ZERO
            )

            val txHash = ethSendTransaction.getTransactionHash();

            return receiptProcessor.waitForTransactionReceipt(txHash)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return TransactionReceipt()
    }

    fun transferErc20Token(toAddress: String, amount: BigDecimal): TransactionReceipt? {
        try {
            val TX_END_CHECK_DURATION: Long = 5000
            val TX_END_CHECK_RETRY = 3

            val web3j = Web3j.build(HttpService(clientAddress))
            val credential = Credentials.create("${inPrivateKey}")

            val params: MutableList<Type<*>> = ArrayList()
            params.add(Address(toAddress))
            params.add(Uint256(Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()))

            // output parameters
            val returnTypes: List<TypeReference<*>> = Collections.emptyList<TypeReference<*>>()
            val function = Function(
                "transfer",
                params,
                returnTypes
            )

            val txData = FunctionEncoder.encode(function)

            val receiptProcessor: TransactionReceiptProcessor =
                PollingTransactionReceiptProcessor(web3j, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
            val manager: TransactionManager = RawTransactionManager(web3j, credential, chainId, receiptProcessor)
            val gasProvider: ContractGasProvider = DefaultGasProvider()

            val tokenSendTransaction: EthSendTransaction = manager.sendEIP1559Transaction(
                chainId,
                Convert.toWei(BigDecimal("101"), Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei(BigDecimal("101"), Convert.Unit.GWEI).toBigInteger(),
                gasProvider.getGasLimit("transfer"),
                "${tokenAddress}",
                txData,
                BigInteger.ZERO
            )

            val txHash = tokenSendTransaction.getTransactionHash();

            return receiptProcessor.waitForTransactionReceipt(txHash)
        } catch (e: Exception) {
            logger.warn("transferErc20Token error")
            logger.warn(e.toString())
        }

        return null
    }

    fun checkTx(hash: String, amount: BigDecimal): Boolean {
        val web3j = Web3j.build(HttpService(clientAddress))
        val receipt: EthGetTransactionReceipt = web3j.ethGetTransactionReceipt(hash).send()

        if (receipt.transactionReceipt.isPresent) {
            val transactionReceipt: TransactionReceipt = receipt.transactionReceipt.get()
            val isIncludedInBlock = (transactionReceipt.blockHash != null)
            val isSuccess = ("0x1" == transactionReceipt.status)

            if (isIncludedInBlock && isSuccess) {
                try {
                    val log = transactionReceipt.logs.get(0)
                    val eventValuesList: EventValues = Contract.staticExtractEventParameters(ERC20.APPROVAL_EVENT, log)
                    val approvalValue = eventValuesList.nonIndexedValues[0].value as BigInteger

                    val approvalAmount = Convert.fromWei(BigDecimal(approvalValue), Convert.Unit.ETHER)
                    if (amount.compareTo(approvalAmount) == 0) {
                        return true
                    } else {
                        logger.warn("checkTx error")
                        logger.warn("approvalAmount: ${approvalAmount}")
                        logger.warn("amount: ${amount}")
                    }
                } catch (e: Exception) {
                    logger.warn("checkTx error")
                    logger.warn(e.toString())
                }
            } else
            {
                logger.warn("checkTx error")
                logger.warn("isIncludedInBlock: ${isIncludedInBlock}")
                logger.warn("isSuccess: ${isSuccess}")
            }
        }

        return false
    }

    fun transfer(toAddress: String, amount: BigDecimal): TransactionReceipt? {
        try {
            val TX_END_CHECK_DURATION: Long = 5000
            val TX_END_CHECK_RETRY = 3

            val web3j = Web3j.build(HttpService(clientAddress))
            val credential = Credentials.create("${inPrivateKey}")

            val amountToSend = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger() // Convert 1 ETH to Wei

            // Define the gas parameters for the EIP-1559 transaction
            val gasLimit = BigInteger.valueOf(21000L) // Gas limit - 21000 is the gas limit for standard transactions
            val maxPriorityFeePerGas = BigInteger("100000000001")
            val maxFeePerGas = BigInteger("100000000001")

            // Create a transaction receipt processor
            val receiptProcessor: TransactionReceiptProcessor =
                PollingTransactionReceiptProcessor(web3j, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)

            // Create a transaction manager
            val manager: TransactionManager = RawTransactionManager(web3j, credential, chainId, receiptProcessor)

            // Send the EIP-1559 transaction
            val nonce = web3j.ethGetTransactionCount(credential.getAddress(), DefaultBlockParameterName.LATEST).send().transactionCount
            val wemixSendTransaction: EthSendTransaction = manager.sendEIP1559Transaction(
                chainId,
                maxPriorityFeePerGas,
                maxFeePerGas,
                gasLimit,
                toAddress,
                "",
                amountToSend
            )

            val txHash = wemixSendTransaction.getTransactionHash();

            return receiptProcessor.waitForTransactionReceipt(txHash)
        } catch (e: Exception) {
            logger.warn("transfer error")
            logger.warn(e.toString())
        }

        return null
    }

    fun getReceipt(hash: String): TransactionReceipt? {
        try {
            val web3j = Web3j.build(HttpService(clientAddress))

            val receipt: TransactionReceipt = web3j.ethGetTransactionReceipt(hash).sendAsync().get().transactionReceipt.get()

            return receipt
        } catch (e: Exception) {
            logger.warn("getReceipt error")
            logger.warn(e.toString())
        }

        return null
    }

    fun getErc20Balance(address: String): BigDecimal {
        try {
            val web3j = Web3j.build(HttpService(clientAddress))

            val params: MutableList<Type<*>> = ArrayList()
            params.add(Address(address))

            // output parameters
            val returnTypes: List<TypeReference<*>> = listOf<TypeReference<*>>(object : TypeReference<Uint256?>() {})
            val function = Function(
                "balanceOf",
                params,
                returnTypes
            )

            val txData = FunctionEncoder.encode(function)

            val ethCall = web3j.ethCall(
                org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                    walletInAddress,
                    tokenAddress,
                    txData
                ),
                DefaultBlockParameterName.LATEST
            ).send()

            val result = ethCall.value

            val results = FunctionReturnDecoder.decode(ethCall.value, function.outputParameters)
            val balance = results[0].value as BigInteger

            return Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
        } catch (e: Exception) {
            logger.warn("getErc20Balance error")
            logger.warn(e.toString())
        }

        return BigDecimal.ZERO
    }

    fun getExplorerUrl(transactionHash: String?): String? {
        return "${explorerAddress}/tx/${transactionHash}"
    }
}

fun TransactionReceipt.isSucceed(): Boolean {
    return "0x1" == this.status
}

fun TransactionReceipt.toFee(): BigDecimal {
    return Convert.fromWei(BigDecimal(this.gasUsed), Convert.Unit.GWEI) * Convert.fromWei(BigDecimal(BigInteger(this.effectiveGasPrice.removePrefix("0x"), 16)), Convert.Unit.GWEI)
}