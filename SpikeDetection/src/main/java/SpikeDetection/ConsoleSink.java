package SpikeDetection;

import Constants.SpikeDetectionConstants.Field;
import Util.config.Configuration;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 *  @author  Alessandra Fais
 *  @version May 2019
 *
 *  Sink node that receives and prints the results.
 */
public class ConsoleSink extends BaseRichBolt {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleSink.class);

    protected OutputCollector collector;
    protected Configuration config;
    protected TopologyContext context;

    private long t_start;
    private long t_end;
    private long processed;
    private int par_deg;
    private int gen_rate;

    private ArrayList<Long> tuple_latencies;

    ConsoleSink(int p_deg, int g_rate) {
        par_deg = p_deg;         // sink parallelism degree
        gen_rate = g_rate;       // generation rate of the source (spout)
        tuple_latencies = new ArrayList<>();
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[Sink] started ({} replicas)", par_deg);

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        processed = 0;               // total number of processed tuples

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String deviceID = tuple.getString(0);
        double moving_avg_instant = tuple.getDouble(1);
        double next_property_value = tuple.getDouble(2);
        long timestamp = tuple.getLong(3);

        LOG.debug("[Sink] outlier: deviceID " + deviceID +
                ", incremental_average " + moving_avg_instant +
                ", next_value " + next_property_value);

        if (gen_rate != -1) {   // evaluate latency
            long now = System.nanoTime();
            long tuple_latency = (now - timestamp); // tuple latency in nanoseconds
            tuple_latencies.add(tuple_latency);
        }
        collector.ack(tuple);

        processed++;
        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        if (processed == 0) {
            System.out.println("[Sink] processed tuples: " + processed);
        } else {
            if (gen_rate == -1) {  // evaluate bandwidth
                long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

                System.out.println("[Sink] processed tuples: " + processed +
                                    ", bandwidth: " +  processed / (t_elapsed / 1000) +
                                    " tuples/s");
            } else {  // evaluate latency
                long acc = 0L;
                for (Long tl : tuple_latencies) {
                    acc += tl;
                }
                double avg_latency = (double) acc / tuple_latencies.size(); // average latency in nanoseconds

                System.out.println("[Sink] processed tuples: " + processed +
                                    ", latency: " +  avg_latency / 1000000 + // average latency in milliseconds
                                    " ms");
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.DEVICE_ID, Field.MOVING_AVG, Field.VALUE, Field.TIMESTAMP));
    }
}

