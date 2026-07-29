package org.footballproject.internaldata

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.footballproject.errors.ApiResourceNotFoundException
import org.footballproject.errors.ErrorMessage
import org.footballproject.model.ContentKey
import org.footballproject.model.VideoContent
import org.footballproject.model.VideoContent.Companion.VIDEO_CONTENT_DATE_FORMAT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class VideoContentManager(

    @Value("\${videocontent.file}")
    private val filePath: String,

    @Value("\${videocontent.agedays}")
    private val ageDays: Int
) {

    fun loadContent(): Map<String, Set<VideoContent>> {
        val file = File(filePath)
        if (!file.exists()) throw ApiResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND)
        return jacksonObjectMapper().readValue(file, object : TypeReference<Map<String, Set<VideoContent>>>() {})
    }

    fun getVideos(contentKey: ContentKey, id: Int): Set<VideoContent> {
        val videos = loadContent()
        val cutoff = LocalDate.now().minusDays(ageDays.toLong())
        val formatter = DateTimeFormatter.ofPattern(VIDEO_CONTENT_DATE_FORMAT)

        return videos[getContentKey(contentKey, id)]
            ?.filter { LocalDate.parse(it.uploadDate, formatter).isAfter(cutoff) }
            ?.toSet()
            ?: emptySet()
    }

    fun newContent(contentKey: ContentKey, id: Int, newVideos: Set<VideoContent>): Set<VideoContent> {
        val fullKey = getContentKey(contentKey, id)
        val content = loadContent()

        val existing = content[fullKey] ?: emptySet()
        val updated = existing + newVideos

        val updatedContent = content + (fullKey to updated)

        jacksonObjectMapper().writeValue(File(filePath), updatedContent)

        return updated
    }

    private fun getContentKey(contentKey: ContentKey, id: Int) = "${contentKey.value}_${id}"
}
