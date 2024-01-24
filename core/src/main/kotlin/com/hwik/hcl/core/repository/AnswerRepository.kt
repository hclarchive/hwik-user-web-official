package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.AnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository: JpaRepository<AnswerEntity, Long> {
}