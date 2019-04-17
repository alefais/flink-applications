package WordCount;

import Constants.WordCountConstants.Field;
import Util.config.Configuration;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Counts words' occurrences.
 */
public class CounterBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(CounterBolt.class);

    protected OutputCollector collector;
    protected Configuration config;
    protected TopologyContext context;

    private final Map<String, MutableLong> counts = new HashMap<>();

    private long t_start;
    private long t_end;
    private int par_deg;
    private long bytes;

    CounterBolt(int p_deg) {
        par_deg = p_deg;     // bolt parallelism degree
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[CounterBolt] Started ({} replicas).", par_deg);

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        bytes = 0;                   // total number of processed bytes

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getStringByField(Field.WORD);
        long timestamp = tuple.getLongByField(Field.TIMESTAMP);

        if (word != null) {
            bytes += word.length();

            MutableLong count = counts.computeIfAbsent(word, k -> new MutableLong(0));
            count.increment();

            collector.emit(new Values(word, count.get(), timestamp));
        }
        collector.ack(tuple);

        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

        System.out.println("[CounterBolt] Processed " + (bytes / 1048576) + " in " + t_elapsed + " ms.");
        System.out.println("[CounterBolt] Bandwidth is " +
                (bytes / 1048576) / (t_elapsed / 1000) + " MB per second.");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.WORD, Field.COUNT, Field.TIMESTAMP));
    }
}

