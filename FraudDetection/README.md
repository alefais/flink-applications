# Compile and run FraudDetection

## Compile
From inside the root directory `FraudDetection/`

`mvn clean install`

## Run
1. Start Flink client: <br> `flink/bin/start-cluster.sh`

2. FraudDetection application can be run passing some arguments (if no command line argument is provided then default values defined in `fd.properties` file and `Constants` package are used). <br> The following arguments can be specified:<ul><li>`--file` path of the dataset input file containing credit card transactions (absolute or relative to `FraudDetection/` directory)</li><li>`--nsource` source parallelism degree</li><li>`--npredictor` predictor bolt parallelism degree</li><li>`--nsink` sink parallelism degree</li><li>`--rate` source generation rate (default -1, generate at the max possible rate)</li><li>`--toponame` topology name (default FraudDetection)</li><li>`--mode` execution mode (default local)</li></ul> Instead of specifying the parallelism degree for each single node, it's possible to use `--pardeg` parallelism degree in order to assign the same parallelism degree to all the nodes in the topology.

### Execution examples:
* No argument is passed (all the nodes have parallelism degree equal to 1, the source generation rate is the maximum possible, the execution is local): <br> `flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar`

* The parallelism degree is explicitly set for all the nodes in the topology and a specific file path is passed <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --file ~/data/app/fd/credit-card.dat --nsource 1 --npredictor 2 --nsink 1 --rate 1000`

* The parallelism degree is set to 4 for all the nodes in the topology and a specific file path is passed <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --file ~/data/app/fd/credit-card.dat --pardeg 4`

3. Stop Flink client: <br> `flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the <b>Flink UI</b>.