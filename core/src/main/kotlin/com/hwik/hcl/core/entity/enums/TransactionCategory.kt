package com.hwik.hcl.core.entity.enums

enum class TransactionCategory(val value: Int) {
    Web3(0),
    Quiz(1),
    MystiBoxApply(2),
    MystiBoxReward(3),
    MystiBoxBurn(4);

    companion object {
        fun toListAll() : List<TransactionCategory> {
            return listOf(TransactionCategory.Web3, TransactionCategory.Quiz, TransactionCategory.MystiBoxApply, TransactionCategory.MystiBoxReward, TransactionCategory.MystiBoxBurn)
        }

        fun toListForUser() : List<TransactionCategory> {
            return listOf(TransactionCategory.Web3, TransactionCategory.Quiz, TransactionCategory.MystiBoxApply, TransactionCategory.MystiBoxReward)
        }
    }
}