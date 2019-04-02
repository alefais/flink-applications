package Constants;

public interface SpikeDetectionConstants extends BaseConstants {
    String PREFIX = "sd";
    int max_hz = 80000;
    
    interface Conf extends BaseConf {
        String PARSER_VALUE_FIELD = "sd.parser.value_field";
        String GENERATOR_COUNT = "sd.generator.count";
        String MOVING_AVERAGE_THREADS = "sd.moving_average.threads";
        String MOVING_AVERAGE_WINDOW = "sd.moving_average.window";
        String SPIKE_DETECTOR_THREADS = "sd.spike_detector.threads";
        String SPIKE_DETECTOR_THRESHOLD = "sd.spike_detector.threshold";
    }
    
    interface Component extends BaseComponent {
        String MOVING_AVERAGE = "movingAverageBolt";
        String SPIKE_DETECTOR = "spikeDetectorBolt";
    }
    
    interface Field {
        String DEVICE_ID = "deviceID";
        String TIMESTAMP = "timestamp";
        String VALUE = "value";
        String MOVING_AVG = "movingAverage";
        String MESSAGE = "message";
    }
}
