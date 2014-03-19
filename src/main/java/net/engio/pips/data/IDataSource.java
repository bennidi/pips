package net.engio.pips.data;

/**
 * A data source emits data points {@link DataPoint} to connected {@link IDataSink}.
 * When, at which rate and what kind of data points are emitted depends entirely on the implementation.
 *
 * Example implementations:
 *
 * {@link net.engio.pips.data.utils.ExecutionTimer}
 * {@link net.engio.pips.data.utils.Counter}
 *
 * @author bennidi
 *         Date: 3/3/14
 */
public interface IDataSource<OUT> {

    <V> IDataProcessor<OUT,V> connectTo(IDataProcessor<OUT, V> destination);

    IDataSource<OUT> connectTo(IDataSink<OUT> destination);

}
