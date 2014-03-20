package net.engio.pips.data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennidi
 *         Date: 2/27/14
 */
public abstract class DataProcessor<IN, OUT> implements IDataProcessor<IN, OUT> {

    private List<IDataProcessor<OUT, ?>> receivers = new LinkedList<IDataProcessor<OUT, ?>>();

    protected DataProcessor() {
        super();
    }

    @Override
    public <V> IDataProcessor<OUT, V> pipeInto(final IDataProcessor<OUT, V> consumer){
       receivers.add(consumer);
        return consumer;
    }

    @Override
    public DataProcessor<IN, OUT> add(IDataProcessor<OUT, ?> destination) {
        receivers.add(destination);
        return this;
    }

    protected void emit(DataPoint<OUT> data){
       for(IDataProcessor sink : receivers)
           sink.receive(data);
    }

    protected List<IDataProcessor<OUT, ?>> getReceivers() {
        return receivers;
    }

    /*
    @Override
    public void receive(DataPoint<IN> datapoint) {
        for(IDataProcessor sink : receivers)
            doReceive(sink, datapoint);
    }

    protected abstract void doReceive(IDataProcessor<OUT,?> receiver, DataPoint<IN> datapoint);
    */

    @Override
    public void receive(IN value) {
        receive(new DataPoint<IN>(value));
    }

}
