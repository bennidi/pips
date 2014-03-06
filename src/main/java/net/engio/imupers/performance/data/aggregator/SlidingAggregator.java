package net.engio.imupers.performance.data.aggregator;

import net.engio.imupers.performance.data.*;
import net.engio.imupers.performance.data.filter.DataFilter;

/**
 * Calculate the moving average by continuously aggregating a fixed number of incoming
 * data points.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class SlidingAggregator<IN, OUT> extends DataProcessor<IN, OUT>{

    // the aggregator used to create the aggregated value
    private final IDataAggregator<IN, OUT> aggregator;

    private DataFilter<IN> dataFilter;

    public SlidingAggregator(DataFilter<IN> dataFilter, IDataAggregator<IN, OUT> aggregator) {
        this.dataFilter = dataFilter;
        this.aggregator = aggregator;
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
        aggregator.receive(datapoint);
        if(dataFilter.accepts(datapoint)){
            // if {range} number of datapoints have been collected
           emit(new DataPoint<OUT>(datapoint.getTsCreated(), aggregator.getValue()));
            // reset for next cycle
           aggregator.reset();
        }
    }

    @Override
    protected DataPoint<OUT> doReceive(DataPoint<IN> datapoint) {
        return null; // Todo: flaw in class hierarchy
    }

    @Override
    public void append(IN value) {
        receive(new DataPoint<IN>(value));
    }

    @Override
    public IDataSink<OUT> connectTo(IDataSink<OUT> destination) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


