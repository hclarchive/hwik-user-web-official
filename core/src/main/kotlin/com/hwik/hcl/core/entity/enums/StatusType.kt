package com.hwik.hcl.core.entity.enums

enum class StatusType(val value: String) {
    Init("INI"),
    Waiting("WAIT"),
    Ing("ING"),
    Pending("PEND"),
    Success("SUC"),
    Fail("FAIL"),
    Ended("END");

    companion object {
        fun fromString(value: String): Web3Uuid? {
            return Web3Uuid.values().firstOrNull { it.value == value }
        }

        fun toListAll() : List<StatusType> {
            return listOf(StatusType.Init, StatusType.Waiting, StatusType.Ing, StatusType.Pending, StatusType.Success, StatusType.Fail, StatusType.Ended)
        }
    }

}