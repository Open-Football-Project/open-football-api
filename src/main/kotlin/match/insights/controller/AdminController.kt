package match.insights.controller

import match.insights.service.TodayPlayersService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val todayPlayersService: TodayPlayersService,
    @Value("\${admin.trigger-key:}")
    private val adminTriggerKey: String
) {

    @PostMapping("/today-players/capture")
    fun triggerTodayPlayersCapture(
        @RequestHeader(value = "X-Admin-Key", required = false, defaultValue = "")
        key: String
    ): ResponseEntity<Void> {
        if (adminTriggerKey.isBlank() || key != adminTriggerKey) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        todayPlayersService.captureTodayPlayers()
        return ResponseEntity.ok().build()
    }
}
