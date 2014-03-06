package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.IDataSink;

/**
 * Decorate a data point consumer to make it thread-safe.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class Synchronizer<IN> implements IDataProcessor<IN, IN> {

    private IDataSink<IN> delegate;

    public Synchronizer() {
    }

    public Synchronizer(IDataSink<IN> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
        synchronized (delegate){
            delegate.receive(datapoint);
        }
    }

    @Override
    public void append(IN value) {
        synchronized (delegate){
            delegate.append(value);
        }
    }

    @Override
    public <V> IDataProcessor<IN, V> connectTo(IDataProcessor<IN, V> processor) {
        delegate = processor;
        return processor;
    }

    @Override
    public IDataSink<IN> connectTo(IDataSink<IN> destination) {
        delegate = destination;
        return destination;
    }
}
