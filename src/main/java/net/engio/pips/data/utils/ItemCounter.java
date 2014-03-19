package net.engio.pips.data.utils;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;
import net.engio.pips.data.IDataSink;

/**
 * Plug in the item counter to keep track of the number of data points
 * flowing a network of data processors.
 *
 * @author bennidi
 *         Date: 3/4/14
 */
public class ItemCounter extends DataProcessor {

    private int itemCount=0;

    @Override
    protected void doReceive(IDataSink sink, DataPoint datapoint) {
        itemCount++;
        sink.receive(datapoint);
    }

    public int getItemCount(){
        return itemCount;
    }
}
