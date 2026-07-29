package match.insights.response


import match.insights.clientData.MatchResponse
import match.insights.clientData.Team

import match.insights.clientData.Venue

data class H2HDetails(
    val date: String,
    val venue: Venue,
    val leagueName: String,
    val season: Int,
    val round: String? = "",
    val winner: String,
    val homeHalfTimeGoal: Int,
    val awayHalfTimeGoal: Int,
    val homeFullTimeGoal: Int,
    val awayFullTimeGoal: Int,
    val homeExtraTimeGoal: Int,
    val awayExtraTimeGoal: Int,
    val homePenalty: Int,
    val awayPenalty: Int
) {
    companion object {
        fun fromResponseData(matchResponse: MatchResponse): H2HDetails {
            return H2HDetails(
                matchResponse.fixture.date,
                matchResponse.fixture.venue ?: Venue(),
                matchResponse.league.name,
                matchResponse.league.season,
                matchResponse.league.round ?: "",
                getWinner(matchResponse.teams.home, matchResponse.teams.away),
                matchResponse.score?.halftime?.home ?: 0,
                matchResponse.score?.halftime?.away ?: 0,
                matchResponse.score?.fulltime?.home ?: 0,
                matchResponse.score?.fulltime?.away ?: 0,
                matchResponse.score?.extratime?.home ?: 0,
                matchResponse.score?.extratime?.away ?: 0,
                matchResponse.score?.penalty?.home ?: 0,
                matchResponse.score?.penalty?.away ?: 0,
            )


        }

        fun getWinner(homeTeam: Team?, awayTeam: Team?): String {
            if (homeTeam == null && awayTeam == null)
                return "No Data";

            if (homeTeam?.winner == true)
                return homeTeam.name;

            if (awayTeam?.winner == true)
                return awayTeam.name;

            return "Draw";
        }

    }

}