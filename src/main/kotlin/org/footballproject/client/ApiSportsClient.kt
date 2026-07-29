package org.footballproject.client

import org.footballproject.clientData.ApiPagingResponse
import org.footballproject.clientData.MatchResponse
import org.footballproject.clientData.ApiResponse
import org.footballproject.clientData.CoachResponse
import org.footballproject.clientData.Event
import org.footballproject.clientData.StandingResponse
import org.footballproject.clientData.FixtureOdds
import org.footballproject.clientData.LeagueAndCountry
import org.footballproject.clientData.LeagueWithStandings
import org.footballproject.clientData.LineupTeam
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.clientData.LiveTeamStats
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.PlayerTransfer
import org.footballproject.clientData.PlayerTrophy
import org.footballproject.clientData.RankingPlayerStats
import org.footballproject.clientData.SquadPlayer
import org.footballproject.clientData.SquadResponse
import org.footballproject.clientData.TeamResponse
import org.footballproject.clientData.ClientTeamStatistics
import org.footballproject.clientData.LiveFixtureOdds
import org.footballproject.clientData.PlayerInfoResponse
import org.footballproject.clientData.TeamLeagueParticipation
import org.footballproject.errors.ApiFailedException
import org.footballproject.errors.ErrorMessage
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.retry.RetryRegistry
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ApiSportsClient(
    private val restClient: RestClient,
    rateLimiterRegistry: RateLimiterRegistry,
    retryRegistry: RetryRegistry = RetryRegistry.ofDefaults()
) {

    private val rateLimiter = rateLimiterRegistry.rateLimiter("apiSports")
    private val retry = retryRegistry.retry("apiSports")

    @Cacheable(value = ["fetchMatches"], key = "#uri")
    fun fetchMatches(uri: String): List<MatchResponse> {
        val result = fetch<ApiResponse<List<MatchResponse>>>(uri)
        return result.response
    }

    @Cacheable(value = ["fetchLeagueInfo"], key = "#uri")
    fun fetchLeagueInfo(uri: String): LeagueWithStandings? {
        val result = fetch<ApiResponse<List<StandingResponse>>>(uri)
        return runCatching {
            result.response
                .firstOrNull()
                ?.league
        }.getOrElse {
            throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }

    @Cacheable(value = ["fetchAllLeagues"], key = "#uri")
    fun fetchAllLeagues(uri: String): List<LeagueAndCountry> {
        val result = fetch<ApiResponse<List<LeagueAndCountry>>>(uri)
        return result.response
    }

    @Cacheable(value = ["fetchFixtureOdds"], key = "#uri")
    fun fetchFixtureOdds(uri: String): List<FixtureOdds> {
        val result = fetch<ApiResponse<List<FixtureOdds>>>(uri)
        return result.response
    }

    @Cacheable(value = ["fetchMatchDetails"], key = "#uri")
    fun fetchMatchDetails(uri: String): MatchResponse {
        val result = fetch<ApiResponse<List<MatchResponse>>>(uri)
        return runCatching { result.response.first() }.getOrElse {
            throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }

    @Cacheable(value = ["fetchMatchEvents"], key = "#uri")
    fun fetchMatchEvents(uri: String): List<Event> {
        val result = fetch<ApiResponse<List<Event>>>(uri)
        return result.response
    }

    @Cacheable(value = ["fetchTeamDetails"], key = "#uri")
    fun fetchTeamDetails(uri: String): TeamResponse {
        val result = fetch<ApiResponse<List<TeamResponse>>>(uri)
        return runCatching { result.response.first() }.getOrElse {
            throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }


    @Cacheable(value = ["fetchCoachDetails"], key = "#uri")
    fun fetchCoachDetails(uri: String): List<CoachResponse> {
        val result = fetch<ApiResponse<List<CoachResponse>>>(uri)
        return runCatching {
            result.response
        }.getOrElse {
            throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }

    @Cacheable(value = ["fetchCurrentSquad"], key = "#uri")
    fun fetchCurrentSquad(uri: String): List<SquadPlayer> {
        val result = fetch<ApiResponse<List<SquadResponse>>>(uri)
        return result.response.firstOrNull()?.players ?: emptyList()
    }


    @Cacheable(value = ["fetchPlayers"], key = "#uri")
    fun fetchPlayers(uri: String): ApiPagingResponse<List<PlayerResponse>> {
        return fetch<ApiPagingResponse<List<PlayerResponse>>>(uri)
    }

    @Cacheable(value = ["fetchLiveFixtures"], key = "#uri")
    fun fetchLiveFixtures(uri: String): List<LiveFixtureResponse> {
        val result = fetch<ApiResponse<List<LiveFixtureResponse>>>(uri)

        return runCatching { result.response }.getOrElse {
            throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }

    @Cacheable(value = ["fetchLiveStatistics"], key = "#uri")
    fun fetchLiveStatistics(uri: String): List<LiveTeamStats> {
        return fetch<ApiPagingResponse<List<LiveTeamStats>>>(uri).response
    }

    @Cacheable(value = ["fetchLiveLineups"], key = "#uri")
    fun fetchLiveLineups(uri: String): List<LineupTeam> {
        return fetch<ApiResponse<List<LineupTeam>>>(uri).response
    }

    @Cacheable(value = ["fetchLeagueRanking"], key = "#uri")
    fun fetchLeagueRanking(uri: String): List<RankingPlayerStats> {
        return fetch<ApiResponse<List<RankingPlayerStats>>>(uri).response
    }

    @Cacheable(value = ["fetchPlayerTrophies"], key = "#uri")
    fun fetchPlayerTrophies(uri: String): List<PlayerTrophy> {
        return fetch<ApiResponse<List<PlayerTrophy>>>(uri).response
    }

    @Cacheable(value = ["fetchTransfers"], key = "#uri")
    fun fetchTransfers(uri: String): List<PlayerTransfer> {
        return fetch<ApiResponse<List<PlayerTransfer>>>(uri).response
    }

    @Cacheable(value = ["fetchPlayerInfo"], key = "#uri")
    fun fetchPlayerInfo(uri: String): List<PlayerInfoResponse> {
        return fetch<ApiResponse<List<PlayerInfoResponse>>>(uri).response
    }

    @Cacheable(value = ["fetchTeamStats"], key = "#uri")
    fun fetchTeamStats(uri: String): ClientTeamStatistics {
        return fetch<ApiResponse<ClientTeamStatistics>>(uri).response
    }

    @Cacheable(value = ["fetchLeagueTeams"], key = "#uri")
    fun fetchLeagueTeams(uri: String): List<TeamResponse> {
        return fetch<ApiResponse<List<TeamResponse>>>(uri).response
    }


    @Cacheable(value = ["fetchTeamLeagues"], key = "#uri")
    fun fetchTeamLeagues(uri: String): List<TeamLeagueParticipation> {
        return fetch<ApiResponse<List<TeamLeagueParticipation>>>(uri).response
    }

    @Cacheable(value = ["fetchLiveFixtureOdds"], key = "#uri")
    fun fetchLiveFixtureOdds(uri: String): List<LiveFixtureOdds> {
        return fetch<ApiResponse<List<LiveFixtureOdds>>>(uri).response
    }


    private inline fun <reified T> fetch(uri: String): T {
        RateLimiter.waitForPermission(rateLimiter)
        val typeRef = object : ParameterizedTypeReference<T>() {}
        return retry.executeSupplier {
            restClient.get()
                .uri(uri)
                .retrieve()
                .body(typeRef)
                ?: throw ApiFailedException(ErrorMessage.CLIENT_FAILED)
        }
    }
}
