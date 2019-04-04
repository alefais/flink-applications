# Compile and run FraudDetection

## Compile
From inside the root directory `FraudDetection/`

`mvn clean install`

## Run
1. Start Flink client 
	`flink/bin/start-cluster.sh`

2. FraudDetection application can be run passing some arguments (if no command line argument is provided then default values defined in `fd.properties` file and `Constants` package are used). Optional arguments are:<ul><li>source parallelism degree</li><li>bolt parallelism degree</li><li>sink parallelism degree</li><li>source generation rate (default -1, generate at the max possible rate)</li><li>topology name (default FraudDetection)</li><li>execution mode (default local)</li></ul>

### Execution examples:
* No argument is passed (all the nodes have parallelism degree equal to 1, the source generation rate is the maximum possible, the execution is local).
`flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar`

<br>

* The parallelism degree is set to 4 for all the nodes in the topology and a specific file path is passed.
`flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --filepath /data/app/fd/credit-card.dat --pardeg 4`

3. Stop Flink client

`flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the Flink UI