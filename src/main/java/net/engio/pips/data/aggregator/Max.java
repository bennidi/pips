package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Max<N extends Number> extends DataProcessor<N, Double> implements IAggregate<N, Double> {

    private Double max = null;

    @Override
    public void reset() {
        max=Double.MIN_VALUE;
    }

    @Override
    public Double getValue() {
        return max;
    }

    @Override
    public void receive(DataPoint<N> datapoint) {
        if(max == null || datapoint.getValue().doubleValue() >= max)
            max = datapoint.getValue().doubleValue();
        emit(new DataPoint<Double>(datapoint.getTsCreated(), max));
    }
}
