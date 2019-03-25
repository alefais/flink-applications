# Compile and run FraudDetection

## Compile
From inside the root directory `FraudDetection/`

`mvn clean install`

## Run
1. Start Flink client 

`flink/bin/start-cluster.sh`

2. Run passing the input dataset and the parallelism degree for all nodes

`flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --filepath /data/app/fd/credit-card.dat --pardeg 4`

3. Stop Flink client

`flink/bin/stop-cluster.sh`