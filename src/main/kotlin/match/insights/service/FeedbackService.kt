package match.insights.service

import match.insights.props.EmailFeedbackProps
import match.insights.request.FeedbackRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class FeedbackService(
    private val mailSender: JavaMailSender,
    private val props: EmailFeedbackProps
) {

    fun handleFeedback(request: FeedbackRequest) {
        val message = SimpleMailMessage().apply {
            setFrom(props.from)
            setTo(*props.recipients.toTypedArray())
            subject = "New Futballero Feedback"
            text = buildEmailBody(request)
        }
        mailSender.send(message)
    }

    private fun buildEmailBody(request: FeedbackRequest): String = """
        New beta feedback submission from Futballero.

        Favorite Team : ${request.favoriteTeam}
        League        : ${request.league}
        Liked         : ${request.liked}
        Improvements  : ${request.improvements}
        Android Beta  : ${if (request.wantsAndroidBeta) "Yes" else "No"}
        Google Email  : ${request.googleEmail ?: "—"}
    """.trimIndent()
}
