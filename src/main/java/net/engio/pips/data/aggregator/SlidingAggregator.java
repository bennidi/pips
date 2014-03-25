package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.filter.IDataFilter;

/**
 * Calculate the moving aggregated by continuously aggregating a fixed number of incoming
 * data points using any given {@link IAggregate}.
 * Values are aggregated until an incoming data point passes the {@link IDataFilter}. The aggregated value
 * will then be propagated to the connected {@link net.engio.pips.data.IDataProcessor} and aggregation will be reset.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class SlidingAggregator<IN, OUT> extends DataProcessor<IN, OUT>{

    // the aggregator used to create the aggregated value
    private final IAggregate<IN, OUT> aggregator;

    // the filter used
    private IDataFilter<IN> IDataFilter;

    public SlidingAggregator(IDataFilter<IN> IDataFilter, IAggregate<IN, OUT> aggregator) {
        this.IDataFilter = IDataFilter;
        this.aggregator = aggregator;
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
        aggregator.receive(datapoint);
        if(IDataFilter.accepts(datapoint)){
            // if {range} number of datapoints have been collected
           emit(new DataPoint<OUT>(datapoint.getTsCreated(), aggregator.getValue()));
            // reset for next cycle
           aggregator.reset();
        }
    }

}


