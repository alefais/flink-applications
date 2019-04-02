# Compile and run FraudDetection

## Compile
From inside the root directory `FraudDetection/`

`mvn clean install`

## Run
1. Start Flink client 

`flink/bin/start-cluster.sh`

2. In order to correctly run FraudDetection app you need to pass the input file path as mandatory argument.<br>
Optional arguments are:
- source parallelism degree (default 1)
- bolt parallelism degree (default 1)
- sink parallelism degree (default 1)
- source generation rate (default -1, generate at the max possible rate)
- topology name (default FraudDetection)
- execution mode (default local)

### Execution example:
The parallelism degree is set to 4 for all the nodes in the topology.

`flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --filepath /data/app/fd/credit-card.dat --pardeg 4`

3. Stop Flink client

`flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the Flink UI