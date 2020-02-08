package org.giogt.web.server.it

import org.giogt.web.server.http.HttpHeaders
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

class TestWebClient() {
    val client = HttpClient.newHttpClient()

    fun get(uri: URI): HttpResponse<ByteArray> = client.send(
            HttpRequest.newBuilder().GET().uri(uri).build(),
            HttpResponse.BodyHandlers.ofByteArray()
    )

    fun get(uri: URI, headers: Collection<TestHeader>): HttpResponse<ByteArray> {
        val request = HttpRequest.newBuilder().GET().uri(uri)
        headers.forEach{ request.header(it.name, it.value) }
        return client.send(
                request.build(),
                HttpResponse.BodyHandlers.ofByteArray()
        )
    }

    fun head(uri: URI): HttpResponse<ByteArray> = client.send(
            HttpRequest.newBuilder().method("HEAD", BodyPublishers.noBody()).uri(uri).build(),
            HttpResponse.BodyHandlers.ofByteArray()
    )

    fun put(uri: URI, body: ByteArray, contentType: String): HttpResponse<Void> = client.send(
            HttpRequest.newBuilder()
                    .PUT(BodyPublishers.ofByteArray(body))
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .build(),
            HttpResponse.BodyHandlers.discarding()
    )

    fun delete(uri: URI): HttpResponse<Void> = client.send(
            HttpRequest.newBuilder()
                    .DELETE()
                    .uri(uri)
                    .build(),
            HttpResponse.BodyHandlers.discarding()
    )
}
