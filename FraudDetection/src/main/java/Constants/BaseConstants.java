package Constants;

public interface BaseConstants {

    interface BaseConf {
        String SPOUT_THREADS = "%s.spout.threads";
        String SPOUT_PATH = "%s.spout.path";
        String SINK_THREADS = "%s.sink.threads";
    }

    interface BaseComponent {
        String SPOUT = "spout";
        String SINK  = "sink";
    }

    interface BaseField {
        String TIMESTAMP = "timestamp";
    }
}
