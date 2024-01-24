package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.ImageEntity
import com.hwik.hcl.core.repository.ImageRepository
import org.springframework.stereotype.Service

@Service
class ImageService(
    val imageRepository: ImageRepository
) {
    fun getImageById(id: Long): ImageEntity? {
        return imageRepository.findById(id).map { it }.orElse(null)
    }
}