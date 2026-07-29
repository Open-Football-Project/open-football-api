package match.insights.seasons

import match.insights.client.ApiSportsClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class Seasons(
    private val apiSportsClient: ApiSportsClient
) {
    val currentYear: Int = LocalDate.now().year
    private var currentSeasons: Map<Int, Int> = emptyMap()

    @Scheduled(cron = "0 0 3 * * *")
    fun refresh() {
        currentSeasons = leaguesCurrentSeasons()
    }

    fun leagueCurrentSeason(leagueId: Int? = null): Int =
        leagueId?.let {
            if (currentSeasons.isEmpty()) leaguesCurrentSeasons()[it]
            else currentSeasons[it]
        } ?: currentYear

    fun leaguesCurrentSeasons(): Map<Int, Int> =
        apiSportsClient.fetchAllLeagues("/leagues?current=true").mapNotNull { leagueDto ->
            val currentSeason = leagueDto.seasons?.firstOrNull { it.current }
            if (currentSeason != null) {
                leagueDto.league.id to currentSeason.year
            } else {
                null
            }
        }.toMap()
}