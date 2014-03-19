package net.engio.pips.data;

/**
 * A data processor is essentially a combination of sink and source.
 * It produces outgoing data points from incoming data points using any
 * processing logic that might make sense.
 * It is not specified how many outgoing data points are generated for each incoming.
 *
 * Data points may be duplicated, dropped, converted, delayed, stored, aggregated etc.
 *
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataProcessor<IN, OUT> extends IDataSink<IN>,IDataSource<OUT>{

    /**
     * A data processor that acts like /dev/null
     */
    public static final IDataProcessor Void = new IDataProcessor() {
        @Override
        public void receive(DataPoint datapoint) {
            // do nothing
        }

        @Override
        public void append(Object value) {
            // do nothing
        }

        @Override
        public IDataProcessor connectTo(IDataProcessor processor) {
            return this;
        }



        @Override
        public IDataSink connectTo(IDataSink destination) {
            return this;
        }
    };

}
