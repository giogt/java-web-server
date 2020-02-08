package org.giogt.web.server.it

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
class WebServerDeleteIT {
    companion object {
        private val rootDirPath = Path.of("root/test/delete")!!
        private val server = TestWebServer(rootDirPath)
        private val client = TestWebClient()

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
    fun delete_whenDeletingANonExistingFile_mustReturnASuccessfulNoContentResponse() {
        val client = TestWebClient()

        val target = URI.create("http://localhost:8080/non_existing_file.txt")
        val response = client.delete(target)
        assertThat(response.statusCode(), equalTo(204))
    }

    @Test
    @Order(2)
    fun delete_whenDeletingAnExistingFile_mustDeleteItAndReturnASuccessfulNoContentResponse() {
        val client = TestWebClient()

        val targetURI = URI.create("http://localhost:8080/non_existing_file.txt")

        // put file
        val putResponse = client.put(
                targetURI,
                TestFiles.getResourceBytes("files/test_put.txt"),
                ContentTypes.TEXT_PLAIN)
        assertThat(putResponse.statusCode(), equalTo(204))

        // check that file is there
        assertThat(client.head(targetURI).statusCode(), equalTo(200))

        // delete file
        assertThat(client.delete(targetURI).statusCode(), equalTo(204))

        // check that file is not there anymore
        assertThat(client.head(targetURI).statusCode(), equalTo(404))
    }
}
