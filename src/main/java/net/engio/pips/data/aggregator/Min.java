package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Min<N extends Number> extends DataProcessor<N, Double> implements IAggregate<N, Double> {

    private double min=Double.MAX_VALUE;

    @Override
    public void reset() {
        min=Double.MAX_VALUE;
    }

    @Override
    public Double getValue() {
        return min;
    }

    @Override
    public void receive(DataPoint<N> datapoint) {
        if(datapoint.getValue().doubleValue() < min)
            min = datapoint.getValue().doubleValue();
        emit(new DataPoint<Double>(datapoint.getTsCreated(), min));
    }
}
