package org.footballproject.controller

import org.footballproject.request.FeedbackRequest
import org.footballproject.service.FeedbackService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feedback")
class FeedbackController(private val feedbackService: FeedbackService) {

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    fun submitFeedback(@RequestBody request: FeedbackRequest) =
        feedbackService.handleFeedback(request)
}
