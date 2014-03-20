package net.engio.pips.data;

/**
 * A data processor produces outgoing data points from incoming data points using any kind of
 * processing logic.
 *
 * Data processors can be connected to form implicit data processing pipelines where each data
 * processor is fed with the outcome of its predecessor. Each processor can receive input from
 * multiple processors as well as it may feed any number of subsequent processors. Thus, complex
 * networks of processors can be formed to implement any data processing logic in a composite
 * fashion.
 *
 * It is not specified how many outgoing data points are generated for each incoming data point.
 * Data points may be duplicated, dropped, converted, delayed, stored, aggregated etc.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataProcessor<IN, OUT> {

    /**
     * Connect another data processor to this data processor. The newly connected processor
     * will be added to the list of receivers and fed with outgoing data points from this processor.
     *
     * @param destination - The data processor to be added to the list of receivers
     * @param <V> - The type of value that the added processor outputs. Used to build pipelines
     *           of data processors in a type-safe manner
     * @return  - The added data processor
     */
    <V> IDataProcessor<OUT,V> pipeInto(IDataProcessor<OUT, V> destination);

    /**
     * Connect another data processor to this data processor. The newly connected processor
     * will be added to the list of receivers and fed with outgoing data points from this processor.
     *
     * @param destination  - The data processor to be added to the list of receivers
     * @return  This data processor
     */
    IDataProcessor<IN, OUT> add(IDataProcessor<OUT, ?> destination);

    /**
     * Receive a new data point
     *
     * @param datapoint
     */
    void receive(DataPoint<IN> datapoint);

    /**
     * Convenience method to receive new values
     *
     * @param value
     */
    void receive(IN value);

    /**
     * A data processor that acts like /dev/null
     */
    public static final IDataProcessor Void = new IDataProcessor() {
        @Override
        public IDataProcessor add(IDataProcessor destination) {
            return this;
        }

        @Override
        public void receive(DataPoint datapoint) {
            // do nothing
        }

        @Override
        public void receive(Object value) {
            // do nothing
        }

        @Override
        public IDataProcessor pipeInto(IDataProcessor processor) {
            return this;
        }




    };

}
