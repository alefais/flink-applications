package FraudDetection;

import Constants.FraudDetectionConstants.Field;
import Util.config.Configuration;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.storm.api.FlinkClient;
import org.apache.flink.storm.api.FlinkLocalCluster;
import org.apache.flink.storm.api.FlinkSubmitter;
import org.apache.flink.storm.api.FlinkTopology;
import org.apache.flink.storm.wrappers.BoltWrapper;
import org.apache.flink.storm.wrappers.SpoutWrapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.NotAliveException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The topology entry class. The Storm compatible API is used in order to submit
 * a Storm topology to Flink. The used Storm classes are replaced with their
 * Flink counterparts in the Storm client code that assembles the topology.
 *
 * See https://ci.apache.org/projects/flink/flink-docs-stable/dev/libs/storm_compatibility.html
 *
 * @author Alessandra Fais
 */
public class FraudDetection {

    private static final Logger LOG = LoggerFactory.getLogger(FileParserSpout.class);

    public static void main(String[] args) throws Exception {
        //topologyFlinkAdaptation(args);
        spoutsANDboltsFlinkAdaptation(args);
    }

    /**
     * Execute the Storm topology.
     * @param args command line arguments
     * @throws Exception
     */
    private static void topologyFlinkAdaptation(String[] args) throws Exception {
        if (args.length == 0) {
            String alert =
                    "In order to correctly run FraudDetection app you can to pass the following (optional) arguments:\n" +
                            "Optional arguments:\n" +
                            " file path (default specified in fd.properties)\n" +
                            " source parallelism degree (default 1)\n" +
                            " bolt parallelism degree (default 1)\n" +
                            " sink parallelism degree (default 1)\n" +
                            " source generation rate (default -1, generate at the max possible rate)\n" +
                            " topology name (default FraudDetection)\n" +
                            " execution mode (default local)";
            LOG.error(alert);
        } else {
            // parse command line arguments
            String file_path = (args.length > 0) ? args[0] : null;
            int source_par_deg = (args.length > 1) ? new Integer(args[1]) : 1;
            int bolt_par_deg = (args.length > 2) ? new Integer(args[2]) : 1;
            int sink_par_deg = (args.length > 3) ? new Integer(args[3]) : 1;
            int gen_rate = (args.length > 4) ? new Integer(args[4]) : -1;
            String topology_name = (args.length > 5) ? args[5] : "FraudDetection";
            String ex_mode = (args.length > 6) ? args[6] : "local";

            // prepare the topology
            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("spout", new FileParserSpout(file_path, ",", gen_rate, source_par_deg), source_par_deg);

            builder.setBolt("fraud_predictor", new FraudPredictorBolt(bolt_par_deg), bolt_par_deg)
                    .fieldsGrouping("spout", new Fields(Field.ENTITY_ID));

            builder.setBolt("sink", new ConsoleSink(sink_par_deg, gen_rate), sink_par_deg)
                    .shuffleGrouping("fraud_predictor");

            // prepare the configuration:
            Config conf = new Config();
            conf.setDebug(false);
            conf.setNumWorkers(1);
            try {
                // load configuration
                String cfg = "/frauddetection/fd.properties";
                Properties p = loadProperties(cfg);

                conf = Configuration.fromProperties(p);
                LOG.info("Loaded configuration file {}.", cfg);
            } catch (IOException e) {
                LOG.error("Unable to load configuration file.", e);
                throw new RuntimeException("Unable to load configuration file.", e);
            }

            // build the topology
            FlinkTopology topology = FlinkTopology.createTopology(builder);
            //topology.execute();

            // run the topology
            try {
                if (ex_mode.equals("local"))
                    runTopologyLocally(topology, topology_name, conf, 120); // 2 minutes
                else if (ex_mode.equals("remote"))
                    runTopologyRemotely(topology, topology_name, conf, 120);
            } catch (InterruptedException e) {
                LOG.error("Interrupted topology.", e);
            }
        }
    }

