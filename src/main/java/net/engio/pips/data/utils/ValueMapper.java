package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataSink;

/**
 * Map values from one type to another.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public abstract class ValueMapper<IN,OUT> extends DataProcessor<IN, OUT>{


    @Override
    protected void doReceive(IDataSink<OUT> sink, DataPoint<IN> datapoint) {
        sink.receive(new DataPoint<OUT>(datapoint.getTsCreated(), map(datapoint.getValue())));
    }

    protected abstract OUT map(IN in);
}
