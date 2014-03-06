package net.engio.imupers.performance.data.aggregator;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataAggregator;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.IDataSink;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Max<N extends Number> implements IDataAggregator<N, Double> {

    private double max=Double.MIN_VALUE;

    @Override
    public void receive(DataPoint<N> datapoint) {
        if(datapoint.getValue().doubleValue() > max)
            max = datapoint.getValue().doubleValue();
    }

    @Override
    public void reset() {
        max=Double.MIN_VALUE;
    }

    @Override
    public void append(N value) {
        receive(new DataPoint<N>(value));
    }

    @Override
    public <V> IDataProcessor<Double, V> connectTo(IDataProcessor<Double, V> processor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IDataSink<Double> connectTo(IDataSink<Double> destination) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double getValue() {
        return max;
    }
}
