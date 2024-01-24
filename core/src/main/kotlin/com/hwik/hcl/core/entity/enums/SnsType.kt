package com.hwik.hcl.core.entity.enums

enum class SnsType(val value: String) {
    None(""),
    Twitter("TWI"),
    Telegram("TEL");

    companion object {
        fun fromString(value: String): SnsType {
            val snsType = SnsType.values().firstOrNull { it.value == value }
            if (snsType == null) {
                return None
            }

            return snsType
        }
    }

}