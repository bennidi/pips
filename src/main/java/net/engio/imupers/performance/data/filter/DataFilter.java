package net.engio.imupers.performance.data.filter;

import net.engio.imupers.performance.data.DataPoint;

/**
 * A filter is used to control which data points may pass from a data emitter to a data
 * consumer.
 *
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface DataFilter<V> {

    boolean accepts(DataPoint<V> vDataPoint);

    /**
     * Time based filtering considers the timestamps of the incoming data points as a
     * filter criterion. Based on a given interval size, it will only accept one data point
     * per interval.
     *
     * @author bennidi
     *         Date: 2/25/14
     */
    class TimeBased<V> implements DataFilter<V> {

        private long intervalInMs;

        private long lastSample = -1;

        public TimeBased(long intervalInMs) {
            this.intervalInMs = intervalInMs;
            lastSample = System.currentTimeMillis() - intervalInMs; // accept new data point immediately
        }

        @Override
        public boolean accepts(DataPoint<V> vDataPoint) {
            if (System.currentTimeMillis() - intervalInMs >= lastSample) {
                lastSample = System.currentTimeMillis();
                return true;
            } else return false;
        }
    }

    /**
     * This counter based filter will let pass only every nth data point based on the specified
     * range.
     *
     * @author bennidi
     *         Date: 2/25/14
     */
    class ItemCountBased<V> implements DataFilter<V> {

        private int range;

        private int numberOfItems = 0;

        public ItemCountBased(int range) {
            if(range < 2)
                throw new IllegalArgumentException("Range must be greater than 2, for this filter to make sense");
            this.range = range;
        }

        @Override
        public boolean accepts(DataPoint<V> vDataPoint) {
            numberOfItems++;
            if (range - numberOfItems == 0) {
                numberOfItems = 0;
                return true;
            } else return false;
        }
    }
}
