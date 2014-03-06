package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.DataProcessor;
import net.engio.imupers.performance.data.IDataProcessor;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class Counter extends DataProcessor<Long, Long> {

    private AtomicLong current;

    public Counter(IDataProcessor<Long, Long> delegate) {
        this(0, delegate);
    }

    public Counter(long initialValue, IDataProcessor<Long, Long> delegate) {
        current = new AtomicLong(initialValue);
        connectTo(delegate);
    }

    @Override
    protected DataPoint<Long> doReceive(DataPoint<Long> datapoint) {
        return new DataPoint<Long>(datapoint.getTsCreated(), current.addAndGet(datapoint.getValue()));
    }
}
