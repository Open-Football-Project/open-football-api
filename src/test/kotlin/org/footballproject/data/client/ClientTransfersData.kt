package org.footballproject.data.client

import org.footballproject.clientData.PlayerTransfer
import org.footballproject.clientData.Transfer
import org.footballproject.clientData.TransferPlayer
import org.footballproject.clientData.TransferTeam
import org.footballproject.clientData.TransferTeams

class ClientTransfersData {
    companion object {
        val transfers = listOf(
            PlayerTransfer(
                player = TransferPlayer(
                    id = 142,
                    name = "Neymar Jr",
                    photo = "https://media.api-sports.io/football/players/142.png"
                ),
                update = "2023-09-04",
                transfers = listOf(
                    Transfer(
                        date = "2023-08-15",
                        type = "Transfer",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 2905,
                                name = "Al-Hilal",
                                logo = "https://media.api-sports.io/football/teams/2905.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 85,
                                name = "Paris Saint-Germain",
                                logo = "https://media.api-sports.io/football/teams/85.png"
                            )
                        )
                    ),
                    Transfer(
                        date = "2017-08-03",
                        type = "Transfer",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 85,
                                name = "Paris Saint-Germain",
                                logo = "https://media.api-sports.io/football/teams/85.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 529,
                                name = "Barcelona",
                                logo = "https://media.api-sports.io/football/teams/529.png"
                            )
                        )
                    ),
                    Transfer(
                        date = "2013-05-27",
                        type = "Transfer",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 529,
                                name = "Barcelona",
                                logo = "https://media.api-sports.io/football/teams/529.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 667,
                                name = "Santos FC",
                                logo = "https://media.api-sports.io/football/teams/667.png"
                            )
                        )
                    )
                )
            ),
            PlayerTransfer(
                player = TransferPlayer(
                    id = 874,
                    name = "Lionel Messi",
                    photo = "https://media.api-sports.io/football/players/874.png"
                ),
                update = "2023-07-21",
                transfers = listOf(
                    Transfer(
                        date = "2023-07-15",
                        type = "Free",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 15736,
                                name = "Inter Miami",
                                logo = "https://media.api-sports.io/football/teams/15736.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 85,
                                name = "Paris Saint-Germain",
                                logo = "https://media.api-sports.io/football/teams/85.png"
                            )
                        )
                    ),
                    Transfer(
                        date = "2021-08-10",
                        type = "Free",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 85,
                                name = "Paris Saint-Germain",
                                logo = "https://media.api-sports.io/football/teams/85.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 529,
                                name = "Barcelona",
                                logo = "https://media.api-sports.io/football/teams/529.png"
                            )
                        )
                    )
                )
            )
        )
    }
}