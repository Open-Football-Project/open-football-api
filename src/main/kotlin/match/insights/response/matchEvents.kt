package match.insights.response

import match.insights.clientData.Event

data class MatchEvent(
    val timeElapsed: Int,
    val timeExtra: Int?,
    val teamName: String,
    val teamLogo: String?,
    val playerName: String?,
    val eventType: String,
    val eventDetails: String
) {
    companion object {
        fun fromEvent(event: Event) = MatchEvent(
            timeElapsed = event.time.elapsed,
            timeExtra = event.time.extra,
            teamName = event.team.name,
            teamLogo = event.team.logo,
            playerName = event.player?.name,
            eventType = event.type,
            eventDetails = event.detail.orEmpty()
        )
    }
}