    /**
     * Embed Storm operators in the Flink streaming program.
     * @param args command line arguments
     * @throws Exception
     */
    private static void spoutsANDboltsFlinkAdaptation(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        if (params.getNumberOfParameters() == 0) {
            String alert =
                    "In order to correctly run FraudDetection app you need to pass the following arguments:\n" +
                            " file path\n" +
                            "Optional arguments:\n" +
                            " source parallelism degree (default 1)\n" +
                            " bolt parallelism degree (default 1)\n" +
                            " sink parallelism degree (default 1)\n" +
                            " source generation rate (default -1, generate at the max possible rate)\n" +
                            " topology name (default FraudDetection)\n" +
                            " execution mode (default local)";
            LOG.error(alert);
        } else {
            // parse command line arguments
            String file_path = params.getRequired("filepath");
            int source_par_deg = params.getInt("nsource", 1);
            int bolt_par_deg = params.getInt("nbolt", 1);
            int sink_par_deg = params.getInt("nsink", 1);
            int gen_rate = params.getInt("rate", -1);
            String topology_name = params.get("toponame", "FraudDetection");
            String ex_mode = params.get("mode", "local");

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.getConfig().setGlobalJobParameters(params);

            // set parallelism for all activities in the topology
            int pardeg = params.getInt("pardeg", 1);
            env.setParallelism(pardeg);

            System.out.println("[main] Command line arguments parsed.");

            // create the topology
            DataStream<Tuple3<String, String, Long>> source =
                env
                    .addSource(
                        new SpoutWrapper<Tuple3<String, String, Long>>(
                                new FileParserSpout(file_path, ",", gen_rate, source_par_deg)),
                                "file_parser")
                        .returns(Types.TUPLE(Types.STRING, Types.STRING, Types.LONG))   // output type
                        //.setParallelism(source_par_deg)
                        .keyBy(0);

            System.out.println("[main] Spout created.");

            DataStream<Tuple4<String, Double, String, Long>> fraud_predictor =
                source
                    .transform(
                        "fraud_predictor", // operator name
                            TypeExtractor.getForObject(new Tuple4<>("", 0.0, "", 0L)), // output type
                            new BoltWrapper<>(new FraudPredictorBolt(bolt_par_deg)));
                        //.setParallelism(bolt_par_deg);

            System.out.println("[main] Bolt created.");

            DataStream<Tuple4<String, Double, String, Long>> sink =
                fraud_predictor
                    .transform(
                            "sink",
                            TypeExtractor.getForObject(new Tuple4<>("", 0.0, "", 0L)), // output type
                            new BoltWrapper<>(new ConsoleSink(sink_par_deg, gen_rate)));
                        //.setParallelism(sink_par_deg);

            System.out.println("[main] Sink created.");

            // prepare the configuration
            String cfg = "/frauddetection/fd.properties";
            params = ParameterTool.fromPropertiesFile(FraudDetection.class.getResourceAsStream(cfg));
            env.getConfig().setGlobalJobParameters(params);

            System.out.println("[main] executing topology...");
            env.execute(topology_name);
        }
    }

    /**
     * Run the topology locally.
     * @param topology the topology to be executed
     * @param topology_name the name of the topology
     * @param conf the configurations for the execution
     * @param runtime_seconds for how much time the topology will run
     * @throws InterruptedException
     */
    private static void runTopologyLocally(FlinkTopology topology, String topology_name, Config conf, int runtime_seconds)
            throws InterruptedException {

        LOG.info("[main] Starting Flink in local mode to run for {} seconds.", runtime_seconds);
        FlinkLocalCluster cluster = new FlinkLocalCluster();//FlinkLocalCluster.getLocalCluster();

        try {
            cluster.submitTopology(topology_name, conf, topology);
            LOG.info("[main] Topology {} submitted.", topology_name);
        } catch (Exception e) {
            LOG.error("Error in running topology locally.", e);
        }

        Thread.sleep((long) runtime_seconds * 1000);

        cluster.shutdown();
        LOG.info("[main] Topology {} finished. Flink local cluster was shut down.", topology_name);
    }

    /**
     * Run the topology remotely.
     * @param topology the topology to be executed
     * @param topology_name the name of the topology
     * @param conf the configurations for the execution
     * @throws InterruptedException
     */
    private static void runTopologyRemotely(FlinkTopology topology, String topology_name, Config conf, int runtime_seconds)
            throws InterruptedException {

        // if we execute via bin/flink values from flink-conf.yaml are set by FlinkSubmitter
        // conf.put(Config.NIMBUS_HOST, "localhost");
        // conf.put(Config.NIMBUS_THRIFT_PORT, new Integer(6123));
        try {
            FlinkSubmitter.submitTopology(topology_name, conf, topology);
            LOG.info("[main] Topology {} submitted.", topology_name);
        } catch (AlreadyAliveException | InvalidTopologyException e) {
            LOG.error("Error in running topology remotely.", e);
        }

        Thread.sleep((long) runtime_seconds * 1000);

        try {
            FlinkClient.getConfiguredClient(conf).killTopology(topology_name);
        } catch (NotAliveException e) {
            LOG.error("Error in killing topology.", e);
        }
    }

    /**
     * Load configuration properties for the application.
     * @param filename the name of the properties file
     * @return the persistent set of properties loaded from the file
     * @throws IOException
     */
    private static Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        InputStream is = FraudDetection.class.getResourceAsStream(filename);
        if (is != null) {
            properties.load(is);
            is.close();
        }
        LOG.info("[main] Properties loaded: {}.", properties.toString());
        return properties;
    }
}
