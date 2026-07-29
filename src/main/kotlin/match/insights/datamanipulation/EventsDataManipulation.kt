package match.insights.datamanipulation

import match.insights.clientData.Event
import match.insights.clientData.EventTypes
import match.insights.clientData.EventTypesDetail
import match.insights.clientData.Time
import match.insights.response.LastFiveMatchesEvents
import org.springframework.stereotype.Component

@Component
class EventsDataManipulation {


    fun fiveMachesEventsSum(events: List<Event>): LastFiveMatchesEvents {
        var firstHalfGoals = 0
        var secondHalfGoals = 0
        var extraTimeGoals = 0
        var penalties = 0

        var firstHalfYellowCards = 0
        var secondHalfYellowCards = 0
        var extraTimeYellowCards = 0

        var firstHalfRedCards = 0
        var secondHalfRedCards = 0
        var extraTimeRedCards = 0


        events.forEach { event ->
            when (event.type) {
                EventTypes.GOAL.typeName -> {
                    val result = processGoalEvent(event.time, event.detail.orEmpty())
                    penalties += result[PENALTY] ?: 0
                    firstHalfGoals += result[FIRST_HALF] ?: 0
                    secondHalfGoals += result[SECOND_HALF] ?: 0
                    extraTimeGoals += result[EXTRA_TIME] ?: 0
                }

                EventTypes.CARD.typeName -> {
                    val result = processCardEvent(event.time, event.detail.orEmpty())
                    firstHalfYellowCards += result[Y_FIRST_HALF] ?: 0
                    secondHalfYellowCards += result[Y_SECOND_HALF] ?: 0
                    extraTimeYellowCards += result[Y_EXTRA_TIME] ?: 0
                    firstHalfRedCards += result[R_FIRST_HALF] ?: 0
                    secondHalfRedCards += result[R_SECOND_HALF] ?: 0
                    extraTimeRedCards += result[R_EXTRA_TIME] ?: 0
                }

                EventTypes.PENALTY.typeName -> penalties++
            }
        }

        return LastFiveMatchesEvents(
            penalties = penalties,

            firstHalfGoals = firstHalfGoals,
            secondHalfGoals = secondHalfGoals,
            extraTimeGoals = extraTimeGoals,

            firstHalfYellowCards = firstHalfYellowCards,
            secondHalfYellowCards = secondHalfYellowCards,
            extraTimeYellowCards = extraTimeYellowCards,

            firstHalfRedCards = firstHalfRedCards,
            secondHalfRedCards = secondHalfRedCards,
            extraTimeRedCards = extraTimeRedCards

        )

    }

    private fun getMatchSegment(time: Time): String {
        val minute = time.elapsed
        return when {
            minute <= 45 -> FIRST_HALF
            minute in 46..90 -> SECOND_HALF
            else -> EXTRA_TIME
        }
    }

    private fun processGoalEvent(time: Time, detail: String): Map<String, Int> {
        var firstHalfGoals = 0
        var secondHalfGoals = 0
        var extraTimeGoals = 0
        var penalties = 0

        if (detail.contains(EventTypesDetail.PENALTY.detail, ignoreCase = true))
            penalties++
        else {
            when (getMatchSegment(time)) {
                FIRST_HALF -> firstHalfGoals++
                SECOND_HALF -> secondHalfGoals++
                EXTRA_TIME -> extraTimeGoals++
            }
        }

        return mapOf(
            PENALTY to penalties,
            FIRST_HALF to firstHalfGoals,
            SECOND_HALF to secondHalfGoals,
            EXTRA_TIME to extraTimeGoals
        )
    }


    private fun processCardEvent(time: Time, detail: String): Map<String, Int> {
        var yFCards = 0
        var ySCards = 0
        var yECards = 0

        var rFCards = 0
        var rSCards = 0
        var rECards = 0

        when (getMatchSegment(time)) {
            FIRST_HALF -> if (detail.contains(
                    EventTypesDetail.YELLOW_CARD.detail,
                    true
                )
            ) yFCards++;
            else
                if (detail.contains(EventTypesDetail.RED_CARD.detail, true)) rFCards++;

            SECOND_HALF -> if (detail.contains(
                    EventTypesDetail.YELLOW_CARD.detail,
                    true
                )
            ) ySCards++
            else if (detail.contains(EventTypesDetail.RED_CARD.detail, true)) rSCards++;

            EXTRA_TIME -> if (detail.contains(
                    EventTypesDetail.YELLOW_CARD.detail,
                    true
                )
            ) yECards++
            else if (detail.contains(EventTypesDetail.RED_CARD.detail, true)) rECards++;
        }

        return mapOf(
            Y_FIRST_HALF to yFCards,
            Y_SECOND_HALF to ySCards,
            Y_EXTRA_TIME to yECards,
            R_FIRST_HALF to rFCards,
            R_SECOND_HALF to rSCards,
            R_EXTRA_TIME to rECards
        )
    }

    companion object {
        val FIRST_HALF = "1H"
        val SECOND_HALF = "2H"
        val EXTRA_TIME = "ET"
        val Y_FIRST_HALF = "Y1H"
        val Y_SECOND_HALF = "Y2H"
        val Y_EXTRA_TIME = "YET"
        val R_FIRST_HALF = "R1H"
        val R_SECOND_HALF = "R2H"
        val R_EXTRA_TIME = "RET"
        val PENALTY = "P"
    }
}