package com.hwik.hcl.user.controller.dto

import com.hwik.hcl.core.entity.MystiboxEntity
import com.hwik.hcl.core.entity.Web3Entity
import org.modelmapper.ModelMapper
import java.time.Instant

data class Web3Dto(
    var id: Int? = null,
    var title: String = "",
    var totalReward: Double = 0.0,
    var children: List<Web3Dto>? = null,
    var reward: Double = 0.0,
    var memo: String = "",
    var uuid: String?  = "",
    val snsType: String? = "",
    val extraData1: String? = "",
    var imagePath: String? = null,
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null,
    var provide: Double = 0.0,
    var participants: Long = 0,
    var remain: Double = 0.0,
    var web3Count: Int = 0,
    var currentWeb3: Int = 0,
    var isComplete: Boolean = false,
    var isClaim: Boolean = false
)

fun Web3Entity.toDto(modelMapper: ModelMapper, contextPath: String): Web3Dto {
    val dto = modelMapper.map(this, Web3Dto::class.java)

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

    dto.children = this.children?.map {
        it.toDto(modelMapper, contextPath)
    }

    return dto
}