package org.giogt.web.server.it

import org.giogt.web.server.http.HttpHeaders
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.util.*

class WebServerHeadIT {
    companion object {
        // re-using the same files used for the GET method integration tests
        private val rootDirPath = Path.of("root/test/get")
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
    fun head_whenTargetIsAnExistingPlainTextFile_mustReturn200ResponseWithExpectedHeadersAndNoBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/test.txt")
        val response = client.head(targetURI)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(response.body().size, equalTo(0))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("54")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.TEXT_PLAIN)))
    }

    @Test
    @Order(2)
    fun head_whenTargetIsAnExistingJsonFile_mustReturn200ResponseWithExpectedHeadersAndNoBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/test.json")
        val response = client.head(targetURI)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(response.body().size, equalTo(0))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("53")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_JSON)))
    }

    @Test
    @Order(3)
    fun head_whenTargetIsAnExistingPdfFile_mustReturn200ResponseWithExpectedHeadersAndNoBody() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/scala-with-cats.pdf")
        val response = client.head(targetURI)

        assertThat(response.statusCode(), equalTo(200))
        assertThat(response.body().size, equalTo(0))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("4676738")))
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_PDF)))
    }

    @Test
    @Order(4)
    fun head_whenTargetIsANonExistingFile_mustReturn404Response() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/non_existing_file.extension")
        val response = client.head(targetURI)
        assertThat(response.statusCode(), equalTo(404))
    }
}
