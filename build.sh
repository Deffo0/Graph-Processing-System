#!/bin/bash

mvn clean

mvn install -Dname=Server -DmainClass=org.example.server.GSPServer
docker build -t gsp-server:latest -f server.Dockerfile .

# mvn install -Dname=Client -DmainClass=org.example.client.GSPClient
# docker build -t gsp-client:latest -f client.Dockerfile .