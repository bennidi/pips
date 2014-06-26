package net.engio.pips.data.filter;

import net.engio.pips.data.DataPoint;

/**
 * A filter is used to control which data points may pass from one {@link net.engio.pips.data.IDataProcessor} to
 * another
 *
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataFilter<V> {

    /**
     * Calculate whether or not the given data point is accepted by this filter.
     * If a filter accepts the data point it will be routed along to the corresponding
     * {@link net.engio.pips.data.IDataProcessor}.
     *
     * For an example use of filters see {@link Sampler} and corresponding filter implementations
     *
     * @param vDataPoint - The data point to be analyzed
     * @return - Whether or not this data point may pass the filter
     */
    boolean accepts(DataPoint<V> vDataPoint);

    /**
     * Time based filtering considers the timestamps of the incoming data points as a
     * filter criterion. Based on a given interval size, it will only accept one data point
     * per interval.
     *
     * @author bennidi
     *         Date: 2/25/14
     */
    class TimeBased<V> implements IDataFilter<V> {

        private long intervalInMs;

        private long lastSample = -1;

        public TimeBased(long intervalInMs) {
            this.intervalInMs = intervalInMs;
        }

        @Override
        public boolean accepts(DataPoint<V> vDataPoint) {
            if (vDataPoint.getTsCreated() - intervalInMs >= lastSample) {
                lastSample = vDataPoint.getTsCreated();
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
    class ItemCountBased<V> implements IDataFilter<V> {

        private int skip;

        private int skipped = 0;

        public ItemCountBased(int skip) {
            if(skip < 1)
                throw new IllegalArgumentException("Skip at least one data point");
            this.skip = skip;
            this.skipped = skip; // accept the first data point
        }

        @Override
        public boolean accepts(DataPoint<V> vDataPoint) {
            if (skip == skipped) {
                skipped = 0;
                return true;
            } else{
                skipped++;
                return false;
            }
        }
    }
}
