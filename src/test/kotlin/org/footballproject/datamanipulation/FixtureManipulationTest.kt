package org.footballproject.datamanipulation

import org.footballproject.data.client.ClientMatchResponseData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.collections.all

class FixtureManipulationTest {
    val underTest = FixturesManipulation()

    @Test
    fun shouldGroupByRound() {

        val result = underTest.groupByRound(ClientMatchResponseData.matchResponseList)

        assertThat(result["first-round"]?.size).isEqualTo(3)
        assertThat(result["second-round"]?.size).isEqualTo(1)

    }

    @Test
    fun shouldGroupByDate() {
        val result = underTest.groupByDate(ClientMatchResponseData.matchResponseList)
        assertThat(result.keys.size).isEqualTo(3)

    }

    @Test
    fun shouldSortMatchesPerDay() {
        val liveIds = ClientMatchResponseData.matchResponseList.map { it.fixture.id }.toSet()
        val sortedByDayMatches = underTest.toLeagueFixtureDays(ClientMatchResponseData.matchResponseList, liveIds)
        assertThat(sortedByDayMatches.size).isEqualTo(3)
        assertThat(sortedByDayMatches.all { it.matches.all { it.isLiveNow } }).isTrue
    }

    @Test
    fun shouldGetTheMatchesPerDayWithinRoundGroups() {
        val liveIds = ClientMatchResponseData.matchResponseList.map { it.fixture.id }.toSet()

        val roundsList = underTest.toLeagueFixtureRounds(ClientMatchResponseData.matchResponseList, liveIds)
        assertThat(roundsList.size).isEqualTo(2)

    }


    @Test
    fun shouldGetTheLeagueFixture() {
        val liveIds = ClientMatchResponseData.matchResponseList.map { it.fixture.id }.toSet()

        val result = underTest.toLeagueFixture(ClientMatchResponseData.matchResponseList, liveIds)

        assertThat(result.currentRoundIndex).isEqualTo(1)
        assertThat(result.rounds.size).isEqualTo(2)
        assertThat(result.totalRounds).isEqualTo(2)
        assertThat(result.rounds[result.currentRoundIndex].days[0].matches[0].isLiveNow).isTrue
    }

    @Test
    fun shouldExtractTeamFixture() {
        val liveIds = ClientMatchResponseData.matchResponseList.map { it.fixture.id }.toSet()

        val result = underTest.extractTeamFixture(
            mapOf(
                "previous" to ClientMatchResponseData.matchResponseList,
                "upcoming" to ClientMatchResponseData.matchResponseList
            ), liveIds
        )

        assertThat(result.previous.size).isEqualTo(ClientMatchResponseData.matchResponseList.size)
        assertThat(result.upcoming.size).isEqualTo(ClientMatchResponseData.matchResponseList.size)
        assertThat(result.upcoming[0].isLiveNow).isTrue
        assertThat(result.previous[0].isLiveNow).isTrue
    }
}