package match.insights.errors

data class ApiFailedException(val error: ErrorMessage) : Exception(error.message)
data class ApiRssFeedException(val error: ErrorMessage) : Exception(error.message)
data class ApiResourceNotFoundException(val error: ErrorMessage) : Exception(error.message)
data class ApiResourceAlreadyExists(val error: ErrorMessage) : Exception(error.message)