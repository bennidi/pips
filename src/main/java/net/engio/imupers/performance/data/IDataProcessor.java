package net.engio.imupers.performance.data;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public interface IDataProcessor<IN, OUT> extends IDataSink<IN>,IDataSource<OUT>{



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
