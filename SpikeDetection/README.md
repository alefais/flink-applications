# Compile and run SpikeDetection

## Compile
From inside the root directory `SpikeDetection/`

`mvn clean install`

## Run
1. Start Flink client: <br> `flink/bin/start-cluster.sh`

2. SpikeDetection application can be run passing some arguments (if no command line argument is provided then default values defined in `sd.properties` file and `Constants` package are used). <br> The following arguments can be specified:<ul><li>`--filepath` path of the dataset input file (absolute or relative to `SpikeDetection/` directory)</li><li>`--nsource` source parallelism degree</li><li>`--nbolt1` moving average bolt parallelism degree</li><li>`--nbolt2` spike detector bolt parallelism degree</li><li>`--nsink` sink parallelism degree</li><li>`--rate` source generation rate (default -1, generate at the max possible rate)</li><li>`--toponame` topology name (default SpikeDetection)</li><li>`--mode` execution mode (default local)</li></ul> Instead of specifying the parallelism degree for each single node, it's possible to use `--pardeg` parallelism degree in order to assign the same parallelism degree to all the nodes in the topology.

### Execution examples:
* No argument is passed (all the nodes have parallelism degree equal to 1, the source generation rate is the maximum possible, the execution is local): <br> `flink run -c SpikeDetection.SpikeDetection target/SpikeDetection-1.0-SNAPSHOT-jar-with-dependencies.jar`

* The parallelism degree is set to 4 for all the nodes in the topology and a specific file path is passed <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c SpikeDetection.SpikeDetection target/SpikeDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --filepath /data/app/sd/sensors.dat --pardeg 4`

3. Stop Flink client: <br> `flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the <b>Flink UI</b>.