package org.giogt.web.server.http.model;

/**
 * HTTP method
 *
 * This is a list of all valid HTTP methods.
 * Some of these methods are not supported by this server, but it is important
 * to keep the enum values here, since they are used to parse the request.
 *
 * Methods not present in here will cause a request parse exception when
 * received by a client, and a bad request response will be send back to the
 * client.
 */
public enum HttpMethod {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT

}
