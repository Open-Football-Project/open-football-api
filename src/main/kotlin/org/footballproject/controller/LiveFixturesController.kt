package org.footballproject.controller

import org.footballproject.response.LiveMatchesResponse
import org.footballproject.service.LiveFixturesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/live")
class LiveFixturesController(
    private val liveFixturesService: LiveFixturesService
) {

    @GetMapping("/all")
    fun liveFixtures(): SseEmitter {
        return liveFixturesService.streamLiveMatches()
    }

    @GetMapping("/events")
    fun liveEvents(): List<LiveMatchesResponse> {
        return liveFixturesService.getLiveMatches()
    }
}