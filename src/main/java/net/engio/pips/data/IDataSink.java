package net.engio.pips.data;

/**
 * A sink receive data points
 *
 * @author bennidi
 *         Date: 3/3/14
 */
public interface IDataSink<IN> {

    void receive(DataPoint<IN> datapoint);

    void append(IN value);
}
