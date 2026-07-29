package org.footballproject.errors

enum class ErrorMessage(val message: String) {
    CLIENT_FAILED("Client Failed."),
    CALCULATION_FAILED("Calculation Failed."),
    RSS_CLIENT_FAILED("RSS news client Failed."),
    EMPTY_STATS("Team Stats not available"),
    RESOURCE_EXIST("Resource already exist"),
    INVALID_DATE("Invalid Date"),
    RESOURCE_NOT_FOUND("Resource not found");
}