#!/bin/bash

docker rm -f gsp-server
mkdir -p src/main/resources/nodes/$1
mv src/main/resources/*.log src/main/resources/nodes/$1