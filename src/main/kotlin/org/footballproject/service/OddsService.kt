package org.footballproject.service

import org.footballproject.apidata.OddsData
import org.footballproject.datamanipulation.DataManipulation
import org.footballproject.model.Odd
import org.footballproject.model.OddFeeling
import org.footballproject.response.Bet
import org.footballproject.response.OddsWinnerFeeling
import org.footballproject.response.ValueBetsResponse

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