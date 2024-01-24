package com.hwik.hcl.core.entity.enums

enum class Web3Uuid(val value: String) {
    platform("6ffe88fe-c650-4a4c-adcd-e3b351e2abb3"),
    platform_wallet("efbb6731-186d-4847-abb3-3a2f56e8619b"),
    community("2b0976ae-1173-40c9-9274-5dacc669c678"),
    community_twitter("348d0fee-88e6-4521-b3ea-fbfe5b479f49"),
    community_telegram_gb("42fda8d2-58a5-45bf-aa48-aa3abb2fe79d"),
    community_telegram_kr("9e698b93-6790-4ba5-9065-330aa471194e");

    companion object {
        fun fromString(value: String): Web3Uuid? {
            return values().firstOrNull { it.value == value }
        }
    }

    override fun toString(): String {
        return value
    }
}