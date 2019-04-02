package SpikeDetection;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Calculates the average over a window for distinct elements.
 * http://github.com/surajwaghulde/storm-example-projects
 *
 * @author Alessandra Fais
 */
public class MovingAverageBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MovingAverageBolt.class);

    protected OutputCollector collector;
    protected Configuration config;
    protected TopologyContext context;

    private int movingAverageWindow;
    private Map<String, LinkedList<Double>> deviceIDtoStreamMap;
    private Map<String, Double> deviceIDtoSumOfEvents;

    private long t_start;
    private long t_end;
    private long processed;
    private int par_deg;

    MovingAverageBolt(int p_deg) {
        par_deg = p_deg;     // bolt parallelism degree
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[MovingAverageBolt] Started ({} replicas).", par_deg);

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        processed = 0;               // total number of processed tuples

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;

        movingAverageWindow = config.getInt(Conf.MOVING_AVERAGE_WINDOW, 1000);
        deviceIDtoStreamMap = new HashMap<>();
        deviceIDtoSumOfEvents = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple) {
        String deviceID = tuple.getString(0);
        double next_property_value = tuple.getDouble(1);
        long timestamp = tuple.getLong(2);

        double moving_avg_instant = movingAverage(deviceID, next_property_value);

        /*if (tuple.getSourceStreamId().equalsIgnoreCase(BaseConstants.BaseStream.Marker_STREAM_ID)) {
            collector.emit(
                    BaseConstants.BaseStream.Marker_STREAM_ID,
                    new Values(deviceID, moving_avg_instant, next_property_value, "spike detected",
                            tuple.getLongByField(BaseConstants.BaseField.MSG_ID),
                            tuple.getLongByField(BaseConstants.BaseField.SYSTEMTIMESTAMP)));
        } else*/
        collector.emit(tuple, new Values(deviceID, moving_avg_instant, next_property_value, timestamp));

        LOG.debug("[MovingAverageBolt] Sending: DeviceID {} avg {} next_value {}",
                deviceID, moving_avg_instant, next_property_value);

        processed++;
        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

        LOG.info("[MovingAverageBolt] Processed {} tuples in {} ms. " +
                        "Source bandwidth is {} tuples per second.",
                processed, t_elapsed,
                processed / (t_elapsed / 1000));  // tuples per second

        System.out.println("[MovingAverageBolt] Bandwidth is " + (processed / (t_elapsed / 1000)) + " tuples per second.");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.DEVICE_ID, Field.MOVING_AVG, Field.VALUE, Field.TIMESTAMP));
    }

    private double movingAverage(String deviceID, double nextDouble) {
        LinkedList<Double> valueList = new LinkedList<>();
        double sum = 0.0;

        if (deviceIDtoStreamMap.containsKey(deviceID)) {
            valueList = deviceIDtoStreamMap.get(deviceID);
            sum = deviceIDtoSumOfEvents.get(deviceID);
            if (valueList.size() > movingAverageWindow - 1) {
                double valueToRemove = valueList.removeFirst();
                sum -= valueToRemove;
            }
            valueList.addLast(nextDouble);
            sum += nextDouble;
            deviceIDtoSumOfEvents.put(deviceID, sum);
            deviceIDtoStreamMap.put(deviceID, valueList);
            return sum / valueList.size();
        } else {
            valueList.add(nextDouble);
            deviceIDtoStreamMap.put(deviceID, valueList);
            deviceIDtoSumOfEvents.put(deviceID, nextDouble);
            return nextDouble;
        }
    }
}
