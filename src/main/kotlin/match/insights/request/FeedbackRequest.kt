package match.insights.request

class FeedbackRequest(
    val favoriteTeam: String,
    val league: String,
    val liked: String,
    val improvements: String,
    val wantsAndroidBeta: Boolean,
    val googleEmail: String?
)
