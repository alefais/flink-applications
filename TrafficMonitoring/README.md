# Compile and run TrafficMonitoring

## Compile
From inside the root directory `TrafficMonitoring/`

`mvn clean install`

## Run
1. Start Flink client: <br> `flink/bin/start-cluster.sh`

2. TrafficMonitoring application can be run passing some arguments (if no command line argument is provided then default values defined in `tm.properties` file and `Constants` package are used). <br> The following arguments can be specified:<ul><li>`--city` city to monitor (this value can be `beijing` or `dublin`)</li><li>`--nsource` source parallelism degree</li><li>`--nbolt1` map matcher bolt parallelism degree</li><li>`--nbolt2` speed calculator bolt parallelism degree</li><li>`--nsink` sink parallelism degree</li><li>`--rate` source generation rate (default -1, generate at the max possible rate)</li><li>`--toponame` topology name (default TrafficMonitoring)</li><li>`--mode` execution mode (default local)</li></ul> Instead of specifying the parallelism degree for each single node, it's possible to use `--pardeg` parallelism degree in order to assign the same parallelism degree to all the nodes in the topology.

### Execution examples:
* No argument is passed (the monitored city is Beijing, all the nodes have parallelism degree equal to 1, the source generation rate is 1000, the execution is local): <br> `flink run -c TrafficMonitoring.TrafficMonitoring target/TrafficMonitoring-1.0-SNAPSHOT-jar-with-dependencies.jar`

* The parallelism degree is set to 4 for all the nodes in the topology, the city is set to Beijing and the source generation rate is 1000 <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c TrafficMonitoring.TrafficMonitoring target/TrafficMonitoring-1.0-SNAPSHOT-jar-with-dependencies.jar --city beijing --pardeg 4 --rate 1000`

3. Stop Flink client: <br> `flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the <b>Flink UI</b>.