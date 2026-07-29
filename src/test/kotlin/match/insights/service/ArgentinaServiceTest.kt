package match.insights.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.every
import io.mockk.mockk
import match.insights.apidata.LeaguesData
import match.insights.clientData.ApiResponse
import match.insights.clientData.MatchResponse

import match.insights.model.ArgLeagueEntry
import match.insights.response.ArgSpecial
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import java.io.File
import java.time.LocalDate

class ArgentinaServiceTest {
    private val mapper = jacksonObjectMapper()
    private val leaguesData: LeaguesData = mockk()
    private val argentinaService: ArgentinaService = ArgentinaService(leaguesData)


    private fun loadJsonResource(filename: String): String {
        val resource = this::class.java.classLoader.getResource(filename)
            ?: throw IllegalArgumentException("Resource $filename not found")
        return File(resource.toURI()).readText()
    }

    @Test
    fun `I get Arg special tables`() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val matches2025: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2025.json"))
        val matches2024: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2024.json"))
        val matches2023: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2023.json"))

        val arcopa2025: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2025.json"))
        val arcopa2024: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2024.json"))
        val arcopa2023: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2023.json"))



        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2025.response
        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2024.response
        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2023.response


        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2025.response
        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2024.response
        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2023.response


        val argSpecial: ArgSpecial = argentinaService.argSpecialTables()

        assertThat(argSpecial.annualTable).isNotEmpty
        assertThat(argSpecial.promediosTable).isNotEmpty
    }

    @Test
    fun `buildAnnualTable should correctly calculate points and matches`() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val matches: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2025.json"))


        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches.response


        val table: List<ArgLeagueEntry> = argentinaService.buildAnnualTable(128, LocalDate.now().year)


        val river = table.first { it.teamName.contains("River", ignoreCase = true) }
        val boca = table.first { it.teamName.contains("Boca", ignoreCase = true) }

        assertThat(river.played).isEqualTo(32)
        assertThat(river.points).isEqualTo(53)
        assertThat(river.goalDifference).isEqualTo(17)

        assertThat(boca.played).isEqualTo(32)
        assertThat(boca.points).isEqualTo(62)
        assertThat(boca.goalDifference).isEqualTo(29)


        assertThat(table[0].teamName).containsIgnoringCase("Central")
        assertThat(table[table.size - 1].teamName).containsIgnoringCase("San Martin")
        assertThat(table[0].points).isGreaterThanOrEqualTo(table[1].points)
    }

    @Test
    fun `should get the promedios table`() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val matches2025: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2025.json"))
        val matches2024: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2024.json"))
        val matches2023: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arg2023.json"))

        val arcopa2025: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2025.json"))
        val arcopa2024: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2024.json"))
        val arcopa2023: ApiResponse<List<MatchResponse>> = mapper.readValue(loadJsonResource("arcopa2023.json"))

        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2025.response
        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2024.response
        every { leaguesData.leagueSeasonMatches(128, any()) } returns matches2023.response


        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2025.response
        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2024.response
        every { leaguesData.leagueSeasonMatches(1032, any()) } returns arcopa2023.response


        val table: List<ArgLeagueEntry> = argentinaService.computePromedios(listOf(128, 1032), LocalDate.now().year)


        assertThat(table[0].points).isGreaterThan(0)
        assertThat(table[0].played).isGreaterThan(0)
        assertThat(table[0].teamName).containsIgnoringCase("Huracan")
        assertThat(table[0].promedio).isGreaterThan(1.7)
        assertThat(table[table.size - 1].teamName).containsIgnoringCase("Argentinos JRS")
        assertThat(table[table.size - 1].points).isGreaterThan(0)
        assertThat(table[table.size - 1].played).isGreaterThan(0)
        assertThat(table[table.size - 1].promedio).isGreaterThan(0.6)

    }
}
