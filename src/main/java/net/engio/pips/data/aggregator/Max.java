package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Max<N extends Number> implements IAggregate<N, Double> {

    private double max=Double.MIN_VALUE;

    @Override
    public void add(DataPoint<N> datapoint) {
        if(datapoint.getValue().doubleValue() > max)
            max = datapoint.getValue().doubleValue();
    }

    @Override
    public void reset() {
        max=Double.MIN_VALUE;
    }

    @Override
    public Double getValue() {
        return max;
    }
}
