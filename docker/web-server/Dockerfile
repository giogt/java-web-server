FROM openjdk:13.0.1
LABEL maintainer="Giorgio Carlo Gili Tos <giorgio.gilitos@gmail.com>"

RUN mkdir -p /web-server/lib && mkdir -p /web-server/root && mkdir -p /web-server/cfg
COPY app/web-server-all.jar /web-server/lib

EXPOSE 80
CMD ["java", "-jar", "/web-server/lib/web-server-all.jar"]
