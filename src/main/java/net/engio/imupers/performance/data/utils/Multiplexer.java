package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.IDataSink;

import java.util.LinkedList;
import java.util.List;

/**
 * Multiplex incoming data points to a static set of consumers
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public class Multiplexer<IN> implements IDataProcessor<IN, IN> {

    public List<IDataSink<IN>> consumers = new LinkedList<IDataSink<IN>>();

    @Override
    public void receive(DataPoint<IN> datapoint) {
        for(IDataSink consumer : consumers)
            consumer.receive(datapoint);
    }

    @Override
    public void append(IN value) {
        for(IDataSink consumer : consumers)
            consumer.append(value);
    }

    @Override
    public <V> IDataProcessor<IN, V> connectTo(IDataProcessor<IN, V> processor) {
        consumers.add(processor);
        return processor;
    }

    @Override
    public IDataSink<IN> connectTo(IDataSink<IN> destination) {
        consumers.add(destination);
        return destination;
    }
}
