package net.engio.pips.data.filter;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataSink;

/**
 * Samplers can be used to reduce the frequency of data point producers {@link net.engio.pips.data.IDataSource}
 * by filtering data points based on a sampling rate criterion.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class Sampler<V> extends DataProcessor<V,V> {

    private IDataFilter strategy ;

    public Sampler(IDataFilter<V> strategy) {
        super();
        this.strategy = strategy;
    }

    @Override
    public void receive(DataPoint<V> datapoint) {
        if(strategy.accepts(datapoint))
            emit(datapoint);
    }

    @Override
    protected void doReceive(IDataSink<V> receiver, DataPoint<V> datapoint) {
        // nope
    }

    public static <V> Sampler<V> timeBased(long interval){
        return new Sampler<V>(new IDataFilter.TimeBased<V>(interval));
    }
}
