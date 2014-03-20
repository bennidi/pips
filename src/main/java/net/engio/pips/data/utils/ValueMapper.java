package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;

/**
 * Map values from one type to another.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public abstract class ValueMapper<IN,OUT> extends DataProcessor<IN, OUT>{

    protected abstract OUT map(IN in);

    @Override
    public void receive(DataPoint<IN> datapoint) {
        emit(new DataPoint<OUT>(datapoint.getTsCreated(), map(datapoint.getValue())));
    }
}
