package org.footballproject.response.game

data class GuessTheTeamGameData(
    val isAvailable: Boolean = false,
    val teamId: Int = -1,
    val teamLogo: String? = "",
    val teamName: String = "",
    val venue: String = "",
    val founded: Int = -1,
    val season: Int = -1,
    val hints: List<GuessTheTeamGameHint> = emptyList(),
    val options: List<String> = emptyList()
)

data class GuessTheTeamGameHint(
    val hintKey: GuessThetTeamGameHintKey,
    val description: GuessThetTeamGameHintDescriptionKey,
    val value: String
)


enum class GuessThetTeamGameHintKey(val value: String) {
    PLAYER("player"),
    STAT("trophy");
}

enum class GuessThetTeamGameHintDescriptionKey(val value: String) {
    TEAM_QUIZ_GOALS_SCORED("team_quiz_goals_scored"),
    TEAM_QUIZ_GOALS_CONCEDED("team_quiz_goals_conceded"),
    TEAM_QUIZ_CLEAN_SHEETS_HOME("team_quiz_clean_sheets_home"),
    TEAM_QUIZ_CLEAN_SHEETS_AWAY("team_quiz_clean_sheets_away"),
    TEAM_QUIZ_FAILED_TO_SCORE("team_quiz_failed_to_score"),
    TEAM_QUIZ_MATCHES_WON("team_quiz_matches_won"),
    TEAM_QUIZ_MATCHES_LOST("team_quiz_matches_lost"),
    TEAM_QUIZ_BEST_MINUTE("team_quiz_best_minute"),
    TEAM_QUIZ_LONGEST_WINNING_STREAK("team_quiz_longest_winning_streak"),
    TEAM_QUIZ_YELLOW_CARDS("team_quiz_yellow_cards"),
    TEAM_QUIZ_RED_CARDS("team_quiz_red_cards"),
    TEAM_QUIZ_PENALTIES_SCORED("team_quiz_penalties_scored"),
    TEAM_QUIZ_PENALTIES_MISSED("team_quiz_penalties_missed"),
    TEAM_QUIZ_MOST_USED_FORMATION("team_quiz_most_used_formation"),
    TEAM_QUIZ_TOP_PLAYER("team_quiz_top_player")
}