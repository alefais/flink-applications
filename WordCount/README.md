# Compile and run the Word Count application

## Compile
From inside the root directory `WordCount/`

`mvn clean install`

## Run
1. Start Flink local cluster: <br> `$FLINK_HOME/bin/start-cluster.sh`

2. WordCount application can be run passing some arguments (if no command line argument is provided then default values defined in [wc.properties](https://github.com/alefais/flink-applications/blob/master/WordCount/src/main/resources/wordcount/wc.properties) and [Constants](https://github.com/alefais/flink-applications/tree/master/WordCount/src/main/java/Constants) package are used). <br> The following arguments can be specified:<ul><li>`--sourcetype` source type (can be `file` or `generator`)</li><li>`--filepath` path of the dataset input file containing words (absolute or relative to `WordCount/` directory) <br> <b>NB:</b> this parameter is valid only if the source type is `file`</li><li>`--nsource` source parallelism degree</li><li>`--nsplitter` splitter bolt parallelism degree</li><li>`--ncounter` counter bolt parallelism degree</li><li>`--nsink` sink parallelism degree</li><li>`--rate` source generation rate (default -1, generate at the max possible rate)</li><li>`--toponame` topology name (default WordCount)</li><li>`--mode` execution mode (default local)</li></ul> Instead of specifying the parallelism degree for each single node, it's possible to use `--pardeg` parallelism degree in order to assign the same parallelism degree to all the nodes in the topology.

### Execution examples:
* Run the application with no arguments (parameters such as the input data set and the parallelism degree of the nodes assume the default values, the source generation rate is the maximum possible, the execution is local): <br> `$FLINK_HOME/binflink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar`

* Run the application explicitly defining the data set file path and the parallelism degree equal for all the nodes in the topology <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `$FLINK_HOME/binflink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar --sourcetype file --filepath data/book.dat --pardeg 1`

* Run the application configuring the source to randomly generate sentences and setting the same parallelism degree for all the nodes in the topology <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case): <br> `$FLINK_HOME/binflink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar --sourcetype generator --pardeg 1`

* Run the application explicitly defining the parallelism degree for all the nodes in the topology and limiting the generation rate <br> (<b>NB:</b> for each unspecified parameter the default value is used, as in the previous case, in particular the data set file path that is defined and can be modified in [wc.properties](https://github.com/alefais/flink-applications/blob/master/WordCount/src/main/resources/wordcount/wc.properties)): <br> `$FLINK_HOME/binflink run -c WordCount.WordCount target/WordCount-1.0-SNAPSHOT-jar-with-dependencies.jar --nsource 1 --nsplitter 2 --ncounter 2 --nsink 1 --rate 10000`

3. Stop Flink local cluster: <br> `$FLINK_HOME/bin/stop-cluster.sh`

4. Check logs and output under `$FLINK_HOME/log/` or see results from the <b>Flink UI</b> available at `http://localhost:8081`.