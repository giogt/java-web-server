package org.giogt.web.server.it

import org.giogt.web.server.Constants
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
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WebServerParallelGetIT {
    companion object {
        val clientExecutor = Executors.newFixedThreadPool(300)!!

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
            clientExecutor.shutdown()
        }
    }

    @Test
    @Order(1)
    fun get_whenMultipleParallelClientsRequestFiles_mustServeAllClients() {
        val futures = ArrayList<Future<TestData>>()
        for (i in 1..100) {
            futures.add(clientExecutor.submit(TxtTest(TestWebClient())))
            futures.add(clientExecutor.submit(JsonTest(TestWebClient())))
            futures.add(clientExecutor.submit(PdfTest(TestWebClient())))
        }
        futures.forEach {
            val testData = it.get()
            assertThat(testData.actualStatusCode, equalTo(testData.expectedStatusCode))
            assertThat(testData.actualContentLength, equalTo(testData.expectedContentLength.toString()))
            assertThat(testData.actualBodyLength, equalTo(testData.expectedContentLength))
            assertThat(testData.actualContentType, equalTo(testData.expectedContentType))
            if (testData.expectedBodyString != null) {
                assertThat(testData.actualBodyString, equalTo(testData.expectedBodyString))
            }
        }
    }

    class TxtTest(val client: TestWebClient) : Callable<TestData> {
        override fun call(): TestData {
            val targetURI = URI.create("http://localhost:8080/test.txt")
            val response = client.get(targetURI)
            return TestData(
                    targetURI = targetURI,

                    expectedStatusCode = 200,
                    actualStatusCode = response.statusCode(),

                    expectedContentLength = 54,
                    actualContentLength = response.headers().firstValue("Content-Length").orElse(null),
                    actualBodyLength = response.body().size,

                    expectedContentType = ContentTypes.TEXT_PLAIN,
                    actualContentType = response.headers().firstValue("Content-Type").orElse(null),

                    expectedBodyString = "This is a text test file used by the integration tests",
                    actualBodyString = String(response.body(), Constants.CHARSET)
            )
        }
    }

    class JsonTest(val client: TestWebClient) : Callable<TestData> {
        override fun call(): TestData {
            val targetURI = URI.create("http://localhost:8080/test.json")
            val response = client.get(targetURI)
            return TestData(
                    targetURI = targetURI,

                    expectedStatusCode = 200,
                    actualStatusCode = response.statusCode(),

                    expectedContentLength = 53,
                    actualContentLength = response.headers().firstValue("Content-Length").orElse(null),
                    actualBodyLength = response.body().size,

                    expectedContentType = ContentTypes.APPLICATION_JSON,
                    actualContentType = response.headers().firstValue("Content-Type").orElse(null),

                    expectedBodyString = """
                        {
                          "name": "Jane",
                          "surname": "Doe",
                          "age": 28
                        }
                        """.trimIndent(),
                    actualBodyString = String(response.body(), Constants.CHARSET)
            )
        }
    }

    class PdfTest(val client: TestWebClient) : Callable<TestData> {
        override fun call(): TestData {
            val targetURI = URI.create("http://localhost:8080/scala-with-cats.pdf")
            val response = client.get(targetURI)
            return TestData(
                    targetURI = targetURI,

                    expectedStatusCode = 200,
                    actualStatusCode = response.statusCode(),

                    expectedContentLength = 4676738,
                    actualContentLength = response.headers().firstValue("Content-Length").orElse(null),
                    actualBodyLength = response.body().size,

                    expectedContentType = ContentTypes.APPLICATION_PDF,
                    actualContentType = response.headers().firstValue("Content-Type").orElse(null),

                    expectedBodyString = null,
                    actualBodyString = null
            )
        }
    }

    data class TestData(
            val targetURI: URI,

            val expectedStatusCode: Int,
            val actualStatusCode: Int,

            val expectedContentLength: Int,
            val actualContentLength: String?,
            val actualBodyLength: Int,

            val expectedContentType: String,
            val actualContentType: String?,

            val expectedBodyString: String?,
            val actualBodyString: String?
    )
}
