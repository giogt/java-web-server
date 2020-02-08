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
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebServerPutIT {
    companion object {
        private val rootDirPath = Path.of("root/test/put")!!
        private val server = TestWebServer(rootDirPath)

        @BeforeAll
        @JvmStatic
        fun init() {
            TestFiles.createDirectories(rootDirPath)
            server.bind()
            server.runAsync()
        }

        @AfterAll
        @JvmStatic
        fun shutdown() {
            server.shutdown()
            TestFiles.deleteDirectoryRecursively(rootDirPath)
        }
    }

    @Test
    @Order(1)
    fun put_whenUploadingNonExistingTxtFile_mustUploadItSuccessfully() {
        val client = TestWebClient()
        val targetURI = URI.create("http://localhost:8080/test_put.txt")

        // put file
        val response = client.put(
                targetURI,
                TestFiles.getResourceBytes("files/test_put.txt"),
                ContentTypes.TEXT_PLAIN)
        assertThat(response.statusCode(), equalTo(204))

        // check that file was uploaded successfully
        val getResponse = client.get(targetURI)
        val getBodyString = String(getResponse.body(), Constants.CHARSET)
        assertThat(getResponse.statusCode(), equalTo(200))
        assertThat(getBodyString, equalTo("This is a text file used to test the PUT method"))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("47")))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.TEXT_PLAIN)))
    }

    @Test
    @Order(2)
    fun put_whenUploadingNonExistingJsonFile_mustUploadItSuccessfully() {
        val client = TestWebClient()
        val targetURI = URI.create("http://localhost:8080/test_put.json")

        // put file
        val response = client.put(
                targetURI,
                TestFiles.getResourceBytes("files/test_put.json"),
                ContentTypes.APPLICATION_JSON)
        assertThat(response.statusCode(), equalTo(204))

        // check that file was uploaded successfully
        val getResponse = client.get(targetURI)
        val getBodyString = String(getResponse.body(), Constants.CHARSET)
        assertThat(getResponse.statusCode(), equalTo(200))
        assertThat(getBodyString, equalTo("""
            {
              "name": "Test",
              "surname": "Put",
              "age": 123
            }
            """.trimIndent()))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("54")))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_JSON)))
    }

    @Test
    @Order(3)
    fun put_whenUploadingNonExistingPdfFile_mustUploadItSuccessfully() {
        val client = TestWebClient()
        val targetURI = URI.create("http://localhost:8080/scala-with-cats.pdf")

        // put file
        val response = client.put(
                targetURI,
                TestFiles.getResourceBytes("files/scala-with-cats.pdf"),
                ContentTypes.APPLICATION_JSON)
        assertThat(response.statusCode(), equalTo(204))

        // check that file was uploaded successfully
        val getResponse = client.get(targetURI)
        assertThat(getResponse.statusCode(), equalTo(200))
        assertThat(getResponse.body().size, equalTo(4676738))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_LENGTH), equalTo(Optional.of("4676738")))
        assertThat(getResponse.headers().firstValue(HttpHeaders.CONTENT_TYPE), equalTo(Optional.of(ContentTypes.APPLICATION_PDF)))
    }
}
