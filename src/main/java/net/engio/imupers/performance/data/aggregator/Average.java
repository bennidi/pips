package net.engio.imupers.performance.data.aggregator;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataAggregator;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.IDataSink;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Average<N extends Number> implements IDataAggregator<N, Double> {

    private double total=0;

    private int count=0;

    @Override
    public void receive(DataPoint<N> datapoint) {
        count++;
        total += datapoint.getValue().doubleValue();
    }

    @Override
    public void append(N value) {
        receive(new DataPoint<N>(value));
    }

    @Override
    public void reset() {
        total = 0;
        count = 0;
    }

    @Override
    public Double getValue(){
        return total / count;
    }

    @Override
    public <V> IDataProcessor<Double, V> connectTo(IDataProcessor<Double, V> destination) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IDataSink<Double> connectTo(IDataSink<Double> destination) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
