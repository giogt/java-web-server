package org.giogt.web.server.http.exceptions;

import org.giogt.web.server.http.model.ClientInfo;

import java.text.MessageFormat;

public class HttpClientDisconnectedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Client disconnected [clientInfo={0}]";

    private final ClientInfo clientInfo;

    private HttpClientDisconnectedException(String message, ClientInfo clientInfo) {
        super(message);
        this.clientInfo = clientInfo;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public static HttpClientDisconnectedException forClient(ClientInfo clientInfo) {
        return new HttpClientDisconnectedException(
                MessageFormat.format(
                        MESSAGE,
                        clientInfo.toString()),
                clientInfo);
    }
}
