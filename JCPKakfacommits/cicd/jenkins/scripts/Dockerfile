FROM gcr.io/yantriks-cicd-infra/alpine-jre:1.2
MAINTAINER vinayak.jog@yantriks.com
ENV REFRESHED_AT 30/08/2019

# create a directory for the java app to run
# we need a work dir for the java apps
RUN mkdir -p /opt/api/work

# create the dir where we will mount out config map
RUN mkdir /configmap
RUN mkdir /app
COPY ./commits-exts.jar /opt/api/commits-exts.jar

COPY ./startup.sh /usr/bin/startup.sh
RUN chmod +x /usr/bin/startup.sh
CMD ["/bin/bash", "startup.sh"]
