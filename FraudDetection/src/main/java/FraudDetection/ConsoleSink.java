package FraudDetection;

import Constants.FraudDetectionConstants.Field;
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
 * The sink is in charge of printing the results.
 *
 * @author Alessandra Fais
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

        System.out.println("[ConsoleSink] Constructor invoked (" + par_deg + " replicas).");
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[ConsoleSink] Started.");

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        processed = 0;               // total number of processed tuples

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String entityID = tuple.getString(0);
        Double score = tuple.getDouble(1);
        String states = tuple.getString(2);
        Long timestamp = tuple.getLong(3);

        processed++;
        LOG.debug("[ConsoleSink] EntityID {}, score {}, states {}.", entityID, score, states);

        if (gen_rate != -1) {   // evaluate latency
            Long now = System.nanoTime();
            Long tuple_latency = (now - timestamp); // tuple latency in nanoseconds
            tuple_latencies.add(tuple_latency);
        }
        //collector.ack(tuple);

        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        if (processed == 0) {
            LOG.info("[ConsoleSink] No outliers found.");
            System.out.println("[ConsoleSink] No outliers found.");
        } else {
            if (gen_rate == -1) {  // evaluate bandwidth
                long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

                LOG.info("[ConsoleSink] Processed {} tuples (outliers) in {} ms. " +
                                "Bandwidth is {} tuples per second.",
                        processed, t_elapsed,
                        (processed / (t_elapsed / 1000))); // tuples per second
                System.out.println("[ConsoleSink] Bandwidth is " + (processed / (t_elapsed / 1000)) + " tuples per second.");
            } else {  // evaluate latency
                long acc = 0L;
                for (Long tl : tuple_latencies) {
                    acc += tl;
                }
                long avg_latency = acc / tuple_latencies.size(); // average latency in nanoseconds

                LOG.info("[ConsoleSink] Average latency: {} ms.", avg_latency / 1000000); // average latency in milliseconds
                System.out.println("[ConsoleSink] Average latency is " + avg_latency / 1000000 + " ms.");
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.ENTITY_ID, Field.SCORE, Field.STATES, Field.TIMESTAMP));
    }
}
