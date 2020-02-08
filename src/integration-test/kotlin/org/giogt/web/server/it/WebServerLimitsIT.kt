package org.giogt.web.server.it

import org.giogt.web.server.http.HttpRequestParser
import org.giogt.web.server.http.HttpRequestReader
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.net.URI
import java.nio.file.Path

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebServerLimitsIT {
    companion object {
        private val rootDirPath = Path.of("root/test/get")!!
        private val server = TestWebServer(rootDirPath)

        @BeforeAll
        @JvmStatic
        fun init() {
            server.bind()
            server.runAsync()
        }

        @AfterAll
        @JvmStatic
        fun shutdown() {
            server.shutdown()
        }
    }

    @Test
    @Order(1)
    fun get_whenRequestLineIsTooLarge_mustReturn400BadRequest() {
        val client = TestWebClient()

        val uriBuilder = StringBuilder("http://localhost:8080/test.txt")
        while (uriBuilder.length <= HttpRequestReader.LINE_LIMIT + 100) {
            uriBuilder.append(".asdf")
        }
        val targetURI = URI.create(uriBuilder.toString())
        val response = client.get(targetURI)

        assertThat(response.statusCode(), equalTo(400))
    }

    @Test
    @Order(2)
    fun get_whenRequestHeaderFieldsAreTooLarge_mustReturn431RequestHeaderFieldsTooLarge() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/test.txt")

        val headers = ArrayList<TestHeader>()
        var headersLength = 0
        var cntr = 0
        while (headersLength <= HttpRequestParser.HEADERS_LIMIT + 100) {
            val header = TestHeader(name = "Header", value= "value${cntr++}")
            headers.add(header)
            headersLength += header.name.length + header.value.length
        }

        val response = client.get(targetURI, headers)

        assertThat(response.statusCode(), equalTo(431))
    }
}
