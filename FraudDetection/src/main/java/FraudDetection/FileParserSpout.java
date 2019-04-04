package FraudDetection;

import Constants.BaseConstants.BaseField;
import Constants.FraudDetectionConstants.Field;
import Util.config.Configuration;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * The spout is in charge of reading the input data file, parsing it
 * and generating the stream of records toward the FraudPredictorBolt.
 *
 * Format of the input file:
 * entityID, transactionID, transactionType
 */
public class FileParserSpout extends BaseRichSpout {

    private static final Logger LOG = LoggerFactory.getLogger(FileParserSpout.class);

    protected Configuration config;
    protected SpoutOutputCollector collector;
    protected TopologyContext context;

    private String file_path;
    private String split_regex;
    private Integer rate;

    private long t_start;
    private long generated;
    private long emitted;
    private int reset;
    private long nt_execution;
    private long nt_end;
    private int par_deg;

    /**
     * Constructor: it expects the file path and the split expression needed
     * to parse the file (it depends on the format of the input data).
     * @param file path to the input data file
     * @param split split expression
     * @param gen_rate if the argument value is -1 then the spout generates tuples at
     *                 the maximum rate possible (measure the bandwidth under this assumption);
     *                 if the argument value is different from -1 then the spout generates
     *                 tuples at the rate given by this parameter (measure the latency given
     *                 this generation rate)
     * @param p_deg source parallelism degree
     */
    FileParserSpout(String file, String split, int gen_rate, int p_deg) {
        file_path = file;
        split_regex = split;
        rate = gen_rate;        // number of tuples per second
        par_deg = p_deg;        // spout parallelism degree
        generated = 0;          // total number of generated tuples
        emitted = 0;            // total number of emitted tuples
        reset = 0;
        nt_execution = 0;       // number of executions of nextTuple() method
    }

    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        LOG.info("[FileParserSpout] Started ({} replicas).", par_deg);

        t_start = System.nanoTime(); // spout start time in nanoseconds

        config = Configuration.fromMap(conf);
        collector = spoutOutputCollector;
        context = topologyContext;
    }

    /**
     * The method is called in an infinite loop by design, this means that the
     * stream is continuously generated from the data source file.
     * The parsing phase splits each line of the input dataset in 2 parts:
     * - first string identifies the customer (entityID)
     * - second string contains the transactionID and the transaction type
     */
    @Override
    public void nextTuple() {
        //nextTupleAll();
        nextTupleParseAndGen();
    }

    @Override
    public void close() {
        long t_elapsed = (nt_end - t_start) / 1000000; // elapsed time in milliseconds

        System.out.println("[FileParserSpout] Terminated after " + nt_execution + " generations.");
        System.out.println("[FileParserSpout] Bandwidth is " + (generated / (t_elapsed / 1000)) +
                " tuples per second (" + nt_execution + " generations).");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field.ENTITY_ID, Field.RECORD_DATA, BaseField.TIMESTAMP));
    }

    //------------------------------ private methods ---------------------------

    /*
        For each line of the input file parse and emit a new tuple.
     */
    private void nextTupleAll() {
        File txt = new File(file_path);
        String entity;
        String record;

        try {
            Scanner scan = new Scanner(txt);
            int interval = 1000000000; // one second (nanoseconds)
            long t_init = System.nanoTime();

            while (scan.hasNextLine()) {
                String[] line = scan.nextLine().split(split_regex, 2);
                entity = line[0];
                record = line[1];

                if (rate == -1) {   // emit at the maximum possible rate
                    collector.emit(new Values(entity, record, System.nanoTime()));
                    emitted++;
                } else {            // emit at the given rate
                    long t_now = System.nanoTime();
                    if (emitted >= rate) {
                        if (t_now - t_init <= interval)
                            active_delay(interval - (t_now - t_init));

                        emitted = 0;
                        t_init = System.nanoTime();
                        reset++;
                    }
                    collector.emit(new Values(entity, record, System.nanoTime()));
                    emitted++;

                    active_delay((double)interval / rate);
                }
                generated++;
            }
            scan.close();
        } catch (FileNotFoundException | NullPointerException e) {
            LOG.error("The file {} does not exists", file_path);
            throw new RuntimeException("The file '" + file_path + "' does not exists");
        }

        nt_execution++;
        nt_end = System.nanoTime();
    }

    /*
        First parse the whole input file and then start emitting tuples.
     */
    private void nextTupleParseAndGen() {
        File txt = new File(file_path);
        ArrayList<String> entities = new ArrayList<>();
        ArrayList<String> records = new ArrayList<>();

        // parsing phase
        try {
            Scanner scan = new Scanner(txt);
            while (scan.hasNextLine()) {
                String[] line = scan.nextLine().split(split_regex, 2);
                entities.add(line[0]);
                records.add(line[1]);
                generated++;
            }
            scan.close();
        } catch (FileNotFoundException | NullPointerException e) {
            LOG.error("The file {} does not exists", file_path);
            throw new RuntimeException("The file '" + file_path + "' does not exists");
        }

        // emit tuples
        int interval = 1000000000; // one second (nanoseconds)
        long t_init = System.nanoTime();

        for (int i = 0; i < entities.size(); i++) {
            if (rate == -1) {       // at the maximum possible rate
                collector.emit(new Values(entities.get(i), records.get(i), System.nanoTime()));
                emitted++;
            } else {                // at the given rate
                long t_now = System.nanoTime();
                if (emitted >= rate) {
                    if (t_now - t_init <= interval)
                        active_delay(interval - (t_now - t_init));

                    emitted = 0;
                    t_init = System.nanoTime();
                    reset++;
                }
                collector.emit(new Values(entities.get(i), records.get(i), System.nanoTime()));
                emitted++;
                active_delay((double) interval / rate);
            }
        }

        nt_execution++;
        nt_end = System.nanoTime();
    }

    /**
     * Add some active delay (busy-waiting function).
     * @param nsecs wait time in nanoseconds
     */
    private void active_delay(double nsecs) {
        long t_start = System.nanoTime();
        long t_now;
        boolean end = false;

        while (!end) {
            t_now = System.nanoTime();
            end = (t_now - t_start) >= nsecs;
        }
    }
}
