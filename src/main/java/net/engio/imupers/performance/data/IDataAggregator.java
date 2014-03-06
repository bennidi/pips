package net.engio.imupers.performance.data;

/**
 * Reduce a set of data points to a single value, e.g. compute AVG, MIN, MAX
 *
 * TODO: Broken class hierarchy
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataAggregator<IN, OUT> extends IDataProcessor<IN, OUT> {

    void reset();

    OUT getValue();
}
