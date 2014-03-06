package net.engio.imupers.performance.data.utils;

/**
 * @author bennidi
 *         Date: 2/26/14
 */
public class DoubleToLong extends ValueMapper<Double, Long>{

    @Override
    protected Long map(Double aDouble) {
        return aDouble.longValue();
    }
}
