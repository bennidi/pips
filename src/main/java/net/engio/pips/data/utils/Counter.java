package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataProcessor;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class Counter extends DataProcessor<Long, Long> {

    private AtomicLong current;

    public Counter() {
        this.current = new AtomicLong(0);
    }

    public Counter(IDataProcessor<Long, Long> delegate) {
        this(0, delegate);
    }

    public Counter(long initialValue, IDataProcessor<Long, Long> delegate) {
        current = new AtomicLong(initialValue);
        pipeInto(delegate);
    }

    @Override
    public void receive(DataPoint<Long> datapoint) {
        emit(new DataPoint<Long>(datapoint.getTsCreated(), current.addAndGet(datapoint.getValue())));
    }
}
