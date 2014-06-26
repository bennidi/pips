package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class Average<N extends Number> extends DataProcessor<N, Double> implements IAggregate<N, Double> {

    private double total=0;

    private int count=0;

    private String name;

    public Average(String name) {
        this.name = name;
    }

    public Average() {
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
    public void receive(DataPoint<N> datapoint) {
        count++;
        total += datapoint.getValue().doubleValue();
        emit(new DataPoint<Double>(datapoint.getTsCreated(), total/count));
    }

    @Override
    public String toString() {
        return name;
    }
}
