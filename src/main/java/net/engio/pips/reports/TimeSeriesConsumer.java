package net.engio.pips.reports;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.IDataSink;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class TimeSeriesConsumer<N extends Number> implements IDataSink<N>{

    private TimeSeries series;

    private String label;

    public TimeSeriesConsumer(String label) {
       series = new TimeSeries(label);
        this.label = label;
    }

    @Override
    public void receive(DataPoint<N> datapoint) {
        series.addOrUpdate(new FixedMillisecond(datapoint.getTsCreated()), datapoint.getValue());
    }

    @Override
    public void append(N value) {
        receive(new DataPoint<N>(value));
    }

    public TimeSeries getSeries() {
        return series;
    }


    public String getLabel() {
        return label;
    }
}
