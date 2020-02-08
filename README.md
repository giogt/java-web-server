giogt web-server
===============

A file web-server able to serve `HTTP` requests, implemented from scratch
with a limited set of third-party dependencies.

This is mainly an educational project and can be used as a base to implement a
custom simple server.

A valid alternative to write it from scratch could be using a framework such as
[Netty](https://netty.io/).

## HTTP methods

Supported methods:

- GET
- PUT
- DELETE
- HEAD

The `POST` method could have been implemented using `multipart/form-data` to
create new files and the `PUT` method could have been left for updates only.
For simplicity, instead, only the `PUT` method was implemented and it is used
for both creates and updates.

The way that the `POST` method could have worked, if implemented, is:

- the request target URI is a server directory
- the request body is encoded using `multipart/form-data`, to be able to
  contain both a file and some parameters (metadata)
- a request parameter can be used as the file name
  * the parameter name must be shared between the client and the server
    (e.g., `filename`)
- the server reads the file from the request and writes it in the target
  directory, using the filename specified in the request parameter for the
  file name
- if the operation is successful, a `204` (Created) response is sent to the
  client, adding a location header set with the newly created file target URI.

## Technology used

- The main application and the unit tests are developed in `Java` version 11
- The integration tests are developed in `Kotlin`
- The build tool used is `gradle`
- Tests are developed using `JUnit` version 5 (`Juniper` + `hamcrest`)
- Other technologies:
  * [Project Lombok](https://projectlombok.org/)
  * `slf4j` + `logback`
  * `freemarker` (for templates)
  * `javax.activation` (for MIME type detection)

## Error responses

For all 4xx and 5xx responses, a `HTML` page with the description of the error
is returned.
This is useful when using a web browser, so that the user can easily visualise
the error occurred.

This feature is implemented using `freemarker` templates.

## Directory listing

When a `GET` request is performed on a target URI denoting a directory, a
`HTML` page listing the files included in the directory is displayed to the
user.

Directories are displayed in bold to be distinguishable from regular files.

This feature is implemented using `freemarker` templates, too.

## Content types

When serving files, the content type is derived using the
`MimetypesFileTypeMap` class from `javax.activation`, which works using a
mapping between file extensions and MIME types.

The library already contains some mappings, but some of them are not up to date.
Additional mappings can be specified in the `META-INF/mime.types` file in the
`Java` resources (in this project, the `src/main/resources` directory).
The library will always try to use the mappings defined in the resource file
first, if present.
A resource file with more up to date mappings has been added to the project.

If no mapping for a given extension is found, neither in the file nor in the
ones contained in the library itself, the MIME type will default to
`application/octet-stream`.

## Configuration

The configuration is defined in the class `WebServerConfig`, with the use of
the custom `Property` annotation to specify the property name and optionally
a default value for each property.

There are multiple implementations of properties stores, under
`org.giogt.web.server.properties.store`.
A properties store is a basically a properties provider, able to retrieve the
properties from different sources (e.g., a map, a file, java system properties,
environment variables). There is also a composite properties store, which is
able to combine different properties stores in order, such that a previously
added one will override any other one added after.

The `ConfigProvider` class is parsing the `Property` annotations in the
`WebServerConfig` data class and filling the class fields with the
corresponding properties retrieved from the properties store.

The properties stores are added in an order such that:

- are first retrieved as Java system properties
  (with the `org.giogt.web.server.` prefix)
- if not found, they are retrieved as environment variables
  (with the `ORG_GIOGT_WEB_SERVER_` prefix, capitalised and with every `.`
  converted to `_`)
- if not found, the default value defined in the `WebServerConfig` `Property`
  annotation is used.

There is also an implementation of a  `FilePropertiesStore`, able to read
properties from a properties file, but for simplicity reasons it is not
currently used.
It would be trivial to add it, if needed.

## Limits

Although the HTTP specification does not define any limit for requests, a limit
must be enforced to avoid malicious clients to send very long requests, which
might result in an out of memory error that will crash the server.

This server enforces the following limits:

- request line: 8 KB
- request headers size: 16 KB
- request payload size: 64 MB

## Launching the server

The server can be launched using the `run` Gradle task:

```bash
./gradlew run
```

Alternatively, it can be run using Docker (see below).

## Docker

A docker image can be produced using the gradle task `buildDockerImage` or
running the script `docker-build.sh`, which is invoking the gradle task under
the hood.

Gradle plugin `com.bmuschko.docker-java-application` is used.
There is a known issue that prevents it to work with `Java` 13, therefore
please be sure to run gradle with `Java` 11 or 12 when invoking the
`buildDockerImage` task.

The task depends on the Gradle build task, gets the uber jar generated with the
`shadowJar` plugin under the `build` directory and copies it under the
`docker/web-server` directory, where the `Dockerfile` is defined and the Docker
image is built.

After the image has been build, it can be launched with the `docker-run.sh`
shell script, which automatically:

- maps the host port `8080` to the container port `8080`
- mounts the `root/main` directory to the `/web/root` directory
- sets the environment variable to configure `/web/root` as the server root directory

## Known limitations

Please find below the list of known limitations and improvements that could
have been implemented, but have been left out due to insufficient time.

### Keep-alive not implemented

The keep-alive behaviour was not implemented.

This is causing a problem, though, since the server is accepting both
`HTTP/1.0` (where keep-alive can be enabled with the `Connection: keep-alive`
header) and `HTTP/1.1` (where keep-alive is enabled by default) requests.
When using clients that will keep the connection alive and try to make another
request on the same connection, the server will just close the connection with
the client without processing the second request.

I noticed this issue while implementing the parallel `GET` integration test
trying to use the same Java `HTTP` client instance for all the requests.
In order to avoid this issue, in the integration tests the `HTTP` client is
newly created for each tests, so that it will never reuse an open connection.

### File writing locks

When receiving multiple `PUT` requests at the same time, a locking mechanism
should be implemented, so that only the first `PUT` request acquiring the lock
will upload the file and the other `PUT` requests will wait until the lock
is released or until a timeout occurs, whichever comes first.

There is a rudimentary lock system in place to avoid file conflicts, based on
`FileChannel` locks, but it should be improved and covered better in the
integration tests.

### Streaming

Currently, the request body is read completely and loaded in a byte array when
the request is parsed, in the `HttpRequestParser` class.
Similarly, the response body is written in a byte array in the HTTP method
handlers that build a response with a body.

This can cause a lot of memory consumption for big files and it could be
improved by streaming the data directly from the sockets to the file and the
other way around.

### Metrics

The application should expose metrics:
- the JVM standard ones (cpu, heap, threads, etc.)
- custom additional metrics, such as:
  * the number of connections since started (counter)
  * the number of concurrently opened connection at a given time (gauge)
  * number of responses, by response code

A very common solution for metrics is `Prometheus`.
- `Java` metrics already present in `JXM` can be exported using the
  [Prometheus JMX exporter](https://github.com/prometheus/jmx_exporter),
  which can be run as a `Java` agent and it will expose all the `JMX`
  metrics with the format expected by `Prometheus` automatically
- Custom metrics in `JVM` projects can be created and exported using
  the [Prometheus Java client](https://github.com/prometheus/client_java)

### Admin console

An admin console server could have been opened on another port, so that the
server could be administered with an admin client.

The admin console could provide operations such as getting the current
configuration, change the configuration on the fly, shutting down the server
gracefully.