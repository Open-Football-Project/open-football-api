package match.insights.response

import match.insights.clientData.CoachResponse
import match.insights.clientData.PlayerResponse
import match.insights.clientData.TeamResponse
import match.insights.model.VideoContent

data class TeamDetails(
    val teamName: String,
    val teamLogo: String,
    val teamCountry: String,
    val teamFounded: Int,
    val venueName: String,
    val venueCity: String,
    val venueCapacity: Int,
    val coachName: String,
    val coachAge: Int,
    val videos: Set<VideoContent>
) {
    companion object {

        fun fromClientResponse(
            coachResponse: CoachResponse?,
            teamResponse: TeamResponse?,
            videos: Set<VideoContent>
        ): TeamDetails {
            return TeamDetails(
                teamResponse?.team?.name ?: "Unknown Team",
                teamResponse?.team?.logo ?: "",
                teamResponse?.team?.country ?: "Unknown Country",
                teamResponse?.team?.founded ?: -1,
                teamResponse?.venue?.name ?: "Unknown Venue",
                teamResponse?.venue?.city ?: "Unknown City",
                teamResponse?.venue?.capacity ?: -1,
                coachResponse?.name ?: "Unknown Coach",
                coachResponse?.age ?: -1,
                videos = videos
            )
        }
    }
}


data class PlayerSummary(
    val name: String,
    val age: Int,
    val height: String,
    val weight: String,
    val position: String,
    val goals: Int,
    val yellowCards: Int,
    val redCards: Int,
    val penaltiesSaved: Int,
    val penaltiesScored: Int
) {
    companion object {
        fun fromResponse(playerResponse: PlayerResponse): PlayerSummary {
            val stats = playerResponse.statistics.firstOrNull()
            val position = stats?.games?.position

            return PlayerSummary(
                name = playerResponse.player.name,
                age = playerResponse.player.age ?: -1,
                height = playerResponse.player.height ?: "Unknown",
                weight = playerResponse.player.weight ?: "Unknown",
                position = position ?: "Unknown",
                goals = stats?.goals?.total ?: 0,
                yellowCards = stats?.cards?.yellow ?: 0,
                redCards = stats?.cards?.red ?: 0,
                penaltiesSaved = stats?.penalty?.saved ?: 0,
                penaltiesScored = stats?.penalty?.scored ?: 0,
            )
        }
    }
}


