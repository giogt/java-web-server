package org.giogt.web.server;

import lombok.extern.slf4j.Slf4j;
import org.giogt.web.server.config.WebServerConfig;
import org.giogt.web.server.http.HttpConnectionHandler;
import org.giogt.web.server.http.model.ClientInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebServer {

    private final WebServerContext context;
    private final ExecutorService executor;

    private ServerSocket serverSocket;
    private WebServerState state;
    private boolean stop;
    private List<WebServerListener> listeners;

    private final Object stateLock = new Object();

    public WebServer(WebServerContext context) {
        this.context = context;
        this.executor = initExecutor();
        this.listeners = new ArrayList<>();

        this.state = WebServerState.CREATED;

        String message = String.format("web server created [config=%s]", this.context.getConfig());
        log.info(message);
        notifyListeners(WebServerState.CREATED, message);
    }

    private ExecutorService initExecutor() {
        return Executors.newFixedThreadPool(context.getConfig().getNThreads());
    }

    public void addListener(WebServerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WebServerListener listener) {
        listeners.remove(listener);
    }

    void notifyListeners(WebServerState state, String message) {
        for (WebServerListener listener : listeners) {
            listener.onWebServerStateChanged(state, message);
        }
    }

    public void bind() throws IOException {
        changeState("bind", WebServerState.STARTING, WebServerState.CREATED);
        String message = "web server starting ...";
        log.info(message);
        notifyListeners(WebServerState.STARTING, message);

        WebServerConfig config = context.getConfig();

        serverSocket = new ServerSocket();
        InetSocketAddress endpoint = config.getAddress() != null
                ? new InetSocketAddress(config.getAddress(), config.getPort())
                : new InetSocketAddress(config.getPort());
        serverSocket.bind(endpoint, config.getIncomingConnectionsQueueSize());

        changeState("run", WebServerState.LISTENING, WebServerState.STARTING);
        message = String.format("web server started listening on %s:%s",
                endpoint.getAddress(),
                endpoint.getPort());
        log.info(message);
        notifyListeners(WebServerState.LISTENING, message);
    }

    public void run() throws IOException {
        changeState("run", WebServerState.RUNNING, WebServerState.LISTENING);
        String message = "web server ready to process requests";
        log.info(message);
        notifyListeners(WebServerState.RUNNING, message);

        while (!stop) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientInfo clientInfo = ClientInfo.builder()
                        .ipAddress(clientSocket.getInetAddress().toString())
                        .port(clientSocket.getPort())
                        .build();

                log.info("client connected [clientInfo={}]", clientInfo);
                executor.submit(new HttpConnectionHandler(context, clientSocket, clientInfo));
            } catch (SocketException e) {
                log.info("server socket exception: server socket has been closed");
            }
        }
    }

    public void shutdown() {
        shutdown(
                context.getConfig().getForceShutdownTimeoutSeconds(),
                TimeUnit.SECONDS);
    }

    public void shutdown(long timeout, TimeUnit unit) {
        changeState(
                "shutdown",
                WebServerState.STOPPING,
                WebServerState.CREATED, WebServerState.STARTING, WebServerState.RUNNING);
        stop = true;
        String message = "web server stopping ...";
        log.info(message);
        notifyListeners(WebServerState.STOPPING, message);

        // stop incoming connections
        try {
            log.info("closing server socket ...");
            serverSocket.close();
            log.info("server socket successfully closed: no new incoming connections will be accepted");
        } catch (IOException e) {
            log.info("error while trying to close server socket", e);
        }

        // shut down thread executor
        log.info(
                "shutting down thread executor [timeout: {} {}] ...",
                timeout,
                unit);
        executor.shutdown();
        boolean terminated = false;
        try {
            terminated = executor.awaitTermination(timeout, unit);
        } catch (InterruptedException ignore) {
        }
        if (terminated) {
            log.info("thread executor successfully shutdown");
        } else {
            log.info(
                    "thread executor is still running after {} {} timeout: " +
                            "starting forced shutdown ...",
                    timeout,
                    unit);
            executor.shutdownNow();
            log.info("thread executor successfully shutdown");
        }
        changeState("shutdown", WebServerState.STOPPED, WebServerState.STOPPING);
        message = "web server successfully stopped";
        log.info(message);
        notifyListeners(WebServerState.STOPPED, message);
    }

    private void changeState(
            String operation,
            WebServerState newState,
            WebServerState... expectedStates) {
        Set<WebServerState> expectedStatesSet = new LinkedHashSet<>();
        Collections.addAll(expectedStatesSet, expectedStates);
        changeState(operation, newState, expectedStatesSet);
    }

    private void changeState(
            String operation,
            WebServerState newState,
            Set<WebServerState> expectedStates) {
        synchronized (stateLock) {
            if (!expectedStates.contains(state)) {
                throw new IllegalStateException(
                        String.format(
                                "cannot perform a %s operation while in %s state [expected states: %s]",
                                operation,
                                state,
                                expectedStates)
                );
            }
            state = newState;
        }
    }
}
