package net.engio.pips.data.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Within a benchmark it may be necessary to share some global counters, data collectors etc.
 * These can be registered at setup time and will be available to the execution units via the
 * Benchmark object.
 *
 * Note: The registry is not thread-safe, such that adding components concurrently while the
 * benchmark is already running will result in concurrency issues like lost items. The registry
 * is meant to be configured completely before the benchmark starts.
 *
 * @author bennidi
 *         Date: 2/28/14
 */
public class Registry {

    private Map<String, Object> registeredComponents = new HashMap<String, Object>();


    /**
     * Register a new component and associate it with the given key
     * @param key - the key used to subsequently retrieve the object
     * @param component
     * @return
     */
    public Registry add(String key, Object component){
        registeredComponents.put(key, component);
        return this;
    }

    public <T> T get(String key){
        return (T) registeredComponents.get(key);
    }

    public Collection<Object> values() {
        return registeredComponents.values();
    }

    public Map<String, Object> getRegisteredComponents(){
        return registeredComponents;
    }
}
