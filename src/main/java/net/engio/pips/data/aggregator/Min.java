package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Min<N extends Number> implements IAggregate<N, Double> {

    private double min=Double.MAX_VALUE;

    @Override
    public void add(DataPoint<N> datapoint) {
        if(datapoint.getValue().doubleValue() < min)
            min = datapoint.getValue().doubleValue();
    }

    @Override
    public void reset() {
        min=Double.MAX_VALUE;
    }

    @Override
    public Double getValue() {
        return min;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
