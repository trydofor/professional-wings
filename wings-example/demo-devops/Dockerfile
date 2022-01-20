# docker build -t wings/demo-devops .
# docker run -it --rm wings/demo-devops

FROM openjdk:8-jdk

ARG BOOT_JAR="target/demo-devops-*-SNAPSHOT.jar"
ARG BOOT_ENV="./wings-starter.env"
ARG BOOT_BSH="./wings-starter.sh"

EXPOSE 80
VOLUME /data
VOLUME /tmp

COPY ${BOOT_JAR} /opt/
COPY ${BOOT_ENV} /opt/
COPY ${BOOT_BSH} /opt/
RUN chmod +x /opt/*.sh

WORKDIR /opt/
# need bash to run
ENTRYPOINT ["/opt/wings-starter.sh","docker"]
