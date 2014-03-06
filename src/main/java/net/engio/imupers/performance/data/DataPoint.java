package net.engio.imupers.performance.data;

import java.util.Collection;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataPoint<V> {

    private long tsCreated;

    private V value;

    public long getTsCreated() {
        return tsCreated;
    }

    public V getValue() {
        return value;
    }

    public DataPoint(V value) {
        this.value = value;
        tsCreated = System.currentTimeMillis();
    }

    public DataPoint(long tsCreated, V value) {
        this.tsCreated = tsCreated;
        this.value = value;
    }


    @Override
    public String toString() {
        // 12432433243|34
        return tsCreated + "|" + value;
    }

    public static String toString(Collection<DataPoint> datapoints){
        StringBuilder dpString = new StringBuilder("[");
        for(DataPoint dp : datapoints)
            dpString.append(dp.toString());
        dpString.append("]");
        return dpString.toString();
    }
}
