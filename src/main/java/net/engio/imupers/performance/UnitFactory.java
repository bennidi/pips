package net.engio.imupers.performance;

/**
 * A factory that provides an iterator like interface to construct
 * execution units that can be run by the executor
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public interface UnitFactory {

    ExecutionUnit create(ExecutionContext context);

}
