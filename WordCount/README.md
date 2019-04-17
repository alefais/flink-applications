# Compile and run WordCount

## Compile
From inside the root directory `WordCount/`

`mvn clean install`

## Run
1. Start Flink client: <br> `flink/bin/start-cluster.sh`

2. WordCount application can be run passing some arguments (if no command line argument is provided then default values defined in `wc.properties` file and `Constants` package are used). <br> The following arguments can be specified:<ul><li>`--sourcetype` source type (can be `file` or `generator`)</li><li>`--filepath` path of the dataset input file containing words (absolute or relative to `WordCount/` directory) <br> <b>NB:</b> this parameter is valid only if the source type is `file`</li><li>`--nsource` source parallelism degree</li><li>`--nbolt1` splitter bolt parallelism degree</li><li>`--nbolt2` counter bolt parallelism degree</li><li>`--nsink` sink parallelism degree</li><li>`--rate` source generation rate (default -1, generate at the max possible rate)</li><li>`--toponame` topology name (default WordCount)</li><li>`--mode` execution mode (default local)</li></ul> Instead of specifying the parallelism degree for each single node, it's possible to use `--pardeg` parallelism degree in order to assign the same parallelism degree to all the nodes in the topology.

### Execution examples:
* No argument is passed (the source reads the input file specified as property, all the nodes have parallelism degree equal to 1, the generation rate is the maximum possible, the execution is local): <br> `flink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar`

* The parallelism degree is explicitly set to 4 for all the nodes in the topology, the source type is `file` and a specific file path is passed <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar --sourcetype file --filepath ../data/app/wc/books.dat --pardeg 4`

* The parallelism degree is explicitly set to 4 for all the nodes in the topology and the source type is `generator` <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `flink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar --sourcetype generator --pardeg 4`

3. Stop Flink client: <br> `flink/bin/stop-cluster.sh`

4. Check logs and output under `flink/log/` or see results from the <b>Flink UI</b>.