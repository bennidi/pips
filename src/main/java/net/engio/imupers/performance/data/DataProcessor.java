package net.engio.imupers.performance.data;

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
    public IDataSink<OUT> connectTo(IDataSink<OUT> destination) {
        receivers.add(destination);
        return destination;
    }

    protected void emit(DataPoint<OUT> data){
       for(IDataSink sink : receivers)
           sink.receive(data);
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
       emit(doReceive(datapoint));
    }

    protected abstract DataPoint<OUT> doReceive(DataPoint<IN> datapoint);

    @Override
    public void append(IN value) {
        receive(new DataPoint<IN>(value));
    }

}
