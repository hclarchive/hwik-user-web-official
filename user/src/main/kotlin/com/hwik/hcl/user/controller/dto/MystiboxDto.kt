package com.hwik.hcl.user.controller.dto

import com.hwik.hcl.core.entity.MystiboxEntity
import com.hwik.hcl.core.entity.enums.StatusType
import org.modelmapper.ModelMapper
import java.math.BigDecimal
import java.time.Instant

data class MystiboxDto(
    var id: Long? = null,
    var title: String? = null,
    var entryRequired: BigDecimal? = null,
    var curEntryCount: Int? = null,
    var reward: Int? = null,
    var status: StatusType? = null,
    var imagePath: String? = null,
    var startedAt: Instant? = null,
    var endedAt: Instant? = null,
    var winnerAddress: String? = null,
) {
}

fun MystiboxEntity.toDto(modelMapper: ModelMapper, contextPath: String): MystiboxDto {
    val dto = modelMapper.map(this, MystiboxDto::class.java)

    val tmpPathList: MutableList<String> = if (contextPath.isNullOrEmpty()) {
        mutableListOf()
    } else {
        mutableListOf(contextPath)
    }

    if (this.image?.id != null) {
        tmpPathList.add("/images/${this.image?.id}")
    } else {
        tmpPathList.add("/assets/images/no_image.jpeg")
    }

    dto.imagePath = tmpPathList.joinToString("/")

    return dto
}


