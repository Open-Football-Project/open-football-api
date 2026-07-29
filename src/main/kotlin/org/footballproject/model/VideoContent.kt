package org.footballproject.model

import org.footballproject.errors.ApiFailedException
import org.footballproject.errors.ErrorMessage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

data class VideoContent(val url: String, val esLabel: String, val enLabel: String, val uploadDate: String, val thumbnailUrl: String? = null) {
    fun selfValidate() {
        val formatter = DateTimeFormatter.ofPattern(VIDEO_CONTENT_DATE_FORMAT)
            .withResolverStyle(ResolverStyle.STRICT)

        try {
            LocalDate.parse(uploadDate, formatter)
        } catch (e: Exception) {
            throw ApiFailedException(ErrorMessage.INVALID_DATE)
        }
    }

    companion object {
        val VIDEO_CONTENT_DATE_FORMAT = "dd/MM/uuuu"
    }
}
