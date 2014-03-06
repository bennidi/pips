package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataCollector;
import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataAggregator;
import net.engio.imupers.performance.data.IDataSink;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class TimeBasedAggregator<V extends Number> implements IDataSink<V>{

    private Map<Long, DataCollector<V>> aggregated = new HashMap<Long, DataCollector<V>>();

    public void receive(DataPoint<V> datapoint) {
        DataCollector<V> collector;
        synchronized (this){
            collector = aggregated.get(datapoint.getTsCreated());
            if(collector == null){
                collector = new DataCollector<V>("" + datapoint.getTsCreated());
                aggregated.put(datapoint.getTsCreated(), collector);
            }}
        collector.receive(datapoint);
    }

    public void append(V value) {
        receive(new DataPoint<V>(value));
    }

    public void feed(IDataSink<V> consumer, IDataAggregator aggregator){
        for(Map.Entry<Long, DataCollector<V>> entry: aggregated.entrySet()){
            entry.getValue().feed(aggregator);
            consumer.receive(new DataPoint(entry.getKey(), aggregator.getValue()));
            aggregator.reset();
        }
    }

    /**
     * Fold multiple data collectors into one, using the aggregator function.
     * Each data point of the generated data collector is the aggregated value
     * of a corresponding source data collector, maintained by this aggregator.
     *
     *
     * @param aggregator
     * @param <A>
     * @return
     */
    public <A> DataCollector<A> fold(IDataAggregator<V, A> aggregator){
        DataCollector<A> reduced = new DataCollector<A>("reduced"); //TODO: Label ??? why
        for(Map.Entry<Long, DataCollector<V>> entry: aggregated.entrySet()){
            entry.getValue().feed(aggregator);
            reduced.receive(new DataPoint(entry.getKey(), aggregator.getValue()));
            aggregator.reset();
        }
        return reduced;
    }
}
