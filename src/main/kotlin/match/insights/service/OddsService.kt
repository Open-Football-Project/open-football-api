package match.insights.service

import match.insights.apidata.OddsData
import match.insights.datamanipulation.DataManipulation
import match.insights.model.Odd
import match.insights.model.OddFeeling
import match.insights.response.Bet
import match.insights.response.OddsWinnerFeeling
import match.insights.response.ValueBetsResponse

import org.springframework.stereotype.Service

@Service
class OddsService(private val apidata: OddsData, private val dataManipulation: DataManipulation) {

    fun fetchAllOdds(fixtureId: Int): List<Bet> =
        dataManipulation.extractBets(apidata.fetchAllOdds(fixtureId))

    fun fetchValueBets(fixtureId: Int): ValueBetsResponse =
        dataManipulation.extractValueBets(apidata.fetchAllOdds(fixtureId))

    fun oddsWinnerFeeling(fixtureId: Int): OddsWinnerFeeling =
        dataManipulation.oddsFeeling(apidata.fetchAllOdds(fixtureId))
            .let {
                OddsWinnerFeeling(
                    it[Odd.HOME]?.value ?: OddFeeling.NO_DATA.value,
                    it[Odd.DRAW]?.value ?: OddFeeling.NO_DATA.value,
                    it[Odd.AWAY]?.value ?: OddFeeling.NO_DATA.value
                )
            }
}