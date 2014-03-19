package net.engio.pips.data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennidi
 *         Date: 2/27/14
 */
public abstract class DataProcessor<IN, OUT> implements IDataProcessor<IN, OUT> {

    private List<IDataSink<OUT>> receivers = new LinkedList<IDataSink<OUT>>();

    protected DataProcessor() {
        super();
    }

    @Override
    public <V> IDataProcessor<OUT, V> connectTo(final IDataProcessor<OUT, V> consumer){
       receivers.add(consumer);
        return consumer;
    }

    @Override
    public IDataSource<OUT> connectTo(IDataSink<OUT> destination) {
        receivers.add(destination);
        return this;
    }

    protected void emit(DataPoint<OUT> data){
       for(IDataSink sink : receivers)
           sink.receive(data);
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
        for(IDataSink sink : receivers)
            doReceive(sink, datapoint);
    }

    protected abstract void doReceive(IDataSink<OUT> receiver, DataPoint<IN> datapoint);

    @Override
    public void append(IN value) {
        receive(new DataPoint<IN>(value));
    }

}
