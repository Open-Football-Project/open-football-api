package org.footballproject.response

import org.footballproject.clientData.Goal
import org.footballproject.clientData.League
import org.footballproject.clientData.MatchResponse
import org.footballproject.clientData.Score
import org.footballproject.clientData.Team
import org.footballproject.clientData.Venue
import org.footballproject.model.VideoContent

data class MatchDetails(
    val id: Int,
    val date: String,
    val league: League,
    val venue: Venue,
    val homeTeam: Team,
    val awayTeam: Team,
    val goals: Goal,
    val score: Score,
    val statusShort: String,
    val statusLong: String,
    val isLiveNow: Boolean,
    val videos: Set<VideoContent> = emptySet()
) {
    companion object {
        fun fromResponseData(
            matchResponse: MatchResponse,
            liveIds: Set<Int>,
            videos: Set<VideoContent> = emptySet()
        ): MatchDetails {
            return MatchDetails(
                matchResponse.fixture.id,
                matchResponse.fixture.date,
                matchResponse.league,
                matchResponse.fixture.venue ?: Venue(),
                matchResponse.teams.home ?: Team(),
                matchResponse.teams.away ?: Team(),
                matchResponse.goals ?: Goal(),
                matchResponse.score ?: Score(),
                statusShort = matchResponse.fixture.status?.short ?: "UN",
                statusLong = matchResponse.fixture.status?.long ?: "Unknown",
                isLiveNow = liveIds.contains(matchResponse.fixture.id),
                videos = videos
            )
        }
    }
}