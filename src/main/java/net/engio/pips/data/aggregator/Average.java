package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Average<N extends Number> implements IAggregate<N, Double> {

    private double total=0;

    private int count=0;

    @Override
    public void add(DataPoint<N> datapoint) {
        count++;
        total += datapoint.getValue().doubleValue();
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


}
