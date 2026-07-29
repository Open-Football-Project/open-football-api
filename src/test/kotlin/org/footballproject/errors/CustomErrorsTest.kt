package org.footballproject.errors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomErrorsTest {

    @Test
    fun `ApiFailedException forwards the ErrorMessage text as its message`() {
        val exception = ApiFailedException(ErrorMessage.CLIENT_FAILED)

        assertThat(exception.message).isEqualTo("Client Failed.")
    }

    @Test
    fun `ApiRssFeedException forwards the ErrorMessage text as its message`() {
        val exception = ApiRssFeedException(ErrorMessage.RSS_CLIENT_FAILED)

        assertThat(exception.message).isEqualTo("RSS news client Failed.")
    }

    @Test
    fun `ApiResourceNotFoundException forwards the ErrorMessage text as its message`() {
        val exception = ApiResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND)

        assertThat(exception.message).isEqualTo("Resource not found")
    }

    @Test
    fun `ApiResourceAlreadyExists forwards the ErrorMessage text as its message`() {
        val exception = ApiResourceAlreadyExists(ErrorMessage.RESOURCE_EXIST)

        assertThat(exception.message).isEqualTo("Resource already exist")
    }
}
