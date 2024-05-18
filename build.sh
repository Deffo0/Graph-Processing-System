#!/bin/bash

mvn clean

mvn install -Dname=Server -DmainClass=org.example.server.GSPServer
docker build -t gsp-server:latest -f server.Dockerfile .