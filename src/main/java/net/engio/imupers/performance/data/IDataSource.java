package net.engio.imupers.performance.data;

/**
 * @author bennidi
 *         Date: 3/3/14
 */
public interface IDataSource<OUT> {

    <V> IDataProcessor<OUT,V> connectTo(IDataProcessor<OUT, V> destination);

    IDataSink<OUT> connectTo(IDataSink<OUT> destination);

}
