package com.hwik.hcl.user.controller

import com.hwik.hcl.user.service.ImageService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/images")
class ImagesController(
    private val imageService: ImageService,
    private val resourceLoader: ResourceLoader
) {
    @GetMapping("/{id}")
    fun getImage(@PathVariable id: Long, response: HttpServletResponse): ResponseEntity<ByteArray> {
        try {
            val image = imageService.getImageById(id) ?: return ResponseEntity.notFound().build()

            val path = Paths.get(image.originalName)
            if (Files.exists(path)) {
                return ResponseEntity.ok().header("Content-Type", image.mimeType).body(Files.readAllBytes(path))
            }

            val resource : Resource = resourceLoader.getResource("classpath:static/assets/images/no_image.jpeg")
            return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(resource.contentAsByteArray)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return ResponseEntity.badRequest().build()
    }
}