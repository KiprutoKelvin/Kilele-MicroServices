#!/bin/bash

echo "👷🚧 Starting Movie Catalog Service Instances..."

# Instance 1 - port 8088
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8088 \
 --eureka.instance.instance-id=catalog-8088 &
PID1=$!

# Instance 2 - port 8084
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8084 \
 --eureka.instance.instance-id=catalog-8084 &
PID2=$!

# Instance 3 - port 8085
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar \
 --server.port=8085 \
 --eureka.instance.instance-id=catalog-8085 &
PID3=$!

echo ""
echo "💡 All instances started!"
echo "🟢 catalog-8088 running with PID $PID1"
echo "🟢 catalog-8084 running with PID $PID2"
echo "🟢 catalog-8085 running with PID $PID3"
echo ""
echo "⏳ These instances will run for 20 minutes..."

# Sleep for 1200 seconds = 20 minutes
sleep 1200

echo ""
echo "🛑 Stopping Movie Catalog Service Instances..."

kill $PID1 2>/dev/null && echo "🔴 Stopped catalog-8088"
kill $PID2 2>/dev/null && echo "🔴 Stopped catalog-8084"
kill $PID3 2>/dev/null && echo "🔴 Stopped catalog-8085"

echo ""
echo "✔️ All instances stopped after 20 minutes."
