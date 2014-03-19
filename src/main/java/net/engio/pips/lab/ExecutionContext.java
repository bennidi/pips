package net.engio.pips.lab;

import net.engio.pips.data.DataCollector;
import net.engio.pips.data.filter.Sampler;
import net.engio.pips.data.utils.ExecutionTimer;

import java.util.*;

/**
 * Each execution unit is provided with an execution context. The context allows
 * access to shared objects such as the result collector.
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public class ExecutionContext {

    private Experiment experiment;
    private ExecutionContext parent;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private long started;
    private long finished;

    public ExecutionContext(Experiment experiment) {
        this.experiment = experiment;
    }

    public void started(){
        started = System.currentTimeMillis();
    }

    public void finished(){
        finished = System.currentTimeMillis();
    }

    public boolean isFinished(){
        return finished != -1;
    }

    public long getExecutionTime(){
        return isFinished() ? finished - started : -1;
    }

    public ExecutionTimer createExecutionTimer(String timerId){
        DataCollector<Long> timings = createLocalCollector(timerId);
        Sampler sampler = Sampler.<Long>timeBased((Integer)get(Experiment.Properties.SampleInterval));
        sampler.connectTo(timings);
        ExecutionTimer timer =  new ExecutionTimer(sampler);
        return timer;
    }

    public <V> DataCollector<V> createLocalCollector(String collectorId){
        DataCollector<V> collector = new DataCollector(collectorId);
        bind(Experiment.Properties.ExecutionTimers + collectorId, collector);
        return collector;
    }

    public ExecutionContext bind(String key, Object value){
        properties.put(key, value);
        return this;
    }

    public ExecutionContext bind(Object value){
        return bind(value.toString(), value);
    }

    public ExecutionContext bind(Object ...values){
        for(Object value : values)
            bind(value);
        return this;
    }

    public ExecutionContext bind(Object[] ...values){
        for(Object value : values)
            bind(value);
        return this;
    }

    public ExecutionContext bindAll(Map<String, Object> values){
        for(String key : values.keySet())
           bind(key, values.get(key));
        return this;
    }


    public ExecutionContext getChild(){
        ExecutionContext child =  new ExecutionContext(experiment);
        child.parent = this;
        return child;
    }

    public <T> T get(String key) {
        return properties.containsKey(key)
                ? (T)properties.get(key)
                : parent != null ? (T)parent.get(key) : null;
    }

    public <T> T get(Object key) {
        return get(key.toString());
    }

    public <T> Collection<T> getAll(Object key){
        return getAll(key.toString());
    }

    public <T> Collection<T> getAll(String key){
        LinkedList all = new LinkedList();
        if(properties.containsKey(key))
            all.add(properties.get(key));
        if(parent != null)
            all.addAll(parent.getAll(key));
        return all;
    }

    public <T> Collection<T> getMatching(String key){
        LinkedList all = new LinkedList();
        for(String globalKey : getGlobalKeySet())
            if(globalKey.startsWith(key))all.addAll(getAll(globalKey));
        return all;
    }

    private Set<String> getGlobalKeySet(){
        Set<String> keys = new HashSet<String>();
        keys.addAll(properties.keySet());
        ExecutionContext current = parent;
        while(current != null){
            keys.addAll(parent.properties.keySet());
            current = current.parent;
        }
        return keys;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key)
                || parent != null ? parent.containsKey(key) : false;
    }
}
