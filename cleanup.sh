#!/bin/bash


# wrap in a function
freq() {
  cd /home/karim/DistribytedSystems/Project/Graph-Processing-System ;\
    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/karim/DistribytedSystems/Project/Graph-Processing-System/target/classes org.example.Start $1 2 50

    docker stop gsp-server
    docker rm gsp-server
    mkdir -p src/main/resources/freq_slow/$1
    mv src/main/resources/*.log src/main/resources/freq_slow/$1
}


writes() {
  cd /home/karim/DistribytedSystems/Project/Graph-Processing-System ;\
    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/karim/DistribytedSystems/Project/Graph-Processing-System/target/classes org.example.Start 1000 2 $1

    docker stop gsp-server
    docker rm gsp-server
    mkdir -p src/main/resources/writes_slow/$1
    mv src/main/resources/*.log src/main/resources/writes_slow/$1
}


nodes() {
  cd /home/karim/DistribytedSystems/Project/Graph-Processing-System ;\
    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/karim/DistribytedSystems/Project/Graph-Processing-System/target/classes org.example.Start 1000 $1 50

    docker stop gsp-server
    docker rm gsp-server
    mkdir -p src/main/resources/nodes_slow/$1
    mv src/main/resources/*.log src/main/resources/nodes_slow/$1
}

stress() {
  cd /home/karim/DistribytedSystems/Project/Graph-Processing-System ;\
    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/karim/DistribytedSystems/Project/Graph-Processing-System/target/classes org.example.Start 1000 $1 50

    docker stop gsp-server
    docker rm gsp-server
    mkdir -p src/main/resources/stress/$1
    mv src/main/resources/*.log src/main/resources/stress/$1
}


docker stop gsp-server
docker rm gsp-server


# for i in {1000..10000..1000}
# do
#   freq $i
#   sleep 2
# done

# for i in {0..100..10}
# do
#   writes $i
#   sleep 2
# done

# for i in {1..5}
# do
#   nodes $i
#   sleep 2
# done

for i in {5..15}
do
  stress $i
  sleep 2
done

