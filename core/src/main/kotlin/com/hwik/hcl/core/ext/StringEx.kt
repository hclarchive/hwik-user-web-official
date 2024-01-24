package com.hwik.hcl.core.ext

fun String.toUtf8HexString(): String {
    return this.toByteArray(Charsets.UTF_8).joinToString("") {
        "%02x".format(it)
    }
}
