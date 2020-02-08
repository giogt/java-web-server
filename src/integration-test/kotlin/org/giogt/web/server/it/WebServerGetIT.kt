package org.giogt.web.server.it

import org.giogt.web.server.Constants
import org.giogt.web.server.http.HttpHeaders
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
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebServerGetIT {
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
    fun get_whenTargetIsAnExistingPlainTextFile_mustReturn200ResponseWithExpectedHeadersAndFileBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/test.txt")
        val response = client.get(targetURI)
        val bodyString = String(response.body(), Constants.CHARSET)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(bodyString, equalTo("This is a text test file used by the integration tests"))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("54")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.TEXT_PLAIN)))
    }

    @Test
    @Order(2)
    fun get_whenTargetIsAnExistingJsonFile_mustReturn200ResponseWithExpectedHeadersAndFileBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/test.json")
        val response = client.get(targetURI)
        val bodyString = String(response.body(), Constants.CHARSET)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(bodyString, equalTo("""
            {
              "name": "Jane",
              "surname": "Doe",
              "age": 28
            }
            """.trimIndent()))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("53")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_JSON)))
    }

    @Test
    @Order(3)
    fun get_whenTargetIsAnExistingPdfFile_mustReturn200ResponseWithExpectedHeadersAndFileBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/scala-with-cats.pdf")
        val response = client.get(targetURI)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(response.body().size, equalTo(4676738))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("4676738")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_PDF)))
    }

    @Test
    @Order(4)
    fun get_whenTargetIsANonExistingFile_mustReturn404Response() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/non_existing_file.txt")
        val response = client.get(targetURI)
        assertThat(response.statusCode(), equalTo(404))
    }
}
