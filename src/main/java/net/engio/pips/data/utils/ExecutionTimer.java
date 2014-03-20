package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.IDataProcessor;

/**
 * An execution timer provides convenience methods (begin(),end()) for measuring time intervals
 * and publishing them to registered data consumers.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class ExecutionTimer{

    private long begin = -1;

    private IDataProcessor<Long,?> delegate;

    public ExecutionTimer(IDataProcessor<Long,?> delegate) {
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
