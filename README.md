# 🏆 Matches API

REST API built with **Spring Boot** and **Kotlin**, designed to provide football data,
betting odds, and analytical insights.

It's open source and easy to run locally or deploy.

Join the community, contribute, and build smarter sports tools.

## 🚀 Features

- 📅 Get **today's matches**
- 📊 Access **match odds** (live & pre-match)
- 🔍 Retrieve **match insights** (form, history)
- 🏆 Browse **available leagues**
- 📈 Historical data support

---

## ⚙️ Tech Stack

- 💻 Kotlin (JVM)
- 🌐 Spring Boot
- 🛠 Gradle
- 🐳 Docker

---

## 🧪 Getting Started

### Prerequisites

- Java
- Gradle
- Docker (optional)

### Running Unit Tests

```bash
./gradlew clean test
```

### Running Locally

The API needs Redis for caching, and a real API key from [api-sports.io](https://api-sports.io) to fetch live football data.

```bash
docker run -d -p 6379:6379 redis:7-alpine
```

Set your API key in `src/main/resources/application.yml` (`api-data.key`), or override it via environment variable (`API_DATA_KEY`), then run:

```bash
./gradlew bootRun
```

The API starts on port `8080`. Everything else in the checked-in `application.yml` is placeholder/example config — safe defaults for local development, not real production values.
