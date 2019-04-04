package SpikeDetection;

import Constants.SpikeDetectionConstants;
import Constants.SpikeDetectionConstants.Conf;
import Constants.SpikeDetectionConstants.Field;
import Util.config.Configuration;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Detects spikes in the measurements received by sensors
 * using a properly defined threshold.
 */
public class SpikeDetectorBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MovingAverageBolt.class);

    protected OutputCollector collector;
    protected Configuration config;
    protected TopologyContext context;

    private long t_start;
    private long t_end;
    private long processed;
    private int par_deg;
    private double spike_threshold;
    private long spikes;

    SpikeDetectorBolt(int p_deg) {
        par_deg = p_deg;     // bolt parallelism degree
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[SpikeDetectorBolt] Started ({} replicas).", par_deg);

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        processed = 0;               // total number of processed tuples
        spikes = 0;                  // total number of spikes detected

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;

        spike_threshold = config.getDouble(Conf.SPIKE_DETECTOR_THRESHOLD, SpikeDetectionConstants.DEFAULT_THRESHOLD);
    }

    @Override
    public void execute(Tuple tuple) {
        String deviceID = tuple.getString(0);
        double moving_avg_instant = tuple.getDouble(1);
        double next_property_value = tuple.getDouble(2);
        long timestamp = tuple.getLong(3);

        if (Math.abs(next_property_value - moving_avg_instant) > spike_threshold * moving_avg_instant) {
            spikes++;
            collector.emit(tuple, new Values(deviceID, moving_avg_instant, next_property_value, timestamp));
        }
        collector.ack(tuple);

        processed++;
        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

        System.out.println("[SpikeDetectorBolt] Processed " +
                processed + " tuples in " +
                t_elapsed + " ms. Source bandwidth is " +
                (processed / (t_elapsed / 1000)) +
                " tuples per second.");
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.DEVICE_ID, Field.MOVING_AVG, Field.VALUE, Field.TIMESTAMP));
    }
}
