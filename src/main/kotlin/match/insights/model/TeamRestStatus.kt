package match.insights.model

enum class TeamRestStatus(val status: String) {
    GOOD_REST("Good Rest"),
    MODERATE_CONGESTION("Moderate Congestion"),
    SEVERE_CONGESTION("Severe Congestion"),
    UNKNOWN_STATE("Unknown"), ;
}