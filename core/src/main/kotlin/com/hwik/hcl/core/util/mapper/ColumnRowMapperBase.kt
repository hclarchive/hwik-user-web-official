package com.hwik.hcl.core.util.mapper

import jakarta.persistence.Column
import jakarta.persistence.JoinColumn
import org.springframework.jdbc.core.RowMapper
import java.math.BigDecimal
import java.sql.*
import java.time.Instant
import java.time.LocalDateTime
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField


open class ColumnRowMapperBase<T : Any>(
) : RowMapper<T> {
    protected var splitOn: MutableList<String> = mutableListOf()
    protected var entityList: MutableList<Any> = mutableListOf()
    protected var operation: (() -> T)? = null

    companion object {
        val logger = org.slf4j.LoggerFactory.getLogger(ColumnRowMapperBase::class.java)
        val columnCache = HashMap<String, MutableMap<String, KProperty1<*, *>>>()
    }

    override fun mapRow(rs: ResultSet, rowNum: Int): T? {
        for (i in 0 .. entityList.size - 1) {
            entityList[i] = entityList[i]::class.primaryConstructor?.call() as Any
        }

        val metaData: ResultSetMetaData = rs.metaData;

        var currentIdx = -1;

        for (i in 1..metaData.columnCount) {
            val columnName = metaData.getColumnLabel(i)
            val splitName = splitOn[min(currentIdx + 1, splitOn.size - 1)]
            if (columnName == splitName) {
                currentIdx = currentIdx + 1;
            }

            var tmpIdx = currentIdx;
            var targetEntity: Any = entityList.last()
            if (tmpIdx >= 0 && tmpIdx < entityList.size - 1) {
                targetEntity = entityList[tmpIdx]
            }

            mapData(rs, i, columnName, targetEntity)
        }

        if (operation != null)
        {
            return operation!!()
        }

        return entityList.last() as T
    }

    protected fun mapData(rs: ResultSet, idx: Int, columnName: String, entity: Any) {
        val propertyMap = getPropertyMap(entity::class)

        if (propertyMap.containsKey(columnName)) {
            val value: Any? = rs.getObject(idx)
            if (value != null) {
                val property = propertyMap[columnName]
                val kProperty = property as KMutableProperty<*>

                when (kProperty.returnType.classifier) {
                    Instant::class -> {
                        kProperty?.setter?.call(entity, rs.getTimestamp(idx).toInstant())
                    }
                    Boolean::class -> {
                        kProperty?.setter?.call(entity, rs.getBoolean(idx))
                    }
                    String::class -> {
                        kProperty?.setter?.call(entity, rs.getString(idx))
                    }
                    Short::class -> {
                        kProperty?.setter?.call(entity, rs.getShort(idx))
                    }
                    Int::class -> {
                        kProperty?.setter?.call(entity, rs.getInt(idx))
                    }
                    Long::class -> {
                        kProperty?.setter?.call(entity, rs.getLong(idx))
                    }
                    Float::class -> {
                        kProperty?.setter?.call(entity, rs.getFloat(idx))
                    }
                    Double::class -> {
                        kProperty?.setter?.call(entity, rs.getDouble(idx))
                    }
                    BigDecimal::class -> {
                        kProperty?.setter?.call(entity, rs.getBigDecimal(idx))
                    }
                    Date::class -> {
                        kProperty?.setter?.call(entity, rs.getDate(idx))
                    }
                    Time::class -> {
                        kProperty?.setter?.call(entity, rs.getTime(idx))
                    }
                    Timestamp::class -> {
                        kProperty?.setter?.call(entity, rs.getTimestamp(idx))
                    }
                    LocalDateTime::class -> {
                        kProperty?.setter?.call(entity, rs.getTimestamp(idx).toLocalDateTime())
                    }
                    else -> {
                        logger.info("not bind Data ${columnName} : ${kProperty.returnType.classifier}")
                    }
                }
            }
        }
    }

    protected fun getPropertyMap(kClass: KClass<*>): MutableMap<String, KProperty1<*, *>> {
        var key: String = kClass.qualifiedName ?: kClass.simpleName ?: kClass.toString()

        if (columnCache.containsKey(key)) {
            return columnCache.get(key)!!
        } else {
            val propertyMap: MutableMap<String, KProperty1<*, *>> = mutableMapOf()
            columnCache[key] = propertyMap

            for (property in kClass.memberProperties) {
                val fieldName = property.name
                val colAnnotation: Column? = property.javaField?.annotations?.find { it.annotationClass == Column::class } as Column?
                val joinColAnnotation: JoinColumn? = property.javaField?.annotations?.find { it.annotationClass == JoinColumn::class } as JoinColumn?

                val columnName: String = colAnnotation?.name ?: joinColAnnotation?.name ?: fieldName

                propertyMap[columnName] = property
            }

            return propertyMap
        }

        return mutableMapOf()
    }
}