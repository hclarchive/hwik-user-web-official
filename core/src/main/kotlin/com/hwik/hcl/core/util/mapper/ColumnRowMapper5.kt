package com.hwik.hcl.core.util.mapper

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


class ColumnRowMapper5<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T : Any>(
) : ColumnRowMapperBase<T>() {

    constructor(
        kClass1: KClass<T1>,
        kClass2: KClass<T2>,
        kClass3: KClass<T3>,
        kClass4: KClass<T4>,
        kClass5: KClass<T5>,
        kClass: KClass<T>,
        operation: (T1, T2, T3, T4, T5) -> T,
        splitOn: List<String>
    ) : this() {
        this.splitOn.add(JU.IdColumn((kClass)))
        this.splitOn.addAll(splitOn)

        val entity: T = kClass.primaryConstructor?.call() as T
        var entity1: T1 = kClass1.primaryConstructor?.call() as T1
        var entity2: T2 = kClass2.primaryConstructor?.call() as T2
        var entity3: T3 = kClass3.primaryConstructor?.call() as T3
        var entity4: T4 = kClass4.primaryConstructor?.call() as T4
        var entity5: T5 = kClass5.primaryConstructor?.call() as T5

        entityList.add(entity1)
        entityList.add(entity2)
        entityList.add(entity3)
        entityList.add(entity4)
        entityList.add(entity5)
        entityList.add(entity)

        this.operation = { operation(entityList[0] as T1, entityList[1] as T2, entityList[2] as T3, entityList[3] as T4, entityList[4] as T5) }
    }
}