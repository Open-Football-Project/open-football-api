package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.footballproject.props.EmailFeedbackProps
import org.footballproject.request.FeedbackRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class FeedbackServiceTest {

    private val mailSender: JavaMailSender = mockk()
    private val props = EmailFeedbackProps(
        from = "sender@footballproject.org",
        recipients = setOf("owner@gmail.com", "contact@footballproject.org")
    )

    private val underTest = FeedbackService(mailSender, props)

    @Test
    fun shouldSendEmailWithAllRecipientsOnFeedbackSubmission() {
        val captured = slot<SimpleMailMessage>()
        every { mailSender.send(capture(captured)) } just runs

        underTest.handleFeedback(aFeedbackRequest())

        verify { mailSender.send(any<SimpleMailMessage>()) }
        assertThat(captured.isCaptured).isTrue()
        assertThat(captured.captured.to).containsExactlyInAnyOrder("owner@gmail.com", "contact@footballproject.org")
    }

    @Test
    fun shouldSendEmailFromConfiguredSender() {
        val captured = slot<SimpleMailMessage>()
        every { mailSender.send(capture(captured)) } just runs

        underTest.handleFeedback(aFeedbackRequest())

        assertThat(captured.captured.from).isEqualTo("sender@footballproject.org")
    }


    @Test
    fun shouldIncludeAllFieldsInEmailBody() {
        val captured = slot<SimpleMailMessage>()
        every { mailSender.send(capture(captured)) } just runs

        underTest.handleFeedback(
            aFeedbackRequest(
                favoriteTeam = "Arsenal",
                league = "Premier League",
                liked = "Live stats",
                improvements = "Faster load",
                wantsAndroidBeta = true,
                googleEmail = "fan@gmail.com"
            )
        )

        val body = captured.captured.text!!
        assertThat(body).contains("Arsenal")
        assertThat(body).contains("Premier League")
        assertThat(body).contains("Live stats")
        assertThat(body).contains("Faster load")
        assertThat(body).contains("Yes")
        assertThat(body).contains("fan@gmail.com")
    }

    @Test
    fun shouldShowDashWhenGoogleEmailIsAbsent() {
        val captured = slot<SimpleMailMessage>()
        every { mailSender.send(capture(captured)) } just runs

        underTest.handleFeedback(aFeedbackRequest(wantsAndroidBeta = false, googleEmail = null))

        assertThat(captured.captured.text).contains("No")
        assertThat(captured.captured.text).contains("—")
    }

    private fun aFeedbackRequest(
        favoriteTeam: String = "Barcelona",
        league: String = "La Liga",
        liked: String = "Stats",
        improvements: String = "UI",
        wantsAndroidBeta: Boolean = false,
        googleEmail: String? = null
    ) = FeedbackRequest(
        favoriteTeam = favoriteTeam,
        league = league,
        liked = liked,
        improvements = improvements,
        wantsAndroidBeta = wantsAndroidBeta,
        googleEmail = googleEmail
    )
}
