package Constants;

import org.apache.storm.Config;
import org.apache.storm.utils.Utils;

/**
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public interface BaseConstants {
    String BASE_PREFIX = "storm";
    
    interface BaseConf {
        String SPOUT_THREADS     = "%s.spout.threads";
        String SPOUT_CLASS       = "%s.spout.class";
        String SPOUT_PATH        = "%s.spout.path";
        String SPOUT_PARSER      = "%s.spout.parser";
        
        String SINK_THREADS        = "%s.sink.threads";
        String SINK_CLASS          = "%s.sink.class";
        String SINK_PATH           = "%s.sink.path";
        String SINK_FORMATTER      = "%s.sink.formatter";
        
        String DEBUG_ON = Config.TOPOLOGY_DEBUG;
    }

    interface BaseComponent {
        String SPOUT = "spout";
        String SINK  = "sink";
    }
    
    interface BaseStream {
        String DEFAULT = Utils.DEFAULT_STREAM_ID;
    }
}
