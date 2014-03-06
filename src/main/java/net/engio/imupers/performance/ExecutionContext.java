package net.engio.imupers.performance;

import net.engio.imupers.performance.data.ResultCollector;
import net.engio.imupers.performance.data.utils.Registry;

/**
 * Each execution unit is provided with an execution context. The context allows
 * access to shared objects such as the result collector.
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public class ExecutionContext {

    private Benchmark benchmark;
    private ResultCollector.Frame resultCollector;

    public ExecutionContext(Benchmark benchmark, ResultCollector.Frame collector) {
        this.benchmark = benchmark;
        this.resultCollector = collector;
    }

    public <T> T getProperty(String key) {
        return benchmark.getProperty(key);
    }

    public Registry getRegistry(){
        return benchmark.getRegistry();
    }

    public ResultCollector.Frame getResultCollector() {
        return resultCollector;
    }

}
