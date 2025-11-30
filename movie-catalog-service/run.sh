#!/bin/bash

echo "👷🚧:Starting Movie Catalog Service Instances..."

# Instance 1 - port 8088
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8088 \
 --eureka.instance.instance-id=catalog-8088 &

# Instance 2 - port 8084
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8084 \
 --eureka.instance.instance-id=catalog-8084 &

# Instance 3 - port 8085
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8085 \
 --eureka.instance.instance-id=catalog-8085 &

echo "💡:All instances started!"
