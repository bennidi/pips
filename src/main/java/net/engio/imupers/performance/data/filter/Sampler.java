package net.engio.imupers.performance.data.filter;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.utils.Multiplexer;

/**
 * Filter data points based on a sampling rate criterion. Samplers can be used to reduce
 * frequency of data point producers;
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class Sampler<V> extends Multiplexer<V> {

    private DataFilter strategy ;

    public Sampler(DataFilter<V> strategy) {
        super();
        this.strategy = strategy;
    }

    @Override
    public void receive(DataPoint<V> datapoint) {
        if(strategy.accepts(datapoint))
            super.receive(datapoint);
    }

    public static <V> Sampler<V> timeBased(long interval){
        return new Sampler<V>(new DataFilter.TimeBased<V>(interval));
    }
}
