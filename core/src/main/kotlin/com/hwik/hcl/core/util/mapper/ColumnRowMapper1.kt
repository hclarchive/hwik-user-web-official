package com.hwik.hcl.core.util.mapper

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


class ColumnRowMapper1<T : Any>(
) : ColumnRowMapperBase<T>() {
    constructor(kClass: KClass<T>) : this() {
        this.splitOn.add(JU.IdColumn((kClass)))

        val entity: T = kClass.primaryConstructor?.call() as T

        entityList.add(entity)

    }

}