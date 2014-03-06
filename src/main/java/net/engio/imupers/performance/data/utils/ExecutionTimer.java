package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataSink;

/**
 * An execution timer provides convenience methods (begin(),end()) for measuring time intervals
 * and publishing them to registered data consumers.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class ExecutionTimer{

    private long begin = -1;

    private IDataSink<Long> delegate;

    public ExecutionTimer(IDataSink<Long> delegate) {
        this.delegate = delegate;
    }

    public void begin() {
        begin = System.currentTimeMillis();
    }

    public void end() {
        if (begin == -1)
            throw new IllegalStateException("end() was called without begin()");
        delegate.receive(new DataPoint<Long>(System.currentTimeMillis() - begin));
        begin = -1;
    }

}
