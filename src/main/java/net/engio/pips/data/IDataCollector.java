package net.engio.pips.data;

import java.util.List;

/**
 * A data collector will store the received data points (persistently or in-memory) to provide
 * subsequent access to them
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataCollector<IN> extends IDataProcessor<IN,IN> {

    /**
     * Feed all collected data points to the consumer by calling the
     * consumers receive() method for each data point.
     *
     * @param consumer
     */
    void feed(IDataProcessor<IN,?> consumer);

    /**
     * The number of data points contained in this collector
     *
     * @return
     */
    int size();

    /**
     * Get the id of the collector. Besides being a unique identifier, the
     * id is used to implicitly form hierarchies of collectors.
     *
     * id: (group:)*label
     * group: .?.*
     * label: .?.*
     *
     * Examples: main:sub1:collector1, main:sub2:collector1, allItemsCollector
     *
     * @return
     */
    String getId();

    /**
     * Retrieve an immutable set of all received data points
     * @return
     */
    List<DataPoint<IN>> getDatapoints();

    /**
     * Get all received values
     * @return
     */
    Object[] getValues();
}
