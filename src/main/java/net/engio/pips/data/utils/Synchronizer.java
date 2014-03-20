package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataProcessor;

/**
 * Synchronize flow of data points
 *
 * @author bennidi
 *         Date: 2/26/14
 */
public class Synchronizer<IN> extends DataProcessor<IN, IN> {

    @Override
    public void receive(DataPoint<IN> datapoint) {
        for(IDataProcessor receiver: getReceivers())
            synchronized (receiver){receiver.receive(datapoint);}
    }
}
