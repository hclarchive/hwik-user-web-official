package com.hwik.hcl.user.util

import java.math.BigDecimal

class BigDecimalEx {
    fun Iterable<BigDecimal>.sum(): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO
        for (element in this) {
            sum += element
        }
        return sum
    }
}