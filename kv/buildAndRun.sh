#!/bin/sh
mvn clean package && docker build -t de.hsb.app/kv .
docker rm -f kv || true && docker run -d -p 8080:8080 -p 4848:4848 --name kv de.hsb.app/kv 
