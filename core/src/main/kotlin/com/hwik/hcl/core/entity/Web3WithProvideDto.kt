package com.hwik.hcl.core.entity

import com.hwik.hcl.core.entity.enums.SnsType
import com.hwik.hcl.core.entity.enums.Web3Uuid
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import java.math.BigDecimal

data class Web3WithProvideDto(
    var id: Long? = null,
    var parentId: Long? = null,
    var title: String? = null,
    var imageId: Long? = null,
    var totalReward: BigDecimal? = null,
    var reward: BigDecimal? = null,
    var memo: String? = null,
    var uuid: String? = null,
    var snsType: String? = null,
    var extraData1: String? = null,
    var extraData2: String? = null,
    var participants: Long? = null,
    var provide: BigDecimal? = null,
    var remain: BigDecimal? = null,
) {
    companion object {
        const val SqlResultSetMappingName = "Web3WithProvideDtoMapping"

        fun Web3WithProvideDto.toEntity() : Web3Entity {
            val dto = this
            val web3 = Web3Entity().apply {
                this.id = dto.id
                this.title = dto.title
                this.totalReward = dto.totalReward
                this.reward = dto.reward!!
                this.memo = dto.memo
                this.uuid = Web3Uuid.fromString(dto.uuid!!)
                this.snsType = SnsType.fromString(dto.snsType!!)
                this.extraData1 = dto.extraData1
                this.extraData2 = dto.extraData2
                this.children = mutableListOf()
                this.participants = dto.participants ?: 0
                this.provide = dto.provide ?: BigDecimal.ZERO
                this.remain = dto.remain ?: dto.totalReward
            }

            return web3
        }
    }
}
