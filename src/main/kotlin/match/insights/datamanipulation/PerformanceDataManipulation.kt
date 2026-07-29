package match.insights.datamanipulation

import match.insights.clientData.MatchResponse
import match.insights.model.Performance
import org.springframework.stereotype.Component

@Component
class PerformanceDataManipulation {

    fun calculateScorePerformance(teamId: Int, lastFiveMatches: List<MatchResponse>): String {
        if (lastFiveMatches.isEmpty()) return Performance.NO_DATA.value

        val points = lastFiveMatches.map { match ->
            val isHome = match.teams.home?.id == teamId

            val teamScore: Int = (if (isHome) match.goals?.home else match.goals?.away) ?: 0
            val opponentScore: Int = (if (isHome) match.goals?.away else match.goals?.home) ?: 0

            when {
                teamScore > opponentScore -> 1.0
                teamScore == opponentScore -> 0.5
                else -> 0.0
            }
        }
        val avg = points.average()
        return if (avg >= 0.5) Performance.GOOD.value else Performance.POOR.value
    }
}