package net.engio.pips.data.aggregator;

import net.engio.pips.data.DataPoint;

/**
 * Reduce a set of data points to a single value, e.g. compute AVG, MIN, MAX
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IAggregate<IN, OUT>{

    /**
     * Reset this aggregate. Resetting will evict all formerly received values
     * and reset the ongoing aggregation process.
     */
    void reset();

    /**
     * Get the aggregated value
     * @return
     */
    OUT getValue();

    void add(DataPoint<IN> value);
}
