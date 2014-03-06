package net.engio.imupers.performance.data.utils;

import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.DataProcessor;

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
    protected DataPoint doReceive(DataPoint datapoint) {
        itemCount++;
        return datapoint;
    }

    public int getItemCount(){
        return itemCount;
    }
}
