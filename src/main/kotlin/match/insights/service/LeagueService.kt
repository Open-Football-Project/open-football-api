package match.insights.service

import match.insights.apidata.LeaguesData
import match.insights.apidata.LiveData
import match.insights.datamanipulation.FixturesManipulation
import match.insights.datamanipulation.LeagueDataManipulation
import match.insights.internaldata.VideoContentManager
import match.insights.model.ContentKey
import match.insights.model.VideoContent
import match.insights.response.BasicTeamInfo
import match.insights.response.LeagueFixture
import match.insights.response.LeagueInfo
import match.insights.response.LeagueRankings
import match.insights.response.LeaguesGroups
import org.springframework.stereotype.Service
import java.time.Year


@Service
class LeagueService(
    private val apidata: LeaguesData,
    private val liveData: LiveData,
    private val leagueDataManipulation: LeagueDataManipulation,
    private val fixturesManipulation: FixturesManipulation,
    private val videoContentManager: VideoContentManager
) {
    fun leagueInfo(leagueId: Int): LeagueInfo =
        leagueDataManipulation.extractLeaguesInfo(apidata.leagueStandings(leagueId)).copy(
            videos = videoContentManager.getVideos(ContentKey.LEAGUE, leagueId)
        )


    fun addVideoContent(leagueId: Int, videos: Set<VideoContent>): Set<VideoContent> =
        videoContentManager.newContent(ContentKey.LEAGUE, leagueId, videos)

    fun allLeagues(): LeaguesGroups =
        leagueDataManipulation.groupLeagues(apidata.leagues())

    fun leagueSeasonFixture(leagueId: Int): LeagueFixture {
        val liveMatchesIds = liveData.allLiveMatches().map { it.fixture.id }.toSet()
        return fixturesManipulation.toLeagueFixture(apidata.leagueSeasonMatches(leagueId), liveMatchesIds)
    }

    fun leagueRankings(leagueId: Int): LeagueRankings {
        val players = apidata.allLeaguePlayers(leagueId).values.flatten()
        return leagueDataManipulation.topNRankings(players)
    }

    fun currentYearTeams(leagueId: Int): List<BasicTeamInfo> =
        apidata.leagueTeams(leagueId, Year.now().value)
            .map {
                BasicTeamInfo(
                    it.team.id,
                    it.team.name
                )
            }
}
