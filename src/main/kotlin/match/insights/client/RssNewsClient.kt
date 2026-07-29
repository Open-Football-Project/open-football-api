package match.insights.client

import match.insights.errors.ApiRssFeedException
import match.insights.errors.ErrorMessage
import org.springframework.stereotype.Component
import org.w3c.dom.Element
import java.net.HttpURLConnection
import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

@Component
class RssNewsClient {

    fun fetchXml(url: String): Element? {
        val conn = URI.create(url).toURL().openConnection() as HttpURLConnection

        return try {
            conn.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", USER_AGENT)
                connectTimeout = CONNECTION_TIMEOUT
                readTimeout = READ_TIMEOUT
            }

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                throw ApiRssFeedException(ErrorMessage.RSS_CLIENT_FAILED)
            }

            val document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(conn.inputStream)

            document.documentElement

        } catch (ex: Exception) {
            throw ApiRssFeedException(ErrorMessage.RSS_CLIENT_FAILED)
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val CONNECTION_TIMEOUT = 8000
        const val READ_TIMEOUT = 8000
        const val USER_AGENT = "Mozilla/5.0 (futballero.com)"
    }
}
