package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.DataProcessor;

/**
 * Map values from one type to another.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public abstract class ValueMapper<IN,OUT> extends DataProcessor<IN, OUT>{


    @Override
    protected DataPoint<OUT> doReceive(DataPoint<IN> datapoint) {
        return new DataPoint<OUT>(datapoint.getTsCreated(), map(datapoint.getValue()));
    }

    protected abstract OUT map(IN in);
}
