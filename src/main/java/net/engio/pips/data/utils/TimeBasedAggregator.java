package net.engio.pips.data.utils;

import net.engio.pips.data.DataCollector;
import net.engio.pips.data.DataPoint;
import net.engio.pips.data.IDataCollector;
import net.engio.pips.data.aggregator.IAggregate;

import java.util.HashMap;
import java.util.Map;

/**
 *  Time based aggregation allows to combine multiple data points with the same time stamp
 *  into an aggregated value (of the same time stamp). The aggregator can be used
 *  to fold all collected data points into a new {@link net.engio.pips.data.IDataCollector}
 *  such that this data collector contains one (aggregated) data point for each set of
 *  data points sharing a common time stamp.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class TimeBasedAggregator<V extends Number>{

    private Map<Long, DataCollector<V>> aggregated = new HashMap<Long, DataCollector<V>>();

    public void receive(DataPoint<V> datapoint) {
        DataCollector<V> collector;
            collector = aggregated.get(datapoint.getTsCreated());
            if(collector == null){
                collector = new DataCollector<V>("" + datapoint.getTsCreated());
                aggregated.put(datapoint.getTsCreated(), collector);
            }
        collector.receive(datapoint);
    }

    public void consume(IDataCollector<V> collector){
        for(DataPoint<V> data : collector.getDatapoints())
            receive(data);
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
    public <A> DataCollector<A> fold(IAggregate<V, A> aggregator){
        DataCollector<A> reduced = new DataCollector<A>(aggregator.toString());
        for(Map.Entry<Long, DataCollector<V>> entry: aggregated.entrySet()){
            // feed aggregator
            for(DataPoint<V> dataPoint : entry.getValue().getDatapoints())
                aggregator.add(dataPoint);
            // add aggregated value to folded collector
            reduced.receive(new DataPoint(entry.getKey(), aggregator.getValue()));
            aggregator.reset();
        }
        return reduced;
    }
}
