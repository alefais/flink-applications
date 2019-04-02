package SpikeDetection;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.storm.wrappers.BoltWrapper;
import org.apache.flink.storm.wrappers.SpoutWrapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The topology entry class.
 *
 * @author Alessandra Fais
 */
public class SpikeDetection {

    private static final Logger LOG = LoggerFactory.getLogger(FileParserSpout.class);

    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        if (params.getNumberOfParameters() == 0) {
            String alert =
                    "In order to correctly run SpikeDetection app you need to pass the following arguments:\n" +
                    " file path\n" +
                    "Optional arguments:\n" +
                    " source parallelism degree (default 1)\n" +
                    " bolt1 parallelism degree (default 1)\n" +
                    " bolt2 parallelism degree (default 1)\n" +
                    " sink parallelism degree (default 1)\n" +
                    " source generation rate (default -1, generate at the max possible rate)\n" +
                    " topology name (default SpikeDetection)\n" +
                    " execution mode (default local)";
            LOG.error(alert);
        } else {
            // parse command line arguments
            String file_path = params.getRequired("filepath");
            int source_par_deg = params.getInt("nsource", 1);
            int bolt1_par_deg = params.getInt("nbolt1", 1);
            int bolt2_par_deg = params.getInt("nbolt2", 1);
            int sink_par_deg = params.getInt("nsink", 1);
            int gen_rate = params.getInt("rate", -1);
            String topology_name = params.get("toponame", "SpikeDetection");
            String ex_mode = params.get("mode", "local");

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.getConfig().setGlobalJobParameters(params);

            // set parallelism for all activities in the topology
            int pardeg = params.getInt("pardeg", 1);
            env.setParallelism(pardeg);

            System.out.println("[main] Command line arguments parsed.");

            // create the topology
            DataStream<Tuple3<String, Double, Long>> source =
                    env
                            .addSource(
                                    new SpoutWrapper<Tuple3<String, Double, Long>>(
                                            new FileParserSpout(file_path, gen_rate, source_par_deg)),
                                    "file_parser")
                            .returns(Types.TUPLE(Types.STRING, Types.DOUBLE, Types.LONG))   // output type
                            //.setParallelism(source_par_deg)
                            .keyBy(0);

            System.out.println("[main] Spout created.");

            DataStream<Tuple4<String, Double, Double, Long>> moving_average_bolt =
                    source
                            .transform(
                                    "moving_average", // operator name
                                    TypeExtractor.getForObject(new Tuple4<>("", 0.0, 0.0, 0L)), // output type
                                    new BoltWrapper<>(new MovingAverageBolt(bolt1_par_deg)));
            //.setParallelism(bolt1_par_deg);

            System.out.println("[main] Bolt MovingAverage created.");

            DataStream<Tuple4<String, Double, Double, Long>> spike_detector_bolt =
                    moving_average_bolt
                            .transform(
                                    "spike_detector", // operator name
                                    TypeExtractor.getForObject(new Tuple4<>("", 0.0, 0.0, 0L)), // output type
                                    new BoltWrapper<>(new SpikeDetectorBolt(bolt2_par_deg)));
            //.setParallelism(bolt2_par_deg);

            System.out.println("[main] Bolt SpikeDetector created.");

            DataStream<Tuple4<String, Double, Double, Long>> sink =
                    spike_detector_bolt
                            .transform(
                                    "sink",
                                    TypeExtractor.getForObject(new Tuple4<>("", 0.0, 0.0, 0L)), // output type
                                    new BoltWrapper<>(new ConsoleSink(sink_par_deg, gen_rate)));
            //.setParallelism(sink_par_deg);

            System.out.println("[main] Sink created.");

            // prepare the configuration
            String cfg = "/spikedetection/sd.properties";
            params = ParameterTool.fromPropertiesFile(SpikeDetection.class.getResourceAsStream(cfg));
            env.getConfig().setGlobalJobParameters(params);

            System.out.println("[main] executing topology...");
            env.execute(topology_name);
        }
    }
}
