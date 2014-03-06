package net.engio.imupers.performance.data;

import net.engio.imupers.performance.Benchmark;
import net.engio.imupers.performance.data.filter.Sampler;
import net.engio.imupers.performance.data.utils.ExecutionTimer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public class ResultCollector {

    private static final String ExecutionTimer = "execution-timer:";

    private List<Frame> frames = new LinkedList<Frame>();

    private final AtomicInteger counter = new AtomicInteger(0);

    private DataCollectorManager collectors = new DataCollectorManager();

    private Benchmark configuration;

    public ResultCollector(Benchmark configuration) {
        this.configuration = configuration;
    }

    public Frame newFrame(){
        Frame frame = new Frame();
        synchronized (this){frames.add(frame);}
        return frame;
    }

    public List<Frame> getFrames(){
        return frames;
    }

    public Set<IDataCollector> getCollectors(String groupId){
        return collectors.getCollectors(groupId);
    }

    public Set<IDataCollector> getCollectors() {
        return collectors.getCollectors("");
    }

    public class Frame{

        private int index = counter.incrementAndGet();

        private long start = -1;

        private long end = -1;

        public ExecutionTimer createExecutionTimer(String timerId){
            DataCollector<Long> timings = createLocalCollector(ExecutionTimer + timerId + index);
            ExecutionTimer timer =  new ExecutionTimer(Sampler.<Long>timeBased(configuration.getSampleInterval()).connectTo(timings));
            return timer;
        }

        public <V> DataCollector<V> createLocalCollector(String collectorId){
            DataCollector<V> collector = new DataCollector(collectorId);
            collectors.addCollector(collector);
            return collector;
        }

        public void start(){
            start = System.currentTimeMillis();
        }

        public void end(){
            end = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Frame" + index + "{" +
                    "execution-time=" + (end -start) + '}';
        }
    }


}
