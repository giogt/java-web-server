package org.giogt.web.server.it

import org.giogt.web.server.WebServer
import org.giogt.web.server.WebServerContext
import org.giogt.web.server.config.WebServerConfig
import java.net.InetAddress
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.Future

class TestWebServer(rootDirPath: Path) {
    private val executor = Executors.newFixedThreadPool(1)
    private val config = config(rootDirPath)
    private val server = WebServer(
            WebServerContext.builder()
                    .config(config)
                    .build()
    )

    private fun config(rootDirPath: Path): WebServerConfig {
        val config = WebServerConfig()
        config.address = defaultAddress()
        config.port = 8080
        config.nThreads = 5
        config.incomingConnectionsQueueSize = 5
        config.forceShutdownTimeoutSeconds = 5
        config.rootDirPath = rootDirPath

        return config
    }

    private fun defaultAddress(): InetAddress = InetAddress.getByName("0.0.0.0")

    fun bind() {
        server.bind()
    }

    fun runAsync(): Future<Void> {
        return executor.submit<Void> {
            server.run()
            null
        }
    }

    fun shutdown() {
        server.shutdown()
        executor.shutdown()
    }
}
