package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataSink;

/**
 * Decorate a data point consumer to make it thread-safe.
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class Synchronizer<IN> extends DataProcessor<IN, IN> {

    public Synchronizer(IDataSink<IN> ...receivers) {
        for(IDataSink receiver : receivers)
            connectTo(receiver);
    }

    @Override
    protected void doReceive(IDataSink<IN> receiver, DataPoint<IN> datapoint) {
        synchronized (receiver){
            receiver.receive(datapoint);
        }
    }

}
