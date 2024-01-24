package com.hwik.hcl.core.util.mapper

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class JU {
    companion object {

        fun TableName(obj: KClass<*>): String {
            val entityAnnotation = obj.findAnnotation<Table>() ?: return ""
            return if (entityAnnotation.name.isNotBlank()) entityAnnotation.name else obj.simpleName ?: ""
        }

        fun IdColumn(kClass: KClass<*>): String {
            for (f in kClass.memberProperties) {
                val id: Id? = f.javaField?.annotations?.find { it.annotationClass == Id::class } as Id?
                if (id != null) {
                    val fieldName = f.name
                    val annotation: Column? = f.javaField?.annotations?.find { it.annotationClass == Column::class } as Column?
                    return annotation?.name ?: fieldName
                }
            }

            return ""
        }

        fun ColumnName(obj: KClass<*>): String {
            val entityAnnotation = obj.findAnnotation<Entity>() ?: return ""
            return if (entityAnnotation.name.isNotBlank()) entityAnnotation.name else obj.simpleName ?: ""
        }
    }
}