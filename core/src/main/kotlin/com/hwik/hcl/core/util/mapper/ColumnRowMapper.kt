package com.hwik.hcl.core.util.mapper

import org.springframework.jdbc.core.RowMapper
import kotlin.reflect.KClass


class ColumnRowMapper(
) {
    companion object {
        fun <T : Any> getMapper(kCalss: KClass<T>): RowMapper<T> {
            return ColumnRowMapper1<T>(kCalss)
        }

        fun <T : Any, T1 : Any, T2 : Any> getMapper(kClass1: KClass<T1>, kClass2: KClass<T2>, kClass: KClass<T>, operation: (T1, T2) -> T, splitOn: List<String>): RowMapper<T> {
            return ColumnRowMapper2(kClass1, kClass2, kClass, operation, splitOn)
        }

        fun <T : Any, T1 : Any, T2 : Any> getMapper(kClass1: KClass<T1>, kClass2: KClass<T2>, kClass: KClass<T>, operation: (T1, T2) -> T): RowMapper<T> {
            val splitOn : MutableList<String> = mutableListOf<String>()
            splitOn.add(JU.IdColumn(kClass1))
            splitOn.add(JU.IdColumn(kClass2))

            return ColumnRowMapper2(kClass1, kClass2, kClass, operation, splitOn)
        }

        fun <T : Any, T1 : Any, T2 : Any, T3 : Any> getMapper(
            kClass1: KClass<T1>,
            kClass2: KClass<T2>,
            kClass3: KClass<T3>,
            kClass: KClass<T>,
            operation: (T1, T2, T3) -> T,
            splitOn: List<String>
        ): RowMapper<T> {
            return ColumnRowMapper3(kClass1, kClass2, kClass3, kClass, operation, splitOn)
        }

        fun <T : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any> getMapper(
            kClass1: KClass<T1>,
            kClass2: KClass<T2>,
            kClass3: KClass<T3>,
            kClass4: KClass<T4>,
            kClass: KClass<T>,
            operation: (T1, T2, T3, T4) -> T,
            splitOn: List<String>
        ): RowMapper<T> {
            return ColumnRowMapper4(kClass1, kClass2, kClass3, kClass4, kClass, operation, splitOn)
        }

        fun <T : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any> getMapper(
            kClass1: KClass<T1>,
            kClass2: KClass<T2>,
            kClass3: KClass<T3>,
            kClass4: KClass<T4>,
            kClass: KClass<T>,
            operation: (T1, T2, T3, T4) -> T,
        ): RowMapper<T> {
            val splitOn : MutableList<String> = mutableListOf<String>()
            splitOn.add(JU.IdColumn(kClass1))
            splitOn.add(JU.IdColumn(kClass2))
            splitOn.add(JU.IdColumn(kClass3))
            splitOn.add(JU.IdColumn(kClass4))

            return ColumnRowMapper4(kClass1, kClass2, kClass3, kClass4, kClass, operation, splitOn)
        }

        fun <T : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> getMapper(
            kClass1: KClass<T1>,
            kClass2: KClass<T2>,
            kClass3: KClass<T3>,
            kClass4: KClass<T4>,
            kClass5: KClass<T5>,
            kClass: KClass<T>,
            operation: (T1, T2, T3, T4, T5) -> T,
            splitOn: List<String>
        ): RowMapper<T> {
            return ColumnRowMapper5(kClass1, kClass2, kClass3, kClass4, kClass5, kClass, operation, splitOn)
        }
    }
}