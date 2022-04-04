#!/bin/sh

# Start common-kafka-consumer Server
#cd /configmap && java $JAVA_OPTS $JVM_MEM_ARGS $JVM_PERF_ARGS -jar /opt/api/microservice-sourcing-tsc-optimizer-app.jar
cd /configmap && java $JAVA_OPTS $JVM_MEM_ARGS $JVM_PERF_ARGS -jar /opt/api/event.jar

