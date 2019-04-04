package FraudDetection;

import Constants.BaseConstants.BaseField;
import Constants.FraudDetectionConstants.*;
import MarkovModelPrediction.MarkovModelPredictor;
import MarkovModelPrediction.ModelBasedPredictor;
import MarkovModelPrediction.Prediction;
import Util.config.Configuration;
import org.apache.commons.lang.StringUtils;
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
 * The bolt is in charge of implementing outliers detection.
 * Given a transaction sequence of a customer, there is a
 * probability associated with each path of state transition,
 * which indicates the chances of fraudolent activities.
 */
public class FraudPredictorBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(FraudPredictorBolt.class);

    protected OutputCollector collector;
    protected Configuration config;
    protected TopologyContext context;

    private ModelBasedPredictor predictor;
    private long t_start;
    private long t_end;
    private long processed;
    private long outliers;
    private int par_deg;

    FraudPredictorBolt(int p_deg) {
        par_deg = p_deg;     // bolt parallelism degree
    }

    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector outputCollector) {
        LOG.info("[FraudPredictorBolt] Started ({} replicas).", par_deg);

        t_start = System.nanoTime(); // bolt start time in nanoseconds
        processed = 0;               // total number of processed tuples
        outliers = 0;                // total number of outliers

        config = Configuration.fromMap(stormConf);
        context = topologyContext;
        collector = outputCollector;

        String strategy = config.getString(Conf.PREDICTOR_MODEL);
        if (strategy.equals("mm"))
            predictor = new MarkovModelPredictor(config);
    }

    @Override
    public void execute(Tuple tuple) {
        String entityID = tuple.getString(0);
        String record = tuple.getString(1);
        Long timestamp = tuple.getLong(2);

        Prediction p = predictor.execute(entityID, record);

        // send outliers
        if (p.isOutlier()) {
            outliers++;
            collector.emit(tuple,
                    new Values(entityID, p.getScore(), StringUtils.join(p.getStates(), ","), timestamp));
        }
        collector.ack(tuple);

        processed++;
        t_end = System.nanoTime();
    }

    @Override
    public void cleanup() {
        long t_elapsed = (t_end - t_start) / 1000000; // elapsed time in milliseconds

        System.out.println("[FraudPredictorBolt] Processed " +
                processed + " tuples in " +
                t_elapsed + " ms. Source bandwidth is " +
                (processed / (t_elapsed / 1000)) +
                " tuples per second.");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.ENTITY_ID, Field.SCORE, Field.STATES, BaseField.TIMESTAMP));
    }
}